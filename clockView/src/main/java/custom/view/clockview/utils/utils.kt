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