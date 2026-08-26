package com.suri.pipsurios.ui.screens

import android.Manifest
import android.content.Intent
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
import com.suri.pipsurios.sonar.*
import com.suri.pipsurios.sonartesting.*
import com.suri.pipsurios.ui.theme.PipAmber
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

    val permissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { retry++ }
    LaunchedEffect(Unit) {
        if (!scanner.hasRequiredPermissions()) permissions.launch(TESTING_BLE_PERMISSIONS)
        tracker.startCalibration(); delay(SonarTuning.CALIBRATION_DURATION_MILLIS); tracker.finishCalibration()
    }
    DisposableEffect(retry) {
        scanStatus = scanner.start({ observation ->
            val contact = tracker.observe(observation)
            contacts = tracker.snapshot().contacts
            if (identifying) identificationObservations.getOrPut(observation.temporaryId) { mutableStateListOf() }.add(observation.rssi)
            if (active && observation.temporaryId == binding) {
                val recovered = if (lastContact == null && live.raw.isNotEmpty()) 1 else 0
                lastContact = contact
                live = live.copy(raw=live.raw+observation.rssi, smooth=live.smooth+contact.smoothedRssi,
                    categories=live.categories+contact.proximity, recovered=live.recovered+recovered)
                recorder.observe(observation, contact, System.currentTimeMillis())
            }
        }, { scanStatus = it })
        onDispose { scanner.stop() }
    }
    DisposableEffect(Unit) { onDispose { recorder.cancel(); scanner.releaseSession() } }
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            tracker.expire(SystemClock.elapsedRealtime()).forEach {
                if (active && it.temporaryId == binding) {
                    recorder.contactExpired(it,System.currentTimeMillis()); lastContact=null; live=live.copy(lost=live.lost+1)
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
                recorder.complete(System.currentTimeMillis());active=false;phase=SonarTestingPhase.RESULT
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
        recorder.start(CalibrationSample(repository.nextSampleId(session.sessionId),session.sessionId,testType,target,position.toCalibrationPosition(),id,now,position.condition))
        live=LiveResult();lastContact=null;elapsed=0;startedElapsed=SystemClock.elapsedRealtime();active=true;phase=SonarTestingPhase.RUNNING
    }
    fun finishMovement(){recorder.complete(System.currentTimeMillis());active=false;phase=SonarTestingPhase.RESULT}
    fun reset(){recorder.cancel();active=false;identifying=false;binding=null;position=ManualCalibrationPosition();target=CalibrationTarget.FLIP_6;testType=CalibrationTestType.STATIC;keepTarget=false;live=LiveResult();session=repository.createSession();phase=SonarTestingPhase.SET_TEST}
    fun nextSample(){position=ManualCalibrationPosition();keepTarget=true;phase=SonarTestingPhase.SET_TEST}
    fun shareCsv(){val file=repository.exportFile(session.sessionId)?:return;val uri=FileProvider.getUriForFile(context,"${context.packageName}.files",file);context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply{type="text/csv";putExtra(Intent.EXTRA_STREAM,uri);addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)},"EXPORT SONAR CSV"))}

    DiagnosticPage(phase,session.sessionId,!active&&!identifying,onBack) {
        when(phase) {
            SonarTestingPhase.SET_TEST -> SetTestPhase(target,{target=it},position,{position=it},testType,{testType=it},keepTarget) {
                identifyUi=IdentifyUi.Instructions;phase=if(binding!=null) SonarTestingPhase.PLACE_TARGET else SonarTestingPhase.IDENTIFY
            }
            SonarTestingPhase.IDENTIFY -> IdentifyPhase(target,identifyUi,scanStatus,::beginIdentification,{identifyUi=IdentifyUi.Found(it)},{candidate->confirm(candidate)}) { phase=SonarTestingPhase.SET_TEST }
            SonarTestingPhase.PLACE_TARGET -> PlaceTargetPhase(target,position,testType,binding in contacts.map{it.temporaryId},::startSample) {binding=null;identifyUi=IdentifyUi.Instructions;phase=SonarTestingPhase.IDENTIFY}
            SonarTestingPhase.RUNNING -> RunningPhase(target,position,testType,elapsed,lastContact,live,::finishMovement)
            SonarTestingPhase.RESULT -> ResultPhase(target,position,live,::shareCsv,::nextSample,::reset) {binding=null;identifyUi=IdentifyUi.Instructions;phase=SonarTestingPhase.IDENTIFY}
        }
    }
}

@Composable private fun DiagnosticPage(phase:SonarTestingPhase,session:String,showBack:Boolean,onBack:()->Unit,content:@Composable BoxScope.()->Unit){
    Box(Modifier.fillMaxSize().background(Color(0xFF101010)).padding(14.dp)){
        Column(Modifier.fillMaxSize()){Text("SONAR TESTING",color=Color.White,fontSize=24.sp,fontFamily=FontFamily.Monospace);Text("${phase.name.replace('_',' ')}  $session",color=PipAmber,fontSize=18.sp,fontFamily=FontFamily.Monospace);Spacer(Modifier.height(8.dp));Box(Modifier.weight(1f),content=content)}
        if(showBack) DButton("BACK",Modifier.align(Alignment.TopEnd),onClick=onBack)
    }
}

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

private fun stats(values:List<Double>)=if(values.isEmpty())"--" else "%.1f / %.1f / %.1f".format(values.min(),values.average(),values.max())
@Composable private fun BigValue(label:String,value:String){Text(label,color=Color.LightGray,fontSize=14.sp);Text(value,color=Color.White,fontSize=23.sp,fontFamily=FontFamily.Monospace);Spacer(Modifier.height(8.dp))}
@Composable private fun MeterField(label:String,value:Int,modifier:Modifier=Modifier,set:(Int)->Unit){OutlinedTextField(if(value==0)"" else value.toString(),{set(it.filter(Char::isDigit).toIntOrNull()?:0)},label={Text(label)},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number),singleLine=true,modifier=modifier)}
@Composable private fun DButton(text:String,modifier:Modifier=Modifier,enabled:Boolean=true,onClick:()->Unit){Text(text,color=if(enabled)Color.White else Color.Gray,fontSize=16.sp,fontFamily=FontFamily.Monospace,modifier=(if(enabled)modifier.clickable(onClick=onClick)else modifier).background(if(enabled)Color(0xFF303030)else Color(0xFF202020)).padding(horizontal=12.dp,vertical=9.dp))}
private val TESTING_BLE_PERMISSIONS=arrayOf(Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT)
