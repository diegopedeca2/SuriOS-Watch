package com.suri.suriosradar.v2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class RadarV2Activity : ComponentActivity() {
    private val viewModel: RadarV2ViewModel by viewModels()
    private var permissionsGranted by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        permissionsGranted = result.values.all { it }
        viewModel.setPermissionsGranted(permissionsGranted)
        if (permissionsGranted) viewModel.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        permissionsGranted = hasBlePermissions()
        viewModel.setPermissionsGranted(permissionsGranted)
        if (permissionsGranted) viewModel.start()
        setContent {
            RadarV2Screen(
                state = viewModel.uiState.collectAsStateWithLifecycle().value,
                onStart = {
                    if (permissionsGranted) viewModel.start()
                    else permissionLauncher.launch(requiredPermissions())
                },
                onStop = viewModel::stop
            )
        }
    }

    override fun onResume() {
        super.onResume()
        permissionsGranted = hasBlePermissions()
        viewModel.setPermissionsGranted(permissionsGranted)
    }

    private fun hasBlePermissions(): Boolean = requiredPermissions().all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requiredPermissions(): Array<String> = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT
    )
}
