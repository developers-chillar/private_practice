package com.chillarcards.privatepractice.utills
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import com.chillarcards.privatepractice.R
import java.text.SimpleDateFormat
import java.util.*

class CustomDatePickerDialog(
    context: Context,
    private val leaveDates: List<String>,
    private val onDateSelected: (String) -> Unit
) : AppCompatDialog(context) {

    private lateinit var calendarView: CalendarView
    private val leaveDateSet: Set<String> = leaveDates.toSet()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_custom_date_picker) // Custom layout for date picker

        calendarView = findViewById(R.id.calendarView)!!

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            if (leaveDateSet.contains(selectedDate)) {
                // Show a message if the date is a leave date
                Toast.makeText(context, "This date is marked as leave!", Toast.LENGTH_SHORT).show()
            } else {
                onDateSelected(selectedDate) // Call the selected date action
                dismiss()
            }
        }

        // Mark leave dates as red
        highlightLeaveDates()
    }

    private fun highlightLeaveDates() {
        // This is a placeholder where you might want to customize your CalendarView
        // In standard CalendarView, you cannot change the date colors
        // You may need to use a library or create a custom view for full control.
        for (date in leaveDateSet) {
            // Parse the leave date and highlight it
            val calendar = Calendar.getInstance().apply {
                time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
            }
            // Custom logic to highlight the date on your calendar view (requires additional implementation)
        }
    }
}
