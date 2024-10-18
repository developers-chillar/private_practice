package com.chillarcards.privatepractice.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.findFragment
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentCalendarBinding
import com.chillarcards.privatepractice.viewmodel.BookingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class CalendarFragment : DialogFragment() {
lateinit var binding: FragmentCalendarBinding
    private val bookingViewModel by viewModel<BookingViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        binding = FragmentCalendarBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
     //   val calendarView = binding.calendarView
//        val specialDate = CalendarDay.from(2022, 10, 18) // Set your special date
//        calendarView.addDecorator(CircleDecorator(specialDate))


    }

}