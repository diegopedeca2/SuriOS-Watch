package com.suri.suriosradar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.suri.suriosradar.ui.RadarScreen

class MainActivity : ComponentActivity() {
    private val radarViewModel: RadarViewModel by viewModels()
    private var permissionsGranted by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        permissionsGranted = result.values.all { it }
        radarViewModel.setPermissionsGranted(permissionsGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionsGranted = hasBlePermissions()
        radarViewModel.setPermissionsGranted(permissionsGranted)

        setContent {
            RadarScreen(
                viewModel = radarViewModel,
                permissionsGranted = permissionsGranted,
                onRequestPermissions = { permissionLauncher.launch(requiredPermissions()) }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        permissionsGranted = hasBlePermissions()
        radarViewModel.setPermissionsGranted(permissionsGranted)
    }

    private fun hasBlePermissions(): Boolean {
        return requiredPermissions().all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requiredPermissions(): Array<String> =
        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
}
