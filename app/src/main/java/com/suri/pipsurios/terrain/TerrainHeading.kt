package com.suri.pipsurios.terrain

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.view.Surface
import android.view.Display
import kotlin.math.PI

class CircularHeadingSmoother(
    private val alpha: Float = 0.12f,
    private val deadbandDegrees: Float = 0.35f
) {
    private var current: Float? = null
    fun update(degrees: Float): Float {
        val normalized = normalize(degrees)
        val previous = current ?: return normalized.also { current = it }
        val delta = ((normalized - previous + 540f) % 360f) - 180f
        if (kotlin.math.abs(delta) < deadbandDegrees) return previous
        return normalize(previous + alpha * delta).also { current = it }
    }
    fun reset() { current = null }
    private fun normalize(value: Float) = ((value % 360f) + 360f) % 360f
}

class TerrainHeading(private val context: Context) {
    private val sensors = context.getSystemService(SensorManager::class.java)
    private val smoother = CircularHeadingSmoother()
    private var listener: SensorEventListener? = null

    fun start(onHeading: (Float) -> Unit, onUnavailable: () -> Unit) {
        stop()
        val sensor = sensors?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) ?: return onUnavailable()
        val active = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            override fun onSensorChanged(event: SensorEvent) {
                val raw = FloatArray(9); val adjusted = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(raw, event.values)
                val rotation = context.getSystemService(DisplayManager::class.java)
                    ?.getDisplay(Display.DEFAULT_DISPLAY)
                    ?.rotation
                    ?: Surface.ROTATION_0
                val axes = when (rotation) {
                    Surface.ROTATION_90 -> SensorManager.AXIS_Y to SensorManager.AXIS_MINUS_X
                    Surface.ROTATION_180 -> SensorManager.AXIS_MINUS_X to SensorManager.AXIS_MINUS_Y
                    Surface.ROTATION_270 -> SensorManager.AXIS_MINUS_Y to SensorManager.AXIS_X
                    else -> SensorManager.AXIS_X to SensorManager.AXIS_Y
                }
                SensorManager.remapCoordinateSystem(raw, axes.first, axes.second, adjusted)
                val radians = SensorManager.getOrientation(adjusted, FloatArray(3))[0]
                onHeading(smoother.update((radians * 180f / PI.toFloat() + 360f) % 360f))
            }
        }
        listener = active
        sensors.registerListener(active, sensor, SensorManager.SENSOR_DELAY_UI)
    }
    fun stop() { listener?.let { sensors?.unregisterListener(it) }; listener = null; smoother.reset() }
}
