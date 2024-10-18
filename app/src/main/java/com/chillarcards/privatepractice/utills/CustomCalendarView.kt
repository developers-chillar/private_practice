package com.chillarcards.privatepractice.utills

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.CalendarView
import java.util.Calendar

class CustomCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CalendarView(context, attrs) {

    private val markedDates = mutableSetOf<Calendar>()
    private val disabledDates = mutableSetOf<Calendar>()

    private val paint = Paint().apply {
        color = Color.BLUE // Set the color for the ring
        style = Paint.Style.STROKE // Set the style to stroke to create a ring
        strokeWidth = 5f // Adjust the width of the ring as needed
    }

    private val greyPaint = Paint().apply {
        color = Color.LTGRAY // Set the color for the disabled date
        style = Paint.Style.FILL // Use fill style for the grey shade
    }

    fun markDate(date: Calendar) {
        markedDates.add(date)
        invalidate() // Redraw the CalendarView
    }

    fun disableDate(date: Calendar) {
        disabledDates.add(date)
        invalidate() // Redraw the CalendarView
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw rings for marked dates and grey out disabled dates
        markedDates.forEach { date ->
            val datePosition = calculatePosition(date)
            datePosition?.let {
                canvas.drawCircle(it.first, it.second, 25f, paint) // Draw the ring
            }
        }

        disabledDates.forEach { date ->
            val datePosition = calculatePosition(date)
            datePosition?.let {
                canvas.drawRect(it.first - 25f, it.second - 25f, it.first + 25f, it.second + 25f, greyPaint) // Grey out the date
            }
        }
    }

    private fun calculatePosition(date: Calendar): Pair<Float, Float>? {
        // Implement the logic to calculate the x and y positions based on the date
        // Assuming each day is 100 pixels wide and 100 pixels high
        val day = date.get(Calendar.DAY_OF_MONTH)
        val month = date.get(Calendar.MONTH)
        val year = date.get(Calendar.YEAR)

        val firstDayOfMonth = Calendar.getInstance().apply {
            set(year, month, 1)
        }

        val dayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday, ..., 7 = Saturday
        val dayOffset = day + dayOfWeek - 2 // Adjusting to get the correct position

        // Assuming each day is 100 pixels wide and 100 pixels high
        val dayWidth = 100f // Adjust based on your CalendarView dimensions
        val dayHeight = 100f // Adjust based on your CalendarView dimensions

        val x = (dayOffset % 7) * dayWidth + dayWidth / 2 // Center of the day
        val y = (dayOffset / 7) * dayHeight + dayHeight / 2 // Center of the day

        return Pair(x, y)
    }

    override fun performClick(): Boolean {
        // Override this to prevent clicks on disabled dates
        return false
    }
}

