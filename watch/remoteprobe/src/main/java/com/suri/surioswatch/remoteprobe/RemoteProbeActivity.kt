package com.suri.surioswatch.remoteprobe

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View

class RemoteProbeActivity : Activity() {
    private lateinit var probeView: RemoteProbeView
    private val refresh = object : Runnable {
        override fun run() {
            probeView.invalidate()
            probeView.postDelayed(this, 500L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        probeView = RemoteProbeView(this) { stopProbe() }
        setContentView(probeView)
        if (missingPermissions().isEmpty()) startProbe()
        else requestPermissions(missingPermissions(), PERMISSION_REQUEST)
    }

    override fun onResume() {
        super.onResume()
        probeView.post(refresh)
    }

    override fun onPause() {
        probeView.removeCallbacks(refresh)
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startProbe()
        } else {
            RemoteProbeState.scanStatus = WatchBleScanStatus.PERMISSION_REQUIRED
            RemoteProbeState.link = WatchProbeLink.ERROR
        }
    }

    private fun startProbe() {
        startForegroundService(Intent(this, RemoteProbeService::class.java).setAction(RemoteProbeService.ACTION_START))
    }

    private fun stopProbe() {
        startService(Intent(this, RemoteProbeService::class.java).setAction(RemoteProbeService.ACTION_STOP))
    }

    private fun missingPermissions(): Array<String> = buildList {
        listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).forEach { permission ->
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) add(permission)
        }
        if (Build.VERSION.SDK_INT >= 37) {
            val localNetwork = "android.permission.ACCESS_LOCAL_NETWORK"
            if (checkSelfPermission(localNetwork) != PackageManager.PERMISSION_GRANTED) add(localNetwork)
        }
    }.toTypedArray()

    companion object {
        private const val PERMISSION_REQUEST = 1403
    }
}

private class RemoteProbeView(
    context: android.content.Context,
    private val onStop: () -> Unit
) : View(context) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.MONOSPACE
        textAlign = Paint.Align.CENTER
    }
    private val button = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)
        val green = Color.rgb(102, 255, 102)
        val dim = Color.rgb(63, 175, 90)
        val width = width.toFloat()
        val height = height.toFloat()
        paint.style = Paint.Style.FILL
        paint.color = green
        paint.textSize = 24f
        canvas.drawText("P.R.S. REMOTE PROBE", width / 2f, height * 0.27f, paint)
        paint.color = Color.LTGRAY
        paint.textSize = 15f
        canvas.drawText("STATUS: ${if (RemoteProbeState.scanStatus == WatchBleScanStatus.SCANNING) "ACTIVE" else RemoteProbeState.scanStatus.name}", width / 2f, height * 0.39f, paint)
        canvas.drawText("LINK: ${RemoteProbeState.link.name}", width / 2f, height * 0.47f, paint)
        canvas.drawText("CONTACTS: ${RemoteProbeState.contactCount.toString().padStart(2, '0')}", width / 2f, height * 0.55f, paint)
        paint.color = dim
        canvas.drawText(if (RemoteProbeState.scanStatus == WatchBleScanStatus.SCANNING) "SCANNING..." else "SCAN STOPPED", width / 2f, height * 0.63f, paint)

        button.set(width * 0.12f, height * 0.75f, width * 0.88f, height * 0.90f)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = green
        canvas.drawRect(button, paint)
        paint.style = Paint.Style.FILL
        paint.textSize = 16f
        canvas.drawText("STOP / RETRIEVE PROBE", width / 2f, height * 0.84f, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP && button.contains(event.x, event.y)) {
            onStop()
            invalidate()
        }
        return true
    }
}
