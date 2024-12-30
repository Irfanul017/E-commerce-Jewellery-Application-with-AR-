package com.example.majorproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RingSizeCalculator: AppCompatActivity() {

    private lateinit var circleView: View
    private lateinit var diameterSeekBar: SeekBar
    private lateinit var diameterValueText: TextView

    private var selectedDiameter = 14.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ring_size_calculator)

        circleView = findViewById(R.id.circleView)
        diameterSeekBar = findViewById(R.id.diameterSeekBar)
        diameterValueText = findViewById(R.id.diameterValueText)
        val calculateButton: Button = findViewById(R.id.calculate_button)

        diameterSeekBar.max = 60
        diameterSeekBar.progress = 14

        diameterSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedDiameter = progress.toDouble()
                updateCircleSize(selectedDiameter)
                diameterValueText.text = "Diameter: $selectedDiameter mm"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        calculateButton.setOnClickListener {
            val ringSize = calculateRingSize(selectedDiameter)
            Log.d("ringSize","${ringSize}")
            val resultIntent = Intent()
            resultIntent.putExtra("ringSize", ringSize)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun calculateRingSize(diameter: Double): String {
        val ringSizeChart = mapOf(
            9.0 to "09.00",
            10.0 to "10.00",
            11.0 to "11.00",
            12.0 to "12.00",
            12.9 to "12.90",
            13.0 to "13.00",
            13.2 to "13.20",
            13.4 to "13.40",
            13.7 to "13.70",
            14.0 to "14.00",
            14.3 to "14.30",
            15.0 to "15.00",
            16.0 to "16.00",
            17.0 to "17.00",
            18.0 to "18.00",
            19.0 to "19.00",
            20.0 to "20.00"
        )

        var closestSize = ""
        var minDifference = Double.MAX_VALUE

        for ((ringDiameter, ringSize) in ringSizeChart) {
            val difference = Math.abs(ringDiameter - diameter)
            if (difference < minDifference) {
                minDifference = difference
                closestSize = ringSize
            }
        }

        return closestSize.ifEmpty { "Size not found" }
    }

    private fun updateCircleSize(diameter: Double) {
        val circleLayoutParams = circleView.layoutParams
        val newSize = (diameter * 10).toInt()
        circleLayoutParams.width = newSize
        circleLayoutParams.height = newSize
        circleView.layoutParams = circleLayoutParams
    }
}