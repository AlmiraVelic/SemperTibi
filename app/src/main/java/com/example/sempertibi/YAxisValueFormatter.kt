package com.example.sempertibi

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class YAxisValueFormatter : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return when {
            value <= 0f -> "Low"
            value <= 25f -> "Medium"
            else -> "High"
        }
    }
}