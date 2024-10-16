package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.data.model.WorkingHours
import com.chillarcards.privatepractice.databinding.FragmentPrivateConsultationBinding
import com.chillarcards.privatepractice.viewmodel.RegistrationCompletedViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel

class PrivateConsultationFragment : Fragment(R.layout.fragment_private_consultation), View.OnClickListener {
    lateinit var binding: FragmentPrivateConsultationBinding
    private val viewmodel by viewModel<RegistrationCompletedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrivateConsultationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSun.setOnClickListener(this)
        binding.tvMon.setOnClickListener(this)
        binding.tvTue.setOnClickListener(this)
        binding.tvWed.setOnClickListener(this)
        binding.tvThu.setOnClickListener(this)
        binding.tvFri.setOnClickListener(this)
        binding.tvSat.setOnClickListener(this)

        binding.tvContinue.setOnClickListener {
            val gson = Gson()
            val jsonWorkingHours = gson.toJson(viewmodel.workingHoursNew)
            Log.d("JSON Output", "Working Hours JSON: $jsonWorkingHours")
            if (isAnyDaySelected()) {
                val workingHoursArray: Array<WorkingHours> = viewmodel.workingHoursNew.toTypedArray()
                val action = PrivateConsultationFragmentDirections
                    .actionPrivateConsultationFragmentToPrivatePracticeAvailableFragment(
                        doctorOnBoardNavDate = workingHoursArray
                    )
                findNavController().navigate(action)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please select at least one day",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun isAnyDaySelected(): Boolean {
        return binding.tvSun.isSelected || binding.tvMon.isSelected ||
                binding.tvTue.isSelected || binding.tvWed.isSelected ||
                binding.tvThu.isSelected || binding.tvFri.isSelected ||
                binding.tvSat.isSelected
    }

    private fun toggleDaySelection(dayTextView: TextView) {
        dayTextView.isSelected = !dayTextView.isSelected

        if (dayTextView.isSelected) {
            dayTextView.setBackgroundResource(R.drawable.background_orange)
            dayTextView.setTextColor(resources.getColor(R.color.white))
        } else {
            dayTextView.setBackgroundResource(R.drawable.background_grey)
            dayTextView.setTextColor(resources.getColor(R.color.black))
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvSun -> {
                toggleDaySelection(binding.tvSun)
                handleDaySelection("Sunday")
            }

            R.id.tvMon -> {
                toggleDaySelection(binding.tvMon)
                handleDaySelection("Monday")
            }

            R.id.tvTue -> {
                toggleDaySelection(binding.tvTue)
                handleDaySelection("Tuesday")
            }

            R.id.tvWed -> {
                toggleDaySelection(binding.tvWed)
                handleDaySelection("Wednesday")
            }

            R.id.tvThu -> {
                toggleDaySelection(binding.tvThu)
                handleDaySelection("Thursday")
            }

            R.id.tvFri -> {
                toggleDaySelection(binding.tvFri)
                handleDaySelection("Friday")
            }

            R.id.tvSat -> {
                toggleDaySelection(binding.tvSat)
                handleDaySelection("Saturday")
            }

            else -> {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleDaySelection(day: String) {
        val selectedDays = viewmodel.workingHoursNew.map { it.day }
        if (selectedDays.contains(day)) {
            // If the day is already selected, remove it from the working hours
            viewmodel.workingHoursNew.removeAll { it.day == day }
        } else {
            // Add the working hours for the selected day
            when (day) {
                "Sunday" -> addWorkingHoursForDay("Sunday", "", "", "")
                "Monday" -> addWorkingHoursForDay("Monday", "", "", "")
                "Tuesday" -> {
                    addWorkingHoursForDay("Tuesday", "", "", "")
                }

                "Wednesday" -> addWorkingHoursForDay("Wednesday", "", "", "")
                "Thursday" -> {
                    addWorkingHoursForDay("Thursday", "", "", "")
                }

                "Friday" -> {
                    addWorkingHoursForDay("Friday", "", "", "")
                }

                "Saturday" -> addWorkingHoursForDay("Saturday", "", "", "")
            }
        }

        Log.d("SelectedDays", "Current working hours: ${viewmodel.workingHoursNew}")


//    if (selectedDays.contains(day)) {
//            // If the day is already selected, remove it
//            selectedDays.remove(day)
//            viewmodel.workingHoursNew.remove(
//                WorkingHours(
//                    day = day,
//                    startTime = "",
//                    endTime = "",
//                    session = ""
//                )
//            )
//        } else {
//            // Otherwise, add it to the list
////            selectedDays.add(day)
//
//            viewmodel.workingHoursNew = mutableListOf(
//                WorkingHours("tuesday", "07:30", "12:30", "morning"),
//                WorkingHours("thursday", "14:30", "18:30", "evening"),
//                WorkingHours("thursday", "08:30", "12:30", "morning"),
//                WorkingHours("Friday", "09:00", "13:00", "morning"),
//                WorkingHours("Friday", "14:30", "18:30", "evening"),
//                WorkingHours("wednesday", "08:00", "12:00", "morning")
//            )
//            Log.d("print1", "workhoursNew: ${viewmodel.workingHoursNew.toString()}")
//            /*viewmodel.workingHoursNew = mutableListOf(
//                WorkingHours(
//                    day = day,
//                    startTime = "",
//                    endTime = "",
//                    session = ""
//                )
//            )*/
//
//        }
//        viewmodel.selectedDays.value = selectedDays
    }

    private fun addWorkingHoursForDay(
        day: String,
        startTime: String,
        endTime: String,
        session: String
    ) {
        viewmodel.workingHoursNew.add(
            WorkingHours(day, startTime, endTime, session)
        )
    }
}
