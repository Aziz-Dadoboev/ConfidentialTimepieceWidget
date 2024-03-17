package custom.view

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import custom.view.clockview.views.CustomClockView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val parentLayout = findViewById<ViewGroup>(R.id.activity_main)
        val dynamicClockView = addCustomClockViewToActivity(parentLayout)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val clockView = findViewById<CustomClockView>(R.id.clockView)

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

    private fun addCustomClockViewToActivity(parentLayout: ViewGroup) : CustomClockView {
        val customClockView = CustomClockView(this)
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        customClockView.layoutParams = layoutParams
        customClockView.visibility = View.INVISIBLE
        parentLayout.addView(customClockView)
        return customClockView
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                openSettingsDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openSettingsDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.settings_dialog)
        dialog.show()
    }
}
