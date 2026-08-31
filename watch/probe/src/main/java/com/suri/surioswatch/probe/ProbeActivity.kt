package com.suri.surioswatch.probe

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable

/** Minimal arming surface. PROBE sends telemetry; it does not render telemetry. */
class ProbeActivity : Activity() {
    private lateinit var statusView: ProbeStatusView
    private val handler = Handler(Looper.getMainLooper())
    private var removeRuntimeObserver: (() -> Unit)? = null
    private var serviceState = ProbeRuntimeSnapshot()
    private var phoneConnected = false
    private var connectionKnown = false
    private var connectionError: String? = null

    private val connectionCheck = object : Runnable {
        override fun run() {
            Wearable.getNodeClient(this@ProbeActivity).connectedNodes
                .addOnSuccessListener(::onConnectedNodes)
                .addOnFailureListener { error ->
                    connectionKnown = true
                    connectionError = error.javaClass.simpleName
                    phoneConnected = false
                    render()
                }
            handler.postDelayed(this, CONNECTION_CHECK_INTERVAL_MILLIS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
        statusView = ProbeStatusView(this)
        setContentView(statusView)
        if (hasPermissions()) {
            readyForPhoneControl()
        } else {
            ActivityCompat.requestPermissions(this, requiredPermissions(), PERMISSION_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PERMISSION_REQUEST) return
        if (hasPermissions()) {
            readyForPhoneControl()
        } else {
            ProbeRuntimeState.update("ERROR", "PERMISSION_REQUIRED")
        }
    }

    override fun onStart() {
        super.onStart()
        removeRuntimeObserver = ProbeRuntimeState.observe {
            runOnUiThread {
                serviceState = it
                render()
            }
        }
        handler.post(connectionCheck)
    }

    override fun onStop() {
        handler.removeCallbacks(connectionCheck)
        removeRuntimeObserver?.invoke()
        removeRuntimeObserver = null
        super.onStop()
    }

    private fun hasPermissions(): Boolean = requiredPermissions().all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun readyForPhoneControl() =
        ProbeRuntimeState.update("READY", "WAITING_FOR_PRS")

    private fun onConnectedNodes(nodes: List<Node>) {
        connectionKnown = true
        connectionError = null
        phoneConnected = nodes.isNotEmpty()
        render()
    }

    private fun render() {
        if (!::statusView.isInitialized) return
        statusView.update(
            serviceState = serviceState.state,
            serviceMessage = serviceState.message,
            phoneConnected = phoneConnected,
            connectionKnown = connectionKnown,
            connectionError = connectionError
        )
    }

    private fun requiredPermissions(): Array<String> = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        add(Manifest.permission.BLUETOOTH_SCAN)
        add(Manifest.permission.BLUETOOTH_CONNECT)
        add(Manifest.permission.POST_NOTIFICATIONS)
    }.toTypedArray()

    private companion object {
        const val PERMISSION_REQUEST = 41
        const val CONNECTION_CHECK_INTERVAL_MILLIS = 2_000L
    }
}
