package custom.view.clockview.utils

import android.content.res.Resources

fun Float.dpToPx(): Float {
    return (this * Resources.getSystem().displayMetrics.density)
}

const val CIRCLE_PADDING = 0.1f
const val NUMBERS_SHIFT = 0.25f
const val MARKER_SHIFT = 0.15f
const val NUMBERS_SIZE = 0.3f
const val HOURS_MARKER_RADIUS = 0.017f
const val MINUTES_MARKER_RADIUS = 0.015f
const val CIRCLE_STROKE_WIDTH = 0.07f
const val NUMBER_STROKE_WIDTH = 2f
const val HOUR_ARROW_STROKE_WIDTH = 0.075f
const val HOUR_ARROW_LENGTH = 0.3f
const val MINUTE_ARROW_LENGTH = 0.17f
const val MINUTE_ARROW_STROKE_WIDTH = 0.045f
const val SECOND_ARROW_STROKE_WIDTH = 0.01f
const val SECOND_ARROW_START_STROKE_WIDTH = 0.03f
const val SECOND_ARROW_LENGTH_START = 0.0f
const val SECOND_ARROW_LENGTH = 0.2f
const val CIRCLE_CENTER_RADIUS = 0.025f