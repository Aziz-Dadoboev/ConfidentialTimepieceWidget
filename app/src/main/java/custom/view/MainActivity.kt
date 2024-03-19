package custom.view

import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import custom.view.clockview.views.CustomClockView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dynamicClockView = addCustomClockViewToActivity()
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val clockView = findViewById<CustomClockView>(R.id.clockView)

        setAttributes(clockView, dynamicClockView)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radio: RadioButton = group.findViewById(checkedId)
            if (radio == findViewById<RadioButton>(R.id.dynamicButton)) {
                clockView.visibility = View.INVISIBLE
                dynamicClockView.visibility = View.VISIBLE
            } else {
                dynamicClockView.visibility = View.INVISIBLE
                clockView.visibility = View.VISIBLE
            }
        }
    }

    private fun addCustomClockViewToActivity() : CustomClockView {
        val parentLayout = findViewById<ConstraintLayout>(R.id.activity_main)
        val constraintSet = ConstraintSet()
        val customClockView = CustomClockView(this)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        customClockView.layoutParams = layoutParams
        customClockView.visibility = View.INVISIBLE
        customClockView.id = View.generateViewId()
        parentLayout.addView(customClockView)
        constraintSet.clone(parentLayout)

        if (this.resources.configuration.orientation == ORIENTATION_PORTRAIT) {
            constraintSet.connect(
                customClockView.id,
                ConstraintSet.BOTTOM,
                R.id.radioGroup,
                ConstraintSet.TOP,
                0
            )
            constraintSet.connect(
                customClockView.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START,
                0
            )
        } else {
            constraintSet.connect(
                customClockView.id,
                ConstraintSet.END,
                R.id.radioGroup,
                ConstraintSet.START,
                0
            )
            constraintSet.connect(
                customClockView.id,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP,
                0
            )
        }
        constraintSet.connect(
            customClockView.id,
            ConstraintSet.TOP,
            ConstraintSet.PARENT_ID,
            ConstraintSet.TOP,
            0
        )
        constraintSet.connect(
            customClockView.id,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START,
            0
        )

        constraintSet.applyTo(parentLayout)
        return customClockView
    }

    private fun setAttributes(clockView: CustomClockView, dynamicClockView: CustomClockView) {
        // using setters & getters
        // Circle
        dynamicClockView.setCircleColor(clockView.getCircleColor())
        // Clock Background
        dynamicClockView.setClockBackgroundColor(clockView.getClockBackgroundColor())
        clockView.setClockBackgroundColor(Color.rgb(250, 247, 237))
        // Hours Arrow
        dynamicClockView.setHoursArrowColor(clockView.getHoursArrowColor())
        // Minutes Arrow
        dynamicClockView.setMinutesArrowColor(clockView.getMinutesArrowColor())
        // Seconds Arrow
        val oldSecondsArrowColor = clockView.getSecondsArrowColor()
        clockView.setSecondsArrowColor(Color.rgb(255, 0, 0))
        dynamicClockView.setSecondsArrowColor(clockView.getSecondsArrowColor())
        clockView.setSecondsArrowColor(oldSecondsArrowColor)
        // Numbers
        dynamicClockView.setNumbersColor(clockView.getNumbersColor())
        // Markers
        dynamicClockView.setHourMarkersColor(clockView.getHourMarkersColor())
        dynamicClockView.setMinutesMarkersColor(clockView.getMinutesMarkersColor())
    }
}