package custom.view.clockview.views

import android.content.Context
import android.graphics.*
import android.os.*
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

    private val mHandler = Handler(Looper.getMainLooper())
    private val mNumberPosMap = hashMapOf<String, Coordinates>()
    private val mPathHourMarker = Path()
    private val mPathMinMarker = Path()
    private val mRectangle = Rect()
    private var mBackgroundColor = 0
    private var mCircleColor = 0
    private var mHoursArrowColor = 0
    private var mMinutesArrowColor = 0
    private var mSecondsArrowColor = 0
    private var mNumbersColor = 0
    private var mHourMarkersColor = 0
    private var mMinutesMarkersColor = 0
    private var mCircleCenterColor = 0
    private var mCordX: Float = 0f
    private var mCordY: Float = 0f
    private var mRadius: Float = 0f
    private var mRadiusNum: Float = 0f
    private var mCurrentHours: Int = 0
    private var mCurrentMinutes: Int = 0
    private var mCurrentSeconds: Int = 0

    init {
        updateCurrentTime(Calendar.getInstance())
        val updateTimeRunnable = object : Runnable {
            override fun run() {
                updateCurrentTime(Calendar.getInstance())
                invalidate()
                mHandler.postDelayed(this, 1000)
            }
        }
        mHandler.postDelayed(updateTimeRunnable, 1000)
        context.withStyledAttributes(attrs, R.styleable.CustomClockView) {
            mBackgroundColor = getColor(
                R.styleable.CustomClockView_clockBackgroundColor, Color.WHITE)
            mCircleColor = getColor(
                R.styleable.CustomClockView_clockCircleColor, Color.BLACK)
            mHoursArrowColor = getColor(
                R.styleable.CustomClockView_hoursArrowColor, Color.BLACK)
            mMinutesArrowColor = getColor(
                R.styleable.CustomClockView_minutesArrowColor, Color.BLACK)
            mSecondsArrowColor = getColor(
                R.styleable.CustomClockView_secondsArrowColor, Color.BLACK)
            mNumbersColor = getColor(
                R.styleable.CustomClockView_numbersColor, Color.DKGRAY)
            mHourMarkersColor = getColor(
                R.styleable.CustomClockView_hourMarkersColor, Color.DKGRAY)
            mMinutesMarkersColor = getColor(
                R.styleable.CustomClockView_minutesMarkersColor, Color.LTGRAY)
            mCircleCenterColor = getColor(
                R.styleable.CustomClockView_circleCenterColor, Color.WHITE)
        }
    }

    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val paintCenter = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val paintCircleBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val paintNumber = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = NUMBER_STROKE_WIDTH.dpToPx()
        style = Paint.Style.FILL
        typeface = ResourcesCompat.getFont(context, R.font.sansserifflf)
    }

    private val paintMinMarker = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val paintHourMarker = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val paintHourArrow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.SQUARE
    }

    private val paintMinArrow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.SQUARE
    }

    private val paintSecondArrow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val paintSecondArrowStart = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private fun updateCurrentTime(time: Calendar) {
        mCurrentSeconds = time.get(Calendar.SECOND)
        mCurrentMinutes = time.get(Calendar.MINUTE)
        mCurrentHours = time.get(Calendar.HOUR)
    }


    private fun setPaintSettings() {
        // set sizes
        paintNumber.textSize = mRadiusNum * NUMBERS_SIZE
        paintCircle.strokeWidth = mRadius * CIRCLE_STROKE_WIDTH
        paintHourArrow.strokeWidth = mRadiusNum * HOUR_ARROW_STROKE_WIDTH
        paintMinArrow.strokeWidth = mRadiusNum * MINUTE_ARROW_STROKE_WIDTH
        paintSecondArrow.strokeWidth = mRadiusNum * SECOND_ARROW_STROKE_WIDTH
        paintSecondArrowStart.strokeWidth = mRadiusNum * SECOND_ARROW_START_STROKE_WIDTH
        // set colors
        paintCircle.color = mCircleColor
        paintCircleBackground.color = mBackgroundColor
        paintNumber.color = mNumbersColor
        paintMinMarker.color = mMinutesMarkersColor
        paintHourMarker.color = mHourMarkersColor
        paintHourArrow.color = mHoursArrowColor
        paintMinArrow.color = mMinutesArrowColor
        paintSecondArrow.color = mSecondsArrowColor
        paintSecondArrowStart.color = mSecondsArrowColor
        paintCenter.color = mCircleCenterColor
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(mCordX, mCordY, mRadius, paintCircleBackground)
        canvas.drawCircle(mCordX, mCordY, mRadius, paintCircle)
        mNumberPosMap.forEach { (number, coordinate) ->
            canvas.drawText(number, coordinate.x, coordinate.y, paintNumber )
        }
        canvas.drawPath(mPathHourMarker, paintHourMarker)
        canvas.drawPath(mPathMinMarker, paintMinMarker)
        drawHourArrow(canvas, mCurrentHours, mCurrentMinutes)
        drawMinuteArrow(canvas, mCurrentMinutes)
        drawSecondArrow(canvas, mCurrentSeconds)
        canvas.drawCircle(mCordX, mCordY, mRadius * CIRCLE_CENTER_RADIUS, paintCenter)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCordX = width / 2f
        mCordY = height / 2f
        mRadius = (if (width > height) mCordY else mCordX)
        mRadius -= mRadius * CIRCLE_PADDING
        mRadiusNum = mRadius - (mRadius * NUMBERS_SHIFT)
        setPaintSettings()
        calculate()
    }

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putInt("mBackgroundColor", mBackgroundColor)
            putInt("mCircleColor", mCircleColor)
            putInt("mHoursArrowColor", mHoursArrowColor)
            putInt("mMinutesArrowColor", mMinutesArrowColor)
            putInt("mSecondsArrowColor", mSecondsArrowColor)
            putInt("mNumbersColor", mNumbersColor)
            putInt("mHourMarkersColor", mHourMarkersColor)
            putInt("mMinutesMarkersColor", mMinutesMarkersColor)
            putInt("mCircleCenterColor", mCircleCenterColor)
            putParcelable("superState", super.onSaveInstanceState())
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState: Parcelable? = null
        if (state is Bundle) {
            mBackgroundColor = state.getInt("mBackgroundColor")
            mCircleColor = state.getInt("mCircleColor")
            mHoursArrowColor = state.getInt("mHoursArrowColor")
            mMinutesArrowColor = state.getInt("mMinutesArrowColor")
            mSecondsArrowColor = state.getInt("mSecondsArrowColor")
            mNumbersColor = state.getInt("mNumbersColor")
            mHourMarkersColor = state.getInt("mHourMarkersColor")
            mMinutesMarkersColor = state.getInt("mMinutesMarkersColor")
            mCircleCenterColor = state.getInt("mCircleCenterColor")
            mMinutesMarkersColor = state.getInt("mMinutesMarkersColor")
            superState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                state.getParcelable("superState", Parcelable::class.java)
            } else {
                @Suppress("DEPRECATION")
                state.getParcelable("superState")
            }
        }
        super.onRestoreInstanceState(superState)
    }

    private fun calculate() {
        var hour = 3
        mNumberPosMap.clear()
        for (angle in 0 until 360 step 6) {
            val pointStart = calculateCoordinates(mRadius - (mRadius * MARKER_SHIFT), angle.toFloat())
            val pointFinish = calculateCoordinates(mRadius, angle.toFloat())
            val pointCenterX = (pointStart.x + pointFinish.x) / 2
            val pointCenterY = (pointStart.y + pointFinish.y) / 2
            if (angle % 30 == 0) {
                val text = when (hour) {
                    in 0..12 -> hour.toString()
                    else -> (hour - 12).toString()
                }
                paintNumber.getTextBounds(text, 0, text.length, mRectangle)
                val coordinates = calculateCoordinates(mRadiusNum, angle.toFloat())
                mPathHourMarker.addCircle(pointCenterX, pointCenterY,
                    mRadius * HOURS_MARKER_RADIUS, Path.Direction.CW)
                mNumberPosMap[text] = Coordinates(
                    x = coordinates.x - paintNumber.measureText(text) / 2f,
                    y = coordinates.y + mRectangle.height() / 2f
                )

                hour++
            } else {
                mPathMinMarker.addCircle(
                    pointCenterX,
                    pointCenterY,
                    mRadius * MINUTES_MARKER_RADIUS,
                    Path.Direction.CW)
            }
        }
    }

    private fun calculateCoordinates(radius: Float, angle: Float): Coordinates {
        val x = (mCordX + radius * cos(Math.toRadians(angle.toDouble())).toFloat())
        val y = (mCordY + radius * sin(Math.toRadians(angle.toDouble())).toFloat())
        return Coordinates(x, y)
    }

    private fun drawHourArrow(canvas: Canvas, hours: Int, minutes: Int) {
        val angle = 360f / 12f * hours.toFloat() - 90f + minutes / 2f
        val radius = mRadiusNum / 2f
        val coordinates = calculateCoordinates(radius, angle)
        canvas.drawLine(mCordX, mCordY, coordinates.x, coordinates.y, paintHourArrow)
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
        val coordinates = calculateCoordinates(mRadiusNum - mRadiusNum * MINUTE_ARROW_LENGTH, angle)
        canvas.drawLine(mCordX, mCordY, coordinates.x, coordinates.y, paintMinArrow)
        drawMinuteArrowStart(canvas, minutes)
    }

    private fun drawMinuteArrowStart(canvas: Canvas, minutes: Int) {
        val startAngle = 360f / 60f * minutes.toFloat() - 90f
        val endAngle = ((startAngle + 180f) % 360f)
        val endCoordinates = calculateCoordinates(
            mRadiusNum * MINUTE_ARROW_LENGTH, startAngle)
        val startCoordinates = calculateCoordinates(
            mRadiusNum * MINUTE_ARROW_LENGTH, endAngle)
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
        val coordinates = calculateCoordinates(mRadiusNum, angle)
        canvas.drawLine(mCordX, mCordY, coordinates.x, coordinates.y, paintSecondArrow)
        drawSecondArrowStart(canvas, seconds)
    }

    private fun drawSecondArrowStart(canvas: Canvas, seconds: Int) {
        val startAngle = 360f / 60f * seconds.toFloat() - 90f
        val endAngle = ((startAngle + 180f) % 360f)
        val endCoordinates = calculateCoordinates(
            mRadiusNum * SECOND_ARROW_LENGTH_START, startAngle)
        val startCoordinates = calculateCoordinates(
            mRadiusNum * SECOND_ARROW_LENGTH, endAngle)
        canvas.drawLine(
            startCoordinates.x,
            startCoordinates.y,
            endCoordinates.x,
            endCoordinates.y,
            paintSecondArrowStart
        )
    }

    fun setCircleColor(newColor: Int) {
        mCircleColor = newColor
        invalidate()
    }

    fun getCircleColor(): Int {
        return mCircleColor
    }
    fun setClockBackgroundColor(newColor: Int) {
        mBackgroundColor = newColor
        invalidate()
    }

    fun getClockBackgroundColor(): Int {
        return mBackgroundColor
    }

    fun setHoursArrowColor(newColor: Int) {
        mHoursArrowColor = newColor
        invalidate()
    }

    fun getHoursArrowColor(): Int {
        return mHoursArrowColor
    }

    fun setMinutesArrowColor(newColor: Int) {
        mMinutesArrowColor = newColor
        invalidate()
    }

    fun getMinutesArrowColor(): Int {
        return mMinutesArrowColor
    }

    fun setSecondsArrowColor(newColor: Int) {
        mSecondsArrowColor = newColor
        invalidate()
    }

    fun getSecondsArrowColor(): Int {
        return mSecondsArrowColor
    }

    fun setNumbersColor(newColor: Int) {
        mNumbersColor = newColor
        invalidate()
    }

    fun getNumbersColor(): Int {
        return mNumbersColor
    }

    fun setHourMarkersColor(newColor: Int) {
        mHourMarkersColor = newColor
        invalidate()
    }

    fun getHourMarkersColor(): Int {
        return mHourMarkersColor
    }

    fun setMinutesMarkersColor(newColor: Int) {
        mMinutesMarkersColor = newColor
        invalidate()
    }

    fun getMinutesMarkersColor(): Int {
        return mMinutesMarkersColor
    }
}

private data class Coordinates(
    val x: Float,
    val y: Float
)