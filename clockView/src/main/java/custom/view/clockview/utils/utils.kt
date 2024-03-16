package custom.view.clockview.utils

import android.content.res.Resources

fun Float.dpToPx(): Float {
    return (this * Resources.getSystem().displayMetrics.density)
}
