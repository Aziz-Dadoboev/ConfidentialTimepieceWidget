package custom.view.clockview.views

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import custom.view.clockview.R
import custom.view.clockview.utils.*
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class CustomClockView @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? =  null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val handler = Handler(Looper.getMainLooper())
    private val numberPosMap = hashMapOf<String, Coordinates>()
    private val pathHourMarker = Path()
    private val pathMinMarker = Path()
    private val rectangle = Rect()
    private var backgroundColor: Int = Color.WHITE
    private var circleColor: Int = Color.BLACK
    private var hoursArrowColor: Int = Color.BLACK
    private var minutesArrowColor: Int = Color.BLACK
    private var secondsArrowColor: Int = Color.BLACK
    private var numbersColor: Int = Color.DKGRAY
    private var hourMarkersColor: Int = Color.DKGRAY
    private var minutesMarkersColor: Int = Color.LTGRAY
    private var cordX = 0f
    private var cordY = 0f
    private var radius = 0f
    private var radiusNum = 0f
    private var currentHours: Int = 0
    private var currentMinutes: Int = 0
    private var currentSeconds: Int = 0

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
        context.withStyledAttributes(attrs, R.styleable.CustomClockView) {
            backgroundColor = getColor(
                R.styleable.CustomClockView_clockBackgroundColor, Color.WHITE)
            circleColor = getColor(
                R.styleable.CustomClockView_clockCircleColor, Color.BLACK)
            hoursArrowColor = getColor(
                R.styleable.CustomClockView_hoursArrowColor, Color.BLACK)
            minutesArrowColor = getColor(
                R.styleable.CustomClockView_minutesArrowColor, Color.BLACK)
            secondsArrowColor = getColor(
                R.styleable.CustomClockView_secondsArrowColor, Color.BLACK)
            numbersColor = getColor(
                R.styleable.CustomClockView_numbersColor, Color.DKGRAY)
            hourMarkersColor = getColor(
                R.styleable.CustomClockView_hourMarkersColor, Color.DKGRAY)
            minutesMarkersColor  = getColor(
                R.styleable.CustomClockView_minutesMarkersColor, Color.LTGRAY)
        }
    }

    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = circleColor
        style = Paint.Style.STROKE
    }

    private val paintCircleBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = backgroundColor
        style = Paint.Style.FILL
    }

    private val paintNumber = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = NUMBER_STROKE_WIDTH.dpToPx()
        color = numbersColor
        style = Paint.Style.FILL
        typeface = ResourcesCompat.getFont(context, R.font.sansserifflf)
    }

    private val paintMinMarker = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = minutesMarkersColor
    }

    private val paintHourMarker = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = hourMarkersColor
    }

    private val paintHourArrow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = hoursArrowColor
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.SQUARE
    }

    private val paintMinArrow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = minutesArrowColor
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.SQUARE
    }

    private val paintSecondArrow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = secondsArrowColor
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val paintSecondArrowStart = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = secondsArrowColor
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
        canvas.drawCircle(cordX, cordY, radius, paintCircleBackground)
        canvas.drawCircle(cordX, cordY, radius, paintCircle)
        numberPosMap.forEach { (number, coordinate) ->
            canvas.drawText(number, coordinate.x, coordinate.y, paintNumber )
        }
        canvas.drawPath(pathHourMarker, paintHourMarker)
        canvas.drawPath(pathMinMarker, paintMinMarker)
        drawHourArrow(canvas, currentHours, currentMinutes)
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

    private fun drawHourArrow(canvas: Canvas, hours: Int, minutes: Int) {
        val angle = 360f / 12f * hours.toFloat() - 90f + minutes / 2f
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
        val coordinates = calculateCoordinates(radiusNum - radiusNum * MINUTE_ARROW_LENGTH, angle)
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