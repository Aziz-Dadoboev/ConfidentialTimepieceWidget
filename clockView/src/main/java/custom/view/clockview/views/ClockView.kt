package custom.view.clockview.views

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import custom.view.clockview.utils.*
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class CustomClockView @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? =  null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var cordX = 0f
    private var cordY = 0f
    private var radius = 0f
    private var radiusNum = 0f
    private val numberPosMap = hashMapOf<String, Coordinates>()
    private val pathHourMarker = Path()
    private val pathMinMarker = Path()
    private val rectangle = Rect()
    private var currentHours: Int = 11
    private var currentMinutes: Int = 5
    private var currentSeconds: Int = 30
    private val handler = Handler(Looper.getMainLooper())

    init {
        updateCurrentTime(Calendar.getInstance())
        val updateTimeRunnable = object : Runnable {
            override fun run() {
                updateCurrentTime(Calendar.getInstance())
                invalidate()
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(updateTimeRunnable, 1000)
    }

    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = RadialGradient(
            0f, 0f, 100f,
            Color.BLACK, Color.rgb(47, 39, 39),
            Shader.TileMode.CLAMP
        )
        style = Paint.Style.STROKE
    }

    private val paintNumber = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = NUMBER_STROKE_WIDTH.dpToPx()
        color = Color.DKGRAY
        style = Paint.Style.FILL
    }

    private val paintMinMarker = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.LTGRAY
    }

    private val paintHourMarker = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.DKGRAY
    }

    private val paintHourArrow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.SQUARE
    }

    private val paintMinArrow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.SQUARE
    }

    private val paintSecondArrow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val paintSecondArrowStart = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private fun updateCurrentTime(time: Calendar) {
        currentSeconds = time.get(Calendar.SECOND)
        currentMinutes = time.get(Calendar.MINUTE)
        currentHours = time.get(Calendar.HOUR)
    }


    private fun setPaintSettings() {
        paintNumber.textSize = radiusNum * NUMBERS_SIZE
        paintCircle.strokeWidth = radius * CIRCLE_STROKE_WIDTH
        paintHourArrow.strokeWidth = radiusNum * HOUR_ARROW_STROKE_WIDTH
        paintMinArrow.strokeWidth = radiusNum * MINUTE_ARROW_STROKE_WIDTH
        paintSecondArrow.strokeWidth = radiusNum * SECOND_ARROW_STROKE_WIDTH
        paintSecondArrowStart.strokeWidth = radiusNum * SECOND_ARROW_START_STROKE_WIDTH
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        cordX = width / 2f
        cordY = height / 2f
        radius = (if (width > height) cordY else cordX)
        radius -= radius * CIRCLE_PADDING
        radiusNum = radius - (radius * NUMBERS_SHIFT)
        setPaintSettings()
        calculate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(cordX, cordY, radius, paintCircle)
        numberPosMap.forEach { (number, coordinate) ->
            canvas.drawText(number, coordinate.x, coordinate.y, paintNumber )
        }
        canvas.drawPath(pathHourMarker, paintHourMarker)
        canvas.drawPath(pathMinMarker, paintMinMarker)
        drawHourArrow(canvas, currentHours)
        drawMinuteArrow(canvas, currentMinutes)
        drawSecondArrow(canvas, currentSeconds)
    }

    private fun calculate() {
        var hour = 3
        numberPosMap.clear()
        for (angle in 0 until 360 step 6) {
            val pointStart = calculateCoordinates(radius - (radius * MARKER_SHIFT), angle.toFloat())
            val pointFinish = calculateCoordinates(radius, angle.toFloat())
            val pointCenterX = (pointStart.x + pointFinish.x) / 2
            val pointCenterY = (pointStart.y + pointFinish.y) / 2
            if (angle % 30 == 0) {
                val text = when (hour) {
                    in 0..12 -> hour.toString()
                    else -> (hour - 12).toString()
                }
                paintNumber.getTextBounds(text, 0, text.length, rectangle)
                val coordinates = calculateCoordinates(radiusNum, angle.toFloat())
                pathHourMarker.addCircle(pointCenterX, pointCenterY,
                    radius * HOURS_MARKER_RADIUS, Path.Direction.CW)
                numberPosMap[text] = Coordinates(
                    x = coordinates.x - paintNumber.measureText(text) / 2f,
                    y = coordinates.y + rectangle.height() / 2f
                )

                hour++
            } else {
                pathMinMarker.addCircle(
                    pointCenterX,
                    pointCenterY,
                    radius * MINUTES_MARKER_RADIUS,
                    Path.Direction.CW)
            }
        }
    }

    private fun calculateCoordinates(radius: Float, angle: Float): Coordinates {
        val x = (cordX + radius * cos(Math.toRadians(angle.toDouble())).toFloat())
        val y = (cordY + radius * sin(Math.toRadians(angle.toDouble())).toFloat())
        return Coordinates(x, y)
    }

    private fun drawHourArrow(canvas: Canvas, hours: Int) {
        val angle = 360f / 12f * hours.toFloat() - 90f
        val radius = radiusNum / 2f
        val coordinates = calculateCoordinates(radius, angle)
        canvas.drawLine(cordX, cordY, coordinates.x, coordinates.y, paintHourArrow)
        drawHourArrowStart(canvas, angle, radius)
    }

    private fun drawHourArrowStart(canvas: Canvas, angle: Float, radius: Float) {
        val startCoordinates = calculateCoordinates(0f, angle + 180f)
        val endCoordinates = calculateCoordinates(
            radius * HOUR_ARROW_LENGTH, angle + 180f)
        canvas.drawLine(
            startCoordinates.x,
            startCoordinates.y,
            endCoordinates.x,
            endCoordinates.y,
            paintHourArrow
        )
    }

    private fun drawMinuteArrow(canvas: Canvas, minutes: Int) {
        val angle = 360f / 60f * minutes.toFloat() - 90f
        val coordinates = calculateCoordinates((radiusNum / 1.15f), angle)
        canvas.drawLine(cordX, cordY, coordinates.x, coordinates.y, paintMinArrow)
        drawMinuteArrowStart(canvas, minutes)
    }

    private fun drawMinuteArrowStart(canvas: Canvas, minutes: Int) {
        val startAngle = 360f / 60f * minutes.toFloat() - 90f
        val endAngle = ((startAngle + 180f) % 360f)
        val endCoordinates = calculateCoordinates(
            radiusNum * MINUTE_ARROW_LENGTH, startAngle)
        val startCoordinates = calculateCoordinates(
            radiusNum * MINUTE_ARROW_LENGTH, endAngle)
        canvas.drawLine(
            startCoordinates.x,
            startCoordinates.y,
            endCoordinates.x,
            endCoordinates.y,
            paintMinArrow
        )
    }

    private fun drawSecondArrow(canvas: Canvas, seconds: Int) {
        val angle = 360f / 60f * seconds.toFloat() - 90f
        val coordinates = calculateCoordinates(radiusNum, angle)
        canvas.drawLine(cordX, cordY, coordinates.x, coordinates.y, paintSecondArrow)
        drawSecondArrowStart(canvas, seconds)
    }

    private fun drawSecondArrowStart(canvas: Canvas, seconds: Int) {
        val startAngle = 360f / 60f * seconds.toFloat() - 90f
        val endAngle = ((startAngle + 180f) % 360f)
        val endCoordinates = calculateCoordinates(
            radiusNum * SECOND_ARROW_LENGTH_START, startAngle)
        val startCoordinates = calculateCoordinates(
            radiusNum * SECOND_ARROW_LENGTH, endAngle)
        canvas.drawLine(
            startCoordinates.x,
            startCoordinates.y,
            endCoordinates.x,
            endCoordinates.y,
            paintSecondArrowStart
        )
    }

}

private data class Coordinates(
    val x: Float,
    val y: Float
)