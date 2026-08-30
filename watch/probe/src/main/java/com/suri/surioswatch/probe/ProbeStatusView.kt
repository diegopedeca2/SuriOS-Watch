package com.suri.surioswatch.probe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.View
import kotlin.math.min

/**
 * Compact PROBE status surface using the same GRID/emblem language as the
 * installed PROBE watchface. The status text is the operational signal; no
 * telemetry is rendered here.
 */
class ProbeStatusView(context: Context) : View(context) {
    private val emblem: Bitmap = BitmapFactory.decodeResource(
        resources,
        R.drawable.brotherhood_emblem_pipgreen
    )
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.MONOSPACE
        textAlign = Paint.Align.CENTER
    }
    private var serviceState = "IDLE"
    private var serviceMessage: String? = null
    private var phoneConnected = false
    private var connectionKnown = false
    private var connectionError: String? = null

    init {
        setBackgroundColor(Color.BLACK)
        isClickable = false
    }

    fun update(
        serviceState: String,
        serviceMessage: String?,
        phoneConnected: Boolean,
        connectionKnown: Boolean,
        connectionError: String?
    ) {
        this.serviceState = serviceState
        this.serviceMessage = serviceMessage
        this.phoneConnected = phoneConnected
        this.connectionKnown = connectionKnown
        this.connectionError = connectionError
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val faceSize = min(width, height).toFloat()
        val offsetX = (width - faceSize) / 2f
        val offsetY = (height - faceSize) / 2f
        val scale = faceSize / FACE_SIZE

        canvas.save()
        canvas.translate(offsetX, offsetY)
        canvas.scale(scale, scale)
        drawGrid(canvas)
        drawEmblem(canvas)
        drawStatus(canvas)
        drawVersion(canvas)
        canvas.restore()
    }

    private fun drawGrid(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = GREEN
        canvas.drawCircle(CENTRE, CENTRE, 215f, paint)
        paint.strokeWidth = 1f
        paint.color = GREEN_DIM
        canvas.drawCircle(CENTRE, CENTRE, 203f, paint)

        paint.strokeWidth = 3f
        paint.color = GREEN
        val cardinalTicks = arrayOf(
            floatArrayOf(225f, 12f, 225f, 29f),
            floatArrayOf(225f, 421f, 225f, 438f),
            floatArrayOf(12f, 225f, 29f, 225f),
            floatArrayOf(421f, 225f, 438f, 225f)
        )
        cardinalTicks.forEach { line -> canvas.drawLine(line[0], line[1], line[2], line[3], paint) }

        paint.strokeWidth = 2f
        val diagonalTicks = arrayOf(
            floatArrayOf(117f, 39f, 125f, 53f),
            floatArrayOf(333f, 39f, 325f, 53f),
            floatArrayOf(39f, 117f, 53f, 125f),
            floatArrayOf(411f, 117f, 397f, 125f),
            floatArrayOf(39f, 333f, 53f, 325f),
            floatArrayOf(411f, 333f, 397f, 325f),
            floatArrayOf(117f, 411f, 125f, 397f),
            floatArrayOf(333f, 411f, 325f, 397f)
        )
        diagonalTicks.forEach { line -> canvas.drawLine(line[0], line[1], line[2], line[3], paint) }

        paint.strokeWidth = 1f
        paint.color = GREEN_DIM
        val minorTicks = arrayOf(
            floatArrayOf(170f, 22f, 173f, 36f),
            floatArrayOf(280f, 22f, 277f, 36f),
            floatArrayOf(22f, 170f, 36f, 173f),
            floatArrayOf(428f, 170f, 414f, 173f),
            floatArrayOf(22f, 280f, 36f, 277f),
            floatArrayOf(428f, 280f, 414f, 277f),
            floatArrayOf(170f, 428f, 173f, 414f),
            floatArrayOf(280f, 428f, 277f, 414f)
        )
        minorTicks.forEach { line -> canvas.drawLine(line[0], line[1], line[2], line[3], paint) }
    }

    private fun drawEmblem(canvas: Canvas) {
        paint.alpha = 71
        canvas.drawBitmap(emblem, null, RectF(104f, 130f, 346f, 416f), paint)
        paint.alpha = 255
    }

    private fun drawStatus(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = GREEN
        canvas.drawRoundRect(RectF(171f, 187f, 279f, 264f), 10f, 10f, paint)

        val status = displayStatus()
        textPaint.color = status.colour
        textPaint.textSize = status.textSize
        canvas.drawText(status.title, CENTRE, 232f, textPaint)
        status.detail?.let { detail ->
            textPaint.textSize = 13f
            canvas.drawText(detail, CENTRE, 282f, textPaint)
        }
    }

    private fun drawVersion(canvas: Canvas) {
        textPaint.color = GREEN_DIM
        textPaint.textSize = 20f
        canvas.drawText("v2.2", CENTRE, 415f, textPaint)
    }

    private fun displayStatus(): DisplayStatus {
        val normalizedState = serviceState.uppercase()
        if (normalizedState == "ERROR") {
            return DisplayStatus(
                title = "ERROR",
                detail = fitDetail(serviceMessage ?: "SERVICE_ERROR"),
                colour = AMBER,
                textSize = 20f
            )
        }
        if (connectionError != null) {
            return DisplayStatus(
                title = "ERROR",
                detail = fitDetail("NODE_CHECK_${connectionError!!.uppercase()}"),
                colour = AMBER,
                textSize = 20f
            )
        }
        if (normalizedState == "ACTIVE" && connectionKnown && phoneConnected) {
            return DisplayStatus(title = "SCANNING", detail = null, colour = GREEN, textSize = 20f)
        }
        if (connectionKnown && !phoneConnected) {
            return DisplayStatus(title = "ERROR", detail = "A56 LINK OFFLINE", colour = AMBER, textSize = 20f)
        }
        if (normalizedState == "STOPPED") {
            return DisplayStatus(title = "ERROR", detail = "SERVICE STOPPED", colour = AMBER, textSize = 20f)
        }
        return DisplayStatus(title = "STARTING", detail = "WAITING FOR A56", colour = GREEN_DIM, textSize = 18f)
    }

    private fun fitDetail(value: String): String = value
        .replace('|', '_')
        .replace(Regex("\\s+"), " ")
        .take(28)

    private data class DisplayStatus(
        val title: String,
        val detail: String?,
        val colour: Int,
        val textSize: Float
    )

    private companion object {
        const val FACE_SIZE = 450f
        const val CENTRE = 225f
        const val GREEN = Color.GREEN
        const val GREEN_DIM = 0xFF3FAF5A.toInt()
        const val AMBER = 0xFFFFC857.toInt()
    }
}
