package custom.view.clockview.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class CustomClockView @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? =  null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var cordX = 0f
    private var cordY = 0f
    private var radius = 0f

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        cordX = width / 2f
        cordY = height / 2f
        radius = (if (width > height) cordY else cordX)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(cordX, cordY, radius, paintCircle)
    }

    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = RadialGradient(
            0f, 0f, 100f,
            Color.BLACK, Color.rgb(47, 39, 39),
            Shader.TileMode.CLAMP
        )
        style = Paint.Style.STROKE
    }
}