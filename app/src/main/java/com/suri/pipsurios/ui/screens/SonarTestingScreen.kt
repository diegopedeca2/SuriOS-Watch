package com.suri.pipsurios.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.suri.pipsurios.remoteprobe.RemoteProbeLink
import com.suri.pipsurios.remoteprobe.RemoteProbeComparator
import com.suri.pipsurios.remoteprobe.RemoteProbeSession
import com.suri.pipsurios.remoteprobe.RemoteProbeSnapshot
import com.suri.pipsurios.sonar.*
import com.suri.pipsurios.sonartesting.*
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlue
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipRed
import kotlinx.coroutines.delay

private data class LiveResult(
    val raw: List<Int> = emptyList(), val smooth: List<Float> = emptyList(),
    val categories: Set<ProximityCategory> = emptySet(), val lost: Int = 0, val recovered: Int = 0
)

private sealed interface IdentifyUi {
    data object Instructions : IdentifyUi
    data class Searching(val secondsLeft: Int) : IdentifyUi
    data class Found(val candidate: IdentificationCandidate) : IdentifyUi
    data class Multiple(val candidates: List<IdentificationCandidate>) : IdentifyUi
    data object NotFound : IdentifyUi
}

@Composable
fun SonarTestingScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scanner = remember { BleScanner(context.applicationContext) }
    val tracker = remember { ContactTracker() }
    val repository = remember { SonarTestingRepository.from(context.applicationContext) }
    var session by remember { mutableStateOf(repository.createSession()) }
    val recorder = remember { SonarTestingRecorder(repository::append) }
    var phase by remember { mutableStateOf(SonarTestingPhase.SET_TEST) }
    var target by remember { mutableStateOf(CalibrationTarget.FLIP_6) }
    var position by remember { mutableStateOf(ManualCalibrationPosition()) }
    var testType by remember { mutableStateOf(CalibrationTestType.STATIC) }
    var nodeMode by remember { mutableStateOf(TestingNodeMode.A56_ONLY) }
    var environment by remember { mutableStateOf(FieldEnvironment.OPEN_FIELD) }
    var placement by remember { mutableStateOf(DevicePlacement.IN_HAND) }
    var orientation by remember { mutableStateOf(DeviceOrientation.FACING_PHONE) }
    var siteName by remember { mutableStateOf("NAVY7") }
    var fieldNotes by remember { mutableStateOf("") }
    var binding by remember { mutableStateOf<String?>(null) }
    var keepTarget by remember { mutableStateOf(false) }
    var identifyUi by remember { mutableStateOf<IdentifyUi>(IdentifyUi.Instructions) }
    var identifying by remember { mutableStateOf(false) }
    val identificationObservations = remember { mutableStateMapOf<String, MutableList<Int>>() }
    var baselineIds by remember { mutableStateOf(emptySet<String>()) }
    var contacts by remember { mutableStateOf(emptyList<SonarContact>()) }
    var scanStatus by remember { mutableStateOf(BleScanStatus.IDLE) }
    var retry by remember { mutableIntStateOf(0) }
    var active by remember { mutableStateOf(false) }
    var elapsed by remember { mutableLongStateOf(0L) }
    var startedElapsed by remember { mutableLongStateOf(0L) }
    var lastContact by remember { mutableStateOf<SonarContact?>(null) }
    var live by remember { mutableStateOf(LiveResult()) }
    val remoteProbe = remember { RemoteProbeSession(context.applicationContext) }
    var remoteSnapshot by remember { mutableStateOf(RemoteProbeSnapshot()) }
    var probeRssiSamples by remember { mutableStateOf(emptyList<Int>()) }
    var lastProbeComparisonTimestamp by remember { mutableLongStateOf(0L) }
    val deviceIdentifiers = remember { mutableStateMapOf<String, String>() }
    fun currentProbeLink(): String? = if (nodeMode.usesRemoteProbe) remoteSnapshot.link.name else "NOT_USED"

    val permissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { retry++ }
    LaunchedEffect(Unit) {
        tracker.startCalibration(); delay(SonarTuning.CALIBRATION_DURATION_MILLIS); tracker.finishCalibration()
    }
    LaunchedEffect(nodeMode) {
        if (!scanner.hasRequiredPermissions() || (nodeMode.usesRemoteProbe && !hasTestingLocalNetworkPermission(context))) {
            permissions.launch(testingPermissions(nodeMode))
        }
    }
    DisposableEffect(retry, nodeMode) {
        scanStatus = scanner.start({ observation ->
            deviceIdentifiers[observation.temporaryId] = observation.deviceIdentifier
            val contact = tracker.observe(observation)
            contacts = tracker.snapshot().contacts
            if (identifying) identificationObservations.getOrPut(observation.temporaryId) { mutableStateListOf() }.add(observation.rssi)
            if (active && observation.temporaryId == binding) {
                val recovered = if (lastContact == null && live.raw.isNotEmpty()) 1 else 0
                lastContact = contact
                live = live.copy(raw=live.raw+observation.rssi, smooth=live.smooth+contact.smoothedRssi,
                    categories=live.categories+contact.proximity, recovered=live.recovered+recovered)
                recorder.observe(
                    observation,
                    contact,
                    System.currentTimeMillis(),
                    currentProbeLink(),
                    probeRssiSamples.averageRssi(),
                    probeRssiSamples.size
                )
            }
            if (nodeMode.usesRemoteProbe) remoteProbe.observeOperator(observation)
        }, { scanStatus = it })
        onDispose { scanner.stop() }
    }
    DisposableEffect(Unit) {
        onDispose {
            recorder.cancel()
            remoteProbe.stop()
            scanner.releaseSession()
        }
    }
    LaunchedEffect(nodeMode, retry) {
        remoteProbe.stop()
        remoteSnapshot = remoteProbe.snapshot()
        if (!nodeMode.usesRemoteProbe || !scanner.hasRequiredPermissions() || !hasTestingLocalNetworkPermission(context)) return@LaunchedEffect
        remoteProbe.start()
    }
    LaunchedEffect(remoteProbe, nodeMode, active, binding) {
        while (true) {
            val nextSnapshot = remoteProbe.snapshot()
            remoteSnapshot = nextSnapshot
            if (nodeMode.usesRemoteProbe && active) {
                val targetIdentifier = binding?.let { deviceIdentifiers[it] }
                nextSnapshot.comparisons
                    .firstOrNull { it.deviceIdentifier == targetIdentifier }
                    ?.let { comparison ->
                        if (comparison.probeTimestampEpochMillis > lastProbeComparisonTimestamp) {
                            lastProbeComparisonTimestamp = comparison.probeTimestampEpochMillis
                            probeRssiSamples = (probeRssiSamples + comparison.probeRssi).takeLast(MAX_PROBE_RSSI_SAMPLES)
                        }
                    }
            }
            delay(500L)
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            tracker.expire(SystemClock.elapsedRealtime()).forEach {
                if (active && it.temporaryId == binding) {
                    recorder.contactExpired(
                        it,
                        System.currentTimeMillis(),
                        currentProbeLink(),
                        probeRssiSamples.averageRssi(),
                        probeRssiSamples.size
                    )
                    lastContact=null
                    live=live.copy(lost=live.lost+1)
                }
            }
            contacts=tracker.snapshot().contacts
        }
    }
    LaunchedEffect(identifying) {
        if (!identifying) return@LaunchedEffect
        val ticks=(SonarIdentificationTuning.WINDOW_MILLIS/1_000L).toInt()
        for (remaining in ticks downTo 1) { identifyUi=IdentifyUi.Searching(remaining); delay(1_000) }
        identifying=false
        identifyUi=when(val result=SonarTargetIdentifier.identify(identificationObservations.mapValues{it.value.toList()},baselineIds)) {
            is IdentificationResult.Dominant -> IdentifyUi.Found(result.candidate)
            is IdentificationResult.Ambiguous -> IdentifyUi.Multiple(result.candidates)
            IdentificationResult.None -> IdentifyUi.NotFound
        }
    }
    LaunchedEffect(active,testType) {
        while(active) {
            elapsed=SystemClock.elapsedRealtime()-startedElapsed
            if(testType==CalibrationTestType.STATIC && elapsed>=SonarTestingTuning.STATIC_SAMPLE_DURATION_MILLIS) {
                recorder.complete(
                    System.currentTimeMillis(),
                    currentProbeLink(),
                    probeRssiSamples.averageRssi(),
                    probeRssiSamples.size
                );active=false;phase=SonarTestingPhase.RESULT
            }
            delay(100)
        }
    }

    fun beginIdentification() {
        identificationObservations.clear();baselineIds=contacts.mapTo(mutableSetOf()){it.temporaryId};identifyUi=IdentifyUi.Searching(5);identifying=true
    }
    fun confirm(candidate:IdentificationCandidate) { binding=candidate.temporaryId;identifying=false;phase=SonarTestingPhase.PLACE_TARGET }
    fun startSample() {
        val id=binding?:return;val now=System.currentTimeMillis()
        recorder.start(
            CalibrationSample(
                sampleId = repository.nextSampleId(session.sessionId),
                sessionId = session.sessionId,
                type = testType,
                target = target,
                position = position.toCalibrationPosition(),
                temporaryContactId = id,
                startedAtEpochMillis = now,
                notes = position.condition,
                nodeMode = nodeMode,
                probeSessionId = remoteSnapshot.sessionId
            )
        )
        live=LiveResult();probeRssiSamples=emptyList();lastProbeComparisonTimestamp=0L;lastContact=null;elapsed=0;startedElapsed=SystemClock.elapsedRealtime();active=true;phase=SonarTestingPhase.RUNNING
    }
    fun finishMovement(){recorder.complete(System.currentTimeMillis(), currentProbeLink(), probeRssiSamples.averageRssi(), probeRssiSamples.size);active=false;phase=SonarTestingPhase.RESULT}
    fun reset(){recorder.cancel();active=false;identifying=false;binding=null;position=ManualCalibrationPosition();target=CalibrationTarget.FLIP_6;testType=CalibrationTestType.STATIC;nodeMode=TestingNodeMode.A56_ONLY;environment=FieldEnvironment.OPEN_FIELD;placement=DevicePlacement.IN_HAND;orientation=DeviceOrientation.FACING_PHONE;siteName="NAVY7";fieldNotes="";keepTarget=false;live=LiveResult();deviceIdentifiers.clear();session=repository.createSession();phase=SonarTestingPhase.SET_TEST}
    fun nextSample(){position=ManualCalibrationPosition();fieldNotes="";keepTarget=true;phase=SonarTestingPhase.SET_TEST}
    fun shareCsv(){val file=repository.exportFile(session.sessionId)?:return;val uri=FileProvider.getUriForFile(context,"${context.packageName}.files",file);context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply{type="text/csv";putExtra(Intent.EXTRA_STREAM,uri);addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)},"EXPORT P.R.S. CSV"))}

    val targetDeviceIdentifier = binding?.let { deviceIdentifiers[it] }

    DiagnosticPage(phase,session.sessionId,!active&&!identifying,onBack) {
        when(phase) {
            SonarTestingPhase.SET_TEST -> FieldSetupPhase(target,{target=it},position,{position=it},testType,{testType=it},nodeMode,{nodeMode=it},environment,{environment=it},placement,{placement=it},orientation,{orientation=it},siteName,{siteName=it},fieldNotes,{fieldNotes=it},keepTarget,remoteSnapshot) {
                position=position.copy(condition=fieldCondition(siteName,environment,placement,orientation,fieldNotes))
                identifyUi=IdentifyUi.Instructions;phase=if(binding!=null) SonarTestingPhase.PLACE_TARGET else SonarTestingPhase.IDENTIFY
            }
            SonarTestingPhase.IDENTIFY -> IdentifyPhase(target,identifyUi,scanStatus,nodeMode,remoteSnapshot,::beginIdentification,{identifyUi=IdentifyUi.Found(it)},{candidate->confirm(candidate)}) { phase=SonarTestingPhase.SET_TEST }
            SonarTestingPhase.PLACE_TARGET -> PlaceTargetPhase(target,position,testType,nodeMode,remoteSnapshot,binding in contacts.map{it.temporaryId},::startSample) {binding=null;identifyUi=IdentifyUi.Instructions;phase=SonarTestingPhase.IDENTIFY}
            SonarTestingPhase.RUNNING -> RunningPhase(target,position,testType,nodeMode,remoteSnapshot,targetDeviceIdentifier,probeRssiSamples,elapsed,lastContact,live,::finishMovement)
            SonarTestingPhase.RESULT -> ResultPhase(target,position,nodeMode,remoteSnapshot,probeRssiSamples,live,::shareCsv,::nextSample,::reset) {binding=null;identifyUi=IdentifyUi.Instructions;phase=SonarTestingPhase.IDENTIFY}
        }
    }
}

@Composable private fun DiagnosticPage(phase:SonarTestingPhase,session:String,showBack:Boolean,onBack:()->Unit,content:@Composable BoxScope.()->Unit){
    Box(Modifier.fillMaxSize().background(Color(0xFF101010)).padding(14.dp)){
        Column(Modifier.fillMaxSize()){Text("P.R.S. TESTING",color=Color.White,fontSize=24.sp,fontFamily=FontFamily.Monospace);Text("${phase.name.replace('_',' ')}  $session",color=PipAmber,fontSize=18.sp,fontFamily=FontFamily.Monospace);Spacer(Modifier.height(8.dp));Box(Modifier.weight(1f),content=content)}
        if(showBack) DButton("BACK",Modifier.align(Alignment.TopEnd),onClick=onBack)
    }
}

@Composable
private fun FieldSetupPhase(
    target: CalibrationTarget,
    setTarget: (CalibrationTarget) -> Unit,
    position: ManualCalibrationPosition,
    setPosition: (ManualCalibrationPosition) -> Unit,
    type: CalibrationTestType,
    setType: (CalibrationTestType) -> Unit,
    nodeMode: TestingNodeMode,
    setNodeMode: (TestingNodeMode) -> Unit,
    environment: FieldEnvironment,
    setEnvironment: (FieldEnvironment) -> Unit,
    placement: DevicePlacement,
    setPlacement: (DevicePlacement) -> Unit,
    orientation: DeviceOrientation,
    setOrientation: (DeviceOrientation) -> Unit,
    siteName: String,
    setSiteName: (String) -> Unit,
    notes: String,
    setNotes: (String) -> Unit,
    targetLocked: Boolean,
    remoteSnapshot: RemoteProbeSnapshot,
    next: () -> Unit
) {
    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        Column(Modifier.width(250.dp).verticalScroll(rememberScrollState())) {
            Text("TARGET", color = Color.LightGray, fontSize = 16.sp)
            if (targetLocked) {
                BigValue("CURRENT TARGET", target.label)
            } else {
                CalibrationTarget.entries.forEach {
                    DButton("${if (it == target) "[X]" else "[ ]"} ${it.label}") { setTarget(it) }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("TEST TYPE", color = Color.LightGray, fontSize = 14.sp)
            DButton("STATIC ${if (type == CalibrationTestType.STATIC) "[X]" else "[ ]"}") { setType(CalibrationTestType.STATIC) }
            DButton("MOVEMENT ${if (type == CalibrationTestType.MOVEMENT) "[X]" else "[ ]"}") { setType(CalibrationTestType.MOVEMENT) }
            Spacer(Modifier.height(8.dp))
            Text("DETECTION NODES", color = Color.LightGray, fontSize = 14.sp)
            TestingNodeMode.entries.forEach { option ->
                DButton("${if (option == nodeMode) "[X]" else "[ ]"} ${option.label}") { setNodeMode(option) }
            }
            if (nodeMode.usesRemoteProbe) {
                Text("● WATCH 2 = REMOTE SENSOR", color = PipBlue, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                if (nodeMode.usesRemoteProbe) "PROBE LINK: ${remoteSnapshot.link.name}" else "PROBE LINK: NOT USED",
                color = if (nodeMode.usesRemoteProbe && remoteSnapshot.link == RemoteProbeLink.CONNECTED) PipGreen else PipGreenDim,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
            if (nodeMode.usesRemoteProbe) {
                Text("WATCH CONTACTS: ${remoteSnapshot.probeContactCount}", color = PipGreenDim, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("MATCHED: ${remoteSnapshot.matchedContactCount}", color = PipGreenDim, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("A DISCONNECTED WATCH IS A VALID NEGATIVE TEST", color = Color.Gray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            }
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Text(if (targetLocked) "NEW MEASUREMENT" else "MEASUREMENT SET-UP", color = Color.White, fontSize = 22.sp)
            Text("Choose the distance and describe the test so the exported file can be compared later.", color = Color.Gray, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MeterField("NORTH", position.northMeters, Modifier.weight(1f)) { setPosition(position.copy(northMeters = it)) }
                MeterField("SOUTH", position.southMeters, Modifier.weight(1f)) { setPosition(position.copy(southMeters = it)) }
                MeterField("EAST", position.eastMeters, Modifier.weight(1f)) { setPosition(position.copy(eastMeters = it)) }
                MeterField("WEST", position.westMeters, Modifier.weight(1f)) { setPosition(position.copy(westMeters = it)) }
            }
            Text("POINT: ${position.display()}", color = PipAmber, fontSize = 20.sp)
            Text("QUICK DISTANCE", color = Color.LightGray, fontSize = 14.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1, 2, 5).forEach { distance ->
                    DButton("${distance}M") {
                        setPosition(position.copy(northMeters = distance, southMeters = 0, eastMeters = 0, westMeters = 0))
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(10, 15, 20).forEach { distance ->
                    DButton("${distance}M") {
                        setPosition(position.copy(northMeters = distance, southMeters = 0, eastMeters = 0, westMeters = 0))
                    }
                }
            }
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                siteName,
                setSiteName,
                label = { Text("FIELD SITE") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("FIELD CONDITION", color = Color.LightGray, fontSize = 14.sp)
            FieldEnvironment.entries.forEach { option ->
                DButton("${if (option == environment) "[X]" else "[ ]"} ${option.label}") { setEnvironment(option) }
            }
            Spacer(Modifier.height(6.dp))
            Text("DEVICE POSITION", color = Color.LightGray, fontSize = 14.sp)
            DevicePlacement.entries.forEach { option ->
                DButton("${if (option == placement) "[X]" else "[ ]"} ${option.label}") { setPlacement(option) }
            }
            Spacer(Modifier.height(6.dp))
            Text("DEVICE ORIENTATION", color = Color.LightGray, fontSize = 14.sp)
            DeviceOrientation.entries.forEach { option ->
                DButton("${if (option == orientation) "[X]" else "[ ]"} ${option.label}") { setOrientation(option) }
            }
            OutlinedTextField(
                notes,
                setNotes,
                label = { Text("EXTRA NOTES") },
                singleLine = false,
                modifier = Modifier.fillMaxWidth()
            )
            Text("RECORDED AS: ${fieldCondition(siteName, environment, placement, orientation, notes)}", color = PipAmber, fontSize = 13.sp)
            if (!position.isValid) Text("CHOOSE ONLY ONE DIRECTION ON EACH AXIS", color = PipRed, fontSize = 14.sp)
            DButton("NEXT", enabled = position.isValid, onClick = next)
        }
    }
}

private fun fieldCondition(
    siteName: String,
    environment: FieldEnvironment,
    placement: DevicePlacement,
    orientation: DeviceOrientation,
    notes: String
): String = listOf(siteName.trim(), environment.label, placement.label, orientation.label, notes.trim())
    .filter { it.isNotEmpty() }
    .joinToString(" | ")

@Composable private fun SetTestPhase(target:CalibrationTarget,setTarget:(CalibrationTarget)->Unit,position:ManualCalibrationPosition,setPosition:(ManualCalibrationPosition)->Unit,type:CalibrationTestType,setType:(CalibrationTestType)->Unit,targetLocked:Boolean,next:()->Unit){
    Row(Modifier.fillMaxSize(),horizontalArrangement=Arrangement.spacedBy(20.dp)){
        Column(Modifier.width(235.dp).verticalScroll(rememberScrollState())){Text("TARGET",color=Color.LightGray,fontSize=16.sp);if(targetLocked) BigValue("CURRENT TARGET",target.label) else CalibrationTarget.entries.forEach{DButton("${if(it==target) "[X]" else "[ ]"} ${it.label}"){setTarget(it)}};Spacer(Modifier.height(8.dp));Text("TEST TYPE",color=Color.LightGray,fontSize=14.sp);DButton("STATIC ${if(type==CalibrationTestType.STATIC) "[X]" else ""}"){setType(CalibrationTestType.STATIC)};DButton("MOVEMENT ${if(type==CalibrationTestType.MOVEMENT) "[X]" else ""}"){setType(CalibrationTestType.MOVEMENT)}}
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())){Text(if(targetLocked)"NEW POSITION" else "POSITION",color=Color.White,fontSize=22.sp);Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){MeterField("NORTH",position.northMeters,Modifier.weight(1f)){setPosition(position.copy(northMeters=it))};MeterField("SOUTH",position.southMeters,Modifier.weight(1f)){setPosition(position.copy(southMeters=it))};MeterField("EAST",position.eastMeters,Modifier.weight(1f)){setPosition(position.copy(eastMeters=it))};MeterField("WEST",position.westMeters,Modifier.weight(1f)){setPosition(position.copy(westMeters=it))}};Text("POSITION: ${position.display()}",color=PipAmber,fontSize=20.sp);OutlinedTextField(position.condition,{setPosition(position.copy(condition=it))},label={Text("NOTES")},singleLine=true,modifier=Modifier.fillMaxWidth());if(!position.isValid)Text("CHOOSE ONLY NORTH OR SOUTH, AND EAST OR WEST",color=PipRed,fontSize=14.sp);DButton("NEXT",enabled=position.isValid,onClick=next)}
    }
}

@Composable private fun IdentifyPhase(target:CalibrationTarget,state:IdentifyUi,status:BleScanStatus,start:()->Unit,select:(IdentificationCandidate)->Unit,confirm:(IdentificationCandidate)->Unit,back:()->Unit){
    Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){BigValue("TARGET",target.label);when(state){IdentifyUi.Instructions->{Text("PLACE ${target.label} NEXT TO SURI-14",color=Color.White,fontSize=26.sp);Text("Bluetooth: ${status.name}",color=Color.Gray,fontSize=13.sp);DButton("START IDENTIFICATION",onClick=start)};is IdentifyUi.Searching->{Text("IDENTIFYING TARGET",color=Color.White,fontSize=28.sp);Text("${state.secondsLeft}",color=PipAmber,fontSize=48.sp)};is IdentifyUi.Found->{Text("TARGET FOUND",color=Color.White,fontSize=28.sp);BigValue("SIGNAL",if(state.candidate.averageRssi>=SonarIdentificationTuning.STRONG_RSSI_THRESHOLD)"STRONG" else "DETECTED");DButton("CONFIRM"){confirm(state.candidate)}};is IdentifyUi.Multiple->{Text("MULTIPLE DEVICES FOUND",color=Color.White,fontSize=25.sp);Row(horizontalArrangement=Arrangement.spacedBy(14.dp)){state.candidates.forEachIndexed{i,c->DButton("DEVICE ${'A'+i}\nRSSI ${c.averageRssi.toInt()}"){select(c)}}}};IdentifyUi.NotFound->{Text("TARGET NOT FOUND",color=PipRed,fontSize=28.sp);Row(horizontalArrangement=Arrangement.spacedBy(16.dp)){DButton("RETRY",onClick=start);DButton("BACK",onClick=back)}}}}
}

@Composable private fun PlaceTargetPhase(target:CalibrationTarget,position:ManualCalibrationPosition,type:CalibrationTestType,bindingVisible:Boolean,start:()->Unit,reidentify:()->Unit){Column(Modifier.fillMaxSize(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Text("TARGET IDENTIFIED",color=Color.White,fontSize=26.sp);BigValue("TARGET",target.label);Text("NOW PLACE TARGET AT",color=Color.LightGray,fontSize=18.sp);Text(position.display(),color=PipAmber,fontSize=34.sp,fontFamily=FontFamily.Monospace);if(!bindingVisible)Text("SIGNAL CURRENTLY LOST",color=PipRed,fontSize=14.sp);DButton(if(type==CalibrationTestType.STATIC)"START SAMPLE" else "START MOVEMENT",onClick=start);DButton("RE-IDENTIFY TARGET",onClick=reidentify)}}

@Composable private fun RunningPhase(target:CalibrationTarget,position:ManualCalibrationPosition,type:CalibrationTestType,elapsed:Long,contact:SonarContact?,live:LiveResult,stop:()->Unit){val remaining=((SonarTestingTuning.STATIC_SAMPLE_DURATION_MILLIS-elapsed).coerceAtLeast(0)+999)/1000;Row(Modifier.fillMaxSize(),horizontalArrangement=Arrangement.SpaceEvenly,verticalAlignment=Alignment.CenterVertically){Column{BigValue("TARGET",target.label);BigValue("POSITION",position.display());BigValue("TIME",if(type==CalibrationTestType.STATIC)"$remaining s" else "${elapsed/1000} s")};Column{BigValue("RAW RSSI",contact?.currentRssi?.toString()?:"--");BigValue("SMOOTHED",contact?.let{"%.1f".format(it.smoothedRssi)}?:"--");BigValue("CATEGORY",contact?.proximity?.name?.replace('_',' ')?:"--")};Column{BigValue("LOST",live.lost.toString());BigValue("RECOVERED",live.recovered.toString());if(type==CalibrationTestType.MOVEMENT)DButton("STOP MOVEMENT",onClick=stop)}}}

@Composable private fun ResultPhase(target:CalibrationTarget,position:ManualCalibrationPosition,result:LiveResult,share:()->Unit,next:()->Unit,reset:()->Unit,reidentify:()->Unit){Row(Modifier.fillMaxSize(),horizontalArrangement=Arrangement.SpaceEvenly,verticalAlignment=Alignment.CenterVertically){Column{Text("SAMPLE COMPLETE",color=PipAmber,fontSize=25.sp);BigValue("TARGET",target.label);BigValue("POSITION",position.display());BigValue("SAMPLES",result.raw.size.toString());BigValue("LOST / RECOVERED","${result.lost} / ${result.recovered}")};Column{BigValue("RAW MIN / AVG / MAX",stats(result.raw.map{it.toDouble()}));BigValue("SMOOTHED MIN / AVG / MAX",stats(result.smooth.map{it.toDouble()}))};Column{DButton("NEXT SAMPLE",onClick=next);DButton("EXPORT CSV",onClick=share);DButton("RE-IDENTIFY TARGET",onClick=reidentify);DButton("RESET TEST",onClick=reset)}}}

@Composable
private fun IdentifyPhase(
    target: CalibrationTarget,
    state: IdentifyUi,
    status: BleScanStatus,
    nodeMode: TestingNodeMode,
    remoteSnapshot: RemoteProbeSnapshot,
    start: () -> Unit,
    select: (IdentificationCandidate) -> Unit,
    confirm: (IdentificationCandidate) -> Unit,
    back: () -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BigValue("TARGET", target.label)
        BigValue("NODES", if (nodeMode.usesRemoteProbe) "A56 + WATCH 2" else "A56 ONLY")
        if (nodeMode.usesRemoteProbe) {
            Text(
                "PROBE LINK: ${remoteSnapshot.link.name}  WATCH: ${remoteSnapshot.probeContactCount}  MATCHED: ${remoteSnapshot.matchedContactCount}",
                color = if (remoteSnapshot.link == RemoteProbeLink.CONNECTED) PipGreen else PipGreenDim,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        when (state) {
            IdentifyUi.Instructions -> {
                Text("PLACE ${target.label} NEXT TO SURI-14", color = Color.White, fontSize = 26.sp)
                Text("Bluetooth: ${status.name}", color = Color.Gray, fontSize = 13.sp)
                DButton("START IDENTIFICATION", onClick = start)
            }
            is IdentifyUi.Searching -> {
                Text("IDENTIFYING TARGET", color = Color.White, fontSize = 28.sp)
                Text("${state.secondsLeft}", color = PipAmber, fontSize = 48.sp)
            }
            is IdentifyUi.Found -> {
                Text("TARGET FOUND", color = Color.White, fontSize = 28.sp)
                BigValue(
                    "SIGNAL",
                    if (state.candidate.averageRssi >= SonarIdentificationTuning.STRONG_RSSI_THRESHOLD) "STRONG" else "DETECTED"
                )
                DButton("CONFIRM") { confirm(state.candidate) }
            }
            is IdentifyUi.Multiple -> {
                Text("MULTIPLE DEVICES FOUND", color = Color.White, fontSize = 25.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    state.candidates.forEachIndexed { index, candidate ->
                        DButton("DEVICE ${'A' + index}\nRSSI ${candidate.averageRssi.toInt()}") { select(candidate) }
                    }
                }
            }
            IdentifyUi.NotFound -> {
                Text("TARGET NOT FOUND", color = PipRed, fontSize = 28.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DButton("RETRY", onClick = start)
                    DButton("BACK", onClick = back)
                }
            }
        }
    }
}

@Composable
private fun PlaceTargetPhase(
    target: CalibrationTarget,
    position: ManualCalibrationPosition,
    type: CalibrationTestType,
    nodeMode: TestingNodeMode,
    remoteSnapshot: RemoteProbeSnapshot,
    bindingVisible: Boolean,
    start: () -> Unit,
    reidentify: () -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("TARGET IDENTIFIED", color = Color.White, fontSize = 26.sp)
        BigValue("TARGET", target.label)
        BigValue("NODES", if (nodeMode.usesRemoteProbe) "A56 + WATCH 2" else "A56 ONLY")
        Text("NOW PLACE TARGET AT", color = Color.LightGray, fontSize = 18.sp)
        Text(position.display(), color = PipAmber, fontSize = 34.sp, fontFamily = FontFamily.Monospace)
        if (nodeMode.usesRemoteProbe) {
            Text(
                "PROBE LINK: ${remoteSnapshot.link.name}  WATCH CONTACTS: ${remoteSnapshot.probeContactCount}",
                color = if (remoteSnapshot.link == RemoteProbeLink.CONNECTED) PipGreen else PipGreenDim,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        if (!bindingVisible) Text("SIGNAL CURRENTLY LOST", color = PipRed, fontSize = 14.sp)
        DButton(if (type == CalibrationTestType.STATIC) "START SAMPLE" else "START MOVEMENT", onClick = start)
        DButton("RE-IDENTIFY TARGET", onClick = reidentify)
    }
}

@Composable
private fun RunningPhase(
    target: CalibrationTarget,
    position: ManualCalibrationPosition,
    type: CalibrationTestType,
    nodeMode: TestingNodeMode,
    remoteSnapshot: RemoteProbeSnapshot,
    targetDeviceIdentifier: String?,
    probeRssiSamples: List<Int>,
    elapsed: Long,
    contact: SonarContact?,
    live: LiveResult,
    stop: () -> Unit
) {
    val remaining = ((SonarTestingTuning.STATIC_SAMPLE_DURATION_MILLIS - elapsed).coerceAtLeast(0) + 999) / 1000
    val comparison = remoteSnapshot.comparisons.firstOrNull { it.deviceIdentifier == targetDeviceIdentifier }
    val probeAverage = probeRssiSamples.averageRssi()
    Row(
        Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            BigValue("TARGET", target.label)
            BigValue("POSITION", position.display())
            BigValue("NODES", if (nodeMode.usesRemoteProbe) "A56 + WATCH 2" else "A56 ONLY")
            BigValue("TIME", if (type == CalibrationTestType.STATIC) "$remaining s" else "${elapsed / 1000} s")
        }
        Column {
            BigValue("A56 RAW RSSI", contact?.currentRssi?.toString() ?: "--")
            BigValue("A56 SMOOTHED", contact?.let { "%.1f".format(it.smoothedRssi) } ?: "--")
            BigValue("CATEGORY", contact?.proximity?.name?.replace('_', ' ') ?: "--")
        }
        Column {
            if (nodeMode.usesRemoteProbe) {
                BigValue("WATCH RSSI AVG", probeAverage?.toString() ?: "--")
                BigValue("LATEST DELTA / RESULT", comparison?.let { "${it.probeRssi - it.operatorRssi} dB / ${it.assessment.name}" } ?: "--")
                BigValue("WATCH SAMPLES", probeRssiSamples.size.toString())
                BigValue("PROBE LINK", remoteSnapshot.link.name)
                BigValue("MATCHED", remoteSnapshot.matchedContactCount.toString())
            } else {
                BigValue("WATCH", "NOT USED")
            }
            BigValue("LOST / RECOVERED", "${live.lost} / ${live.recovered}")
            if (type == CalibrationTestType.MOVEMENT) DButton("STOP MOVEMENT", onClick = stop)
        }
    }
}

@Composable
private fun ResultPhase(
    target: CalibrationTarget,
    position: ManualCalibrationPosition,
    nodeMode: TestingNodeMode,
    remoteSnapshot: RemoteProbeSnapshot,
    probeRssiSamples: List<Int>,
    result: LiveResult,
    share: () -> Unit,
    next: () -> Unit,
    reset: () -> Unit,
    reidentify: () -> Unit
) {
    val operatorAverage = result.raw.averageRssi()
    val probeAverage = probeRssiSamples.averageRssi()
    val sampleAssessment = if (operatorAverage != null && probeAverage != null) {
        RemoteProbeComparator.classify(operatorAverage, probeAverage).name
    } else {
        "UNCERTAIN / NO MATCH"
    }
    Row(
        Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("SAMPLE COMPLETE", color = PipAmber, fontSize = 25.sp)
            BigValue("TARGET", target.label)
            BigValue("POSITION", position.display())
            BigValue("NODES", if (nodeMode.usesRemoteProbe) "A56 + WATCH 2" else "A56 ONLY")
            BigValue("SAMPLES", result.raw.size.toString())
            BigValue("LOST / RECOVERED", "${result.lost} / ${result.recovered}")
        }
        Column {
            BigValue("A56 RAW MIN / AVG / MAX", stats(result.raw.map { it.toDouble() }))
            BigValue("A56 SMOOTHED MIN / AVG / MAX", stats(result.smooth.map { it.toDouble() }))
        }
        Column {
            if (nodeMode.usesRemoteProbe) {
                BigValue("WATCH CONTACTS", remoteSnapshot.probeContactCount.toString())
                BigValue("WATCH RSSI AVG", probeRssiSamples.averageRssi()?.toString() ?: "--")
                BigValue("WATCH MIN / AVG / MAX", stats(probeRssiSamples.map { it.toDouble() }))
                BigValue("WATCH SAMPLES", probeRssiSamples.size.toString())
                BigValue("MATCHED", remoteSnapshot.matchedContactCount.toString())
                BigValue("AVG DELTA", if (operatorAverage != null && probeAverage != null) "${probeAverage - operatorAverage} dB" else "--")
                BigValue("RESULT", sampleAssessment)
                BigValue("PROBE LINK", remoteSnapshot.link.name)
                BigValue("PROBE SESSION", remoteSnapshot.sessionId ?: "--")
            } else {
                BigValue("WATCH", "NOT USED")
            }
            DButton("NEXT SAMPLE", onClick = next)
            DButton("EXPORT CSV", onClick = share)
            DButton("RE-IDENTIFY TARGET", onClick = reidentify)
            DButton("RESET TEST", onClick = reset)
        }
    }
}

private const val MAX_PROBE_RSSI_SAMPLES = 120
private fun List<Int>.averageRssi(): Int? = if (isEmpty()) null else average().toInt()
private fun stats(values:List<Double>)=if(values.isEmpty())"--" else "%.1f / %.1f / %.1f".format(values.min(),values.average(),values.max())
@Composable private fun BigValue(label:String,value:String){Text(label,color=Color.LightGray,fontSize=14.sp);Text(value,color=Color.White,fontSize=23.sp,fontFamily=FontFamily.Monospace);Spacer(Modifier.height(8.dp))}
@Composable private fun MeterField(label:String,value:Int,modifier:Modifier=Modifier,set:(Int)->Unit){OutlinedTextField(if(value==0)"" else value.toString(),{set(it.filter(Char::isDigit).toIntOrNull()?:0)},label={Text(label)},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number),singleLine=true,modifier=modifier)}
@Composable private fun DButton(text:String,modifier:Modifier=Modifier,enabled:Boolean=true,onClick:()->Unit){Text(text,color=if(enabled)Color.White else Color.Gray,fontSize=16.sp,fontFamily=FontFamily.Monospace,modifier=(if(enabled)modifier.clickable(onClick=onClick)else modifier).background(if(enabled)Color(0xFF303030)else Color(0xFF202020)).padding(horizontal=12.dp,vertical=9.dp))}
private fun testingPermissions(nodeMode: TestingNodeMode): Array<String> = buildList {
    add(Manifest.permission.BLUETOOTH_SCAN)
    add(Manifest.permission.BLUETOOTH_CONNECT)
    add(Manifest.permission.ACCESS_FINE_LOCATION)
    if (nodeMode.usesRemoteProbe && Build.VERSION.SDK_INT >= 37) {
        add("android.permission.ACCESS_LOCAL_NETWORK")
    }
}.toTypedArray()

private fun hasTestingLocalNetworkPermission(context: android.content.Context): Boolean =
    Build.VERSION.SDK_INT < 37 ||
        context.checkSelfPermission("android.permission.ACCESS_LOCAL_NETWORK") ==
        android.content.pm.PackageManager.PERMISSION_GRANTED
