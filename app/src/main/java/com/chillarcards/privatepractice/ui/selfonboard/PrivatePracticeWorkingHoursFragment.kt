package com.chillarcards.privatepractice.ui.selfonboard

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentPrivatePracticeWorkingHoursBinding
import com.chillarcards.privatepractice.viewmodel.RegistrationCompletedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar


class PrivatePracticeWorkingHoursFragment :
    Fragment(R.layout.fragment_private_practice_working_hours) {
    private val viewmodel by viewModel<RegistrationCompletedViewModel>()
    lateinit var binding: FragmentPrivatePracticeWorkingHoursBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentPrivatePracticeWorkingHoursBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val startMoriningTime = arrayOf("08:00", "08:30", "09:00", "09:30", "10:00", "10.30", "11:00", "11.30", "12:00")
//        val endMoriningTime = arrayOf("08:00", "08:30", "09:00", "09:30", "10:00", "10.30", "11:00", "11.30", "12:00")
//        val startEveningTime = arrayOf("12.30", "01:00", "01.30", "02:00", "02.30", "03:00", "03.30", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00")
//        val endEveningTime = arrayOf("12.30", "01:00", "01.30", "02:00", "02.30", "03:00", "03.30", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00")
//
//        val startMorningTimeAdapter = ArrayAdapter(requireContext(), R.layout.drop_down_items, startMoriningTime)
//        val endMoriningTimeAdapter =
//            ArrayAdapter(requireContext(), R.layout.drop_down_items, endMoriningTime)
//        val startEveningTimeAdapter =
//            ArrayAdapter(requireContext(), R.layout.drop_down_items, startEveningTime)
//        val endEveningTimeAdapter =
//            ArrayAdapter(requireContext(), R.layout.drop_down_items, endEveningTime)
//
//        binding.etStartTime.setAdapter(startMorningTimeAdapter)
//       // binding.etStartTime.showDropDown()
//        binding.etEndTime.setAdapter(endMoriningTimeAdapter)
//       // binding.etEndTime.showDropDown()
//        binding.etStartEveningTime.setAdapter(startEveningTimeAdapter)
//       // binding.etStartEveningTime.showDropDown()
//        binding.etEveningEndTime.setAdapter(endEveningTimeAdapter)
//      //  binding.etEveningEndTime.showDropDown()

        binding.etStartTime.setOnClickListener {
            showTimePicker(binding.etStartTime)
         //   binding.etStartTime.showDropDown()
        }

        binding.etEndTime.setOnClickListener {
            showTimePicker(binding.etEndTime)
         //   binding.etEndTime.showDropDown()
        }

        binding.etStartEveningTime.setOnClickListener {
            showTimePicker(binding.etStartEveningTime)
           // binding.etStartEveningTime.showDropDown()
        }

        binding.etEveningEndTime.setOnClickListener {
            showTimePicker(binding.etEveningEndTime)
          //  binding.etEveningEndTime.showDropDown()
        }

        val selectedSession = arguments?.let {
            PrivatePracticeWorkingHoursFragmentArgs.fromBundle(it).session
        }

        when (selectedSession) {
            "Morning" -> {
                binding.tvWorkingEveningHours.visibility = View.GONE
                binding.layoutEveningTime.visibility = View.GONE
                binding.layoutEveningTimeSlot.visibility=View.GONE
                binding.tvMorningWorkingHours.visibility = View.VISIBLE
                binding.layoutTime.visibility = View.VISIBLE
                binding.layoutTimeSlot.visibility=View.VISIBLE

            }
            "Evening" -> {
                binding.tvMorningWorkingHours.visibility = View.GONE
                binding.layoutTime.visibility = View.GONE
                binding.layoutTimeSlot.visibility=View.GONE
                binding.tvWorkingEveningHours.visibility = View.VISIBLE
                binding.layoutEveningTime.visibility = View.VISIBLE
                binding.layoutEveningTimeSlot.visibility=View.VISIBLE
            }
            "Both" -> {
                binding.tvWorkingEveningHours.visibility = View.VISIBLE
                binding.layoutEveningTime.visibility = View.VISIBLE
                binding.tvMorningWorkingHours.visibility = View.VISIBLE
                binding.layoutTime.visibility = View.VISIBLE
                binding.layoutTime.visibility = View.VISIBLE
                binding.layoutEveningTimeSlot.visibility=View.VISIBLE
                binding.layoutTimeSlot.visibility=View.VISIBLE
            }
        }

        binding.tvContinue.setOnClickListener {
            val selectedSession = arguments?.let {
                PrivatePracticeWorkingHoursFragmentArgs.fromBundle(it).session
            }

            when (selectedSession) {
                "Morning" -> {
                    if (binding.etStartTime.text.toString().isEmpty()) {
                        binding.etStartTime.error = "Please select morning start time"
                    } else if (binding.etEndTime.text.toString().isEmpty()) {
                        binding.etEndTime.error = "Please select morning end time"
                    } else {
                        // Save morning times to ViewModel
                        viewmodel.startTime.value = binding.etStartTime.text.toString()
                        viewmodel.endTime.value = binding.etEndTime.text.toString()

                        // Navigate to next fragment
                        val action = PrivatePracticeWorkingHoursFragmentDirections
                            .actionPrivatePracticeWorkingHoursFragmentToConsultationDurationFragment()
                        findNavController().navigate(action)
                    }
                }

                "Evening" -> {
                    if (binding.etStartEveningTime.text.toString().isEmpty()) {
                        binding.etStartEveningTime.error = "Please select evening start time"
                    } else if (binding.etEveningEndTime.text.toString().isEmpty()) {
                        binding.etEveningEndTime.error = "Please select evening end time"
                    } else {
                        // Save evening times to ViewModel
                        viewmodel.startTime.value = binding.etStartEveningTime.text.toString()
                        viewmodel.endTime.value = binding.etEveningEndTime.text.toString()

                        // Navigate to next fragment
                        val action = PrivatePracticeWorkingHoursFragmentDirections
                            .actionPrivatePracticeWorkingHoursFragmentToConsultationDurationFragment()
                        findNavController().navigate(action)
                    }
                }

                "Both" -> {
                    if (binding.etStartTime.text.toString().isEmpty()) {
                        binding.etStartTime.error = "Please select morning start time"
                    } else if (binding.etEndTime.text.toString().isEmpty()) {
                        binding.etEndTime.error = "Please select morning end time"
                    } else if (binding.etStartEveningTime.text.toString().isEmpty()) {
                        binding.etStartEveningTime.error = "Please select evening start time"
                    } else if (binding.etEveningEndTime.text.toString().isEmpty()) {
                        binding.etEveningEndTime.error = "Please select evening end time"
                    } else {
                        // Save both morning and evening times to ViewModel
                        viewmodel.startTime.value = binding.etStartTime.text.toString()
                        viewmodel.endTime.value = binding.etEndTime.text.toString()
                        viewmodel.startTime.value = binding.etStartEveningTime.text.toString()
                        viewmodel.endTime.value = binding.etEveningEndTime.text.toString()

                        // Navigate to next fragment
                        val action = PrivatePracticeWorkingHoursFragmentDirections
                            .actionPrivatePracticeWorkingHoursFragmentToConsultationDurationFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }



//        binding.tvContinue.setOnClickListener {
//            when {
//                binding.etStartTime.text.toString().isEmpty() -> {
//                    binding.etStartTime.error = "Please select start time"
//
//                }
//
//                binding.etEndTime.text.toString().isEmpty() -> {
//                    binding.etEndTime.error = "Please select end time"
//                }
//
//                binding.etStartEveningTime.text.toString().isEmpty() -> {
//                    binding.etStartEveningTime.error = "Please select start time"
//                }
//
//                binding.etEveningEndTime.text.toString().isEmpty() -> {
//                    binding.etEveningEndTime.error = "Please select end time"
//                }
//
//                else -> {
//                    viewmodel.startTime.value = binding.etStartTime.text.toString()
//                    viewmodel.endTime.value = binding.etEndTime.text.toString()
//                    viewmodel.startTime.value = binding.etStartEveningTime.text.toString()
//                    viewmodel.endTime.value = binding.etEveningEndTime.text.toString()
//                    val action=PrivatePracticeWorkingHoursFragmentDirections.actionPrivatePracticeWorkingHoursFragmentToConsultationDurationFragment()
//                    findNavController().navigate(action)
//                }
//            }
//
//
//        }
    }

    private fun showTimePicker(textView: AutoCompleteTextView) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                // Format the selected time
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                // Set the selected time to the AutoCompleteTextView
                textView.setText(formattedTime)
            }, hour, minute, true
        )
        timePickerDialog.show()
    }


}