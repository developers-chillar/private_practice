package com.chillarcards.privatepractice.ui.selfonboard

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.data.model.WorkingHours
import com.chillarcards.privatepractice.databinding.FragmentPrivatePracticeWorkingHoursBinding
import com.chillarcards.privatepractice.viewmodel.RegistrationCompletedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar

class PrivatePracticeWorkingHoursFragment : Fragment(R.layout.fragment_private_practice_working_hours) {

    private val viewmodel by viewModel<RegistrationCompletedViewModel>()
    private lateinit var binding: FragmentPrivatePracticeWorkingHoursBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrivatePracticeWorkingHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = PrivatePracticeAvailableFragmentArgs.fromBundle(requireArguments())
        val workingHoursArray = args.doctorOnBoardNavDate
        val selectedSession = args.doctorOnBoardNavDate
        Log.d("selectedSession", "workingHoursArray: ${args.doctorOnBoardNavDate.contentToString()}")
        // Setting up time pickers
        binding.etStartTime.setOnClickListener { showTimePicker(binding.etStartTime) }
        binding.etEndTime.setOnClickListener { showTimePicker(binding.etEndTime) }
        binding.etStartEveningTime.setOnClickListener { showTimePicker(binding.etStartEveningTime) }
        binding.etEveningEndTime.setOnClickListener { showTimePicker(binding.etEveningEndTime) }


        setupUIBasedOnSession(selectedSession)

        binding.tvContinue.setOnClickListener {
            validateAndNavigate(selectedSession)
        }
    }

    private fun setupUIBasedOnSession(selectedSession: Array<WorkingHours>?) {
        Log.d("PrivatePractice", "Selected Session: $selectedSession")
        // Determine session availability
        val hasMorning = selectedSession?.any { it.session.equals("morning", ignoreCase = true) } == true
        val hasEvening = selectedSession?.any { it.session.equals("evening", ignoreCase = true) } == true
        Log.d("PrivatePractice", "Has Morning: $hasMorning, Has Evening: $hasEvening")

        // Configure UI based on available sessions
        when {
            hasMorning && hasEvening -> configureUIForBothSessions()
            hasMorning -> configureUIForMorningSession()
            hasEvening -> configureUIForEveningSession()
            else -> {
                // Optionally handle the case where no sessions are available
                Toast.makeText(requireContext(), "No sessions available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configureUIForBothSessions() {
        binding.tvWorkingEveningHours.visibility = View.VISIBLE
        binding.layoutEveningTime.visibility = View.VISIBLE
        binding.tvMorningWorkingHours.visibility = View.VISIBLE
        binding.layoutTime.visibility = View.VISIBLE
        binding.layoutEveningTimeSlot.visibility = View.VISIBLE
        binding.layoutTimeSlot.visibility = View.VISIBLE
    }

    private fun configureUIForMorningSession() {
        binding.tvWorkingEveningHours.visibility = View.GONE
        binding.layoutEveningTimeSlot.visibility = View.GONE
        binding.tvMorningWorkingHours.visibility = View.VISIBLE
        binding.layoutTime.visibility = View.VISIBLE
        binding.layoutTimeSlot.visibility = View.VISIBLE

        // Hide evening time layouts
        binding.layoutEveningTime.visibility = View.GONE
        binding.etStartEveningTime.visibility = View.GONE
        binding.etEveningEndTime.visibility = View.GONE
    }

    private fun configureUIForEveningSession() {
        binding.tvMorningWorkingHours.visibility = View.GONE
        binding.layoutTime.visibility = View.GONE
        binding.layoutTimeSlot.visibility = View.GONE

        binding.tvWorkingEveningHours.visibility = View.VISIBLE
        binding.layoutEveningTime.visibility = View.VISIBLE
        binding.layoutEveningTimeSlot.visibility = View.VISIBLE

        // Hide morning time layouts
        binding.layoutTime.visibility = View.GONE
        binding.etStartTime.visibility = View.GONE
        binding.etEndTime.visibility = View.GONE
    }

    private fun validateAndNavigate(selectedSession: Array<WorkingHours>?) {
        // Perform validation based on the available sessions
        if (selectedSession != null) {
            var isValid = true

            // Check morning session fields
            if (selectedSession.any { it.session.equals("morning", ignoreCase = true) }) {
                if (binding.etStartTime.text.isEmpty()) {
                    binding.etStartTime.error = "Please select morning start time"
                    isValid = false
                }
                if (binding.etEndTime.text.isEmpty()) {
                    binding.etEndTime.error = "Please select morning end time"
                    isValid = false
                }
            }

            // Check evening session fields
            if (selectedSession.any { it.session.equals("evening", ignoreCase = true) }) {
                if (binding.etStartEveningTime.text.isEmpty()) {
                    binding.etStartEveningTime.error = "Please select evening start time"
                    isValid = false
                }
                if (binding.etEveningEndTime.text.isEmpty()) {
                    binding.etEveningEndTime.error = "Please select evening end time"
                    isValid = false
                }
            }

            // If all checks pass, navigate
            if (isValid) {
                viewmodel.workingHoursNew.let { workingHoursList ->
                    workingHoursList.forEach { workingHour ->
                        when {
                            workingHour.session.equals("morning", ignoreCase = true) -> {
                                workingHour.startTime = binding.etStartTime.text.toString()
                                workingHour.endTime = binding.etEndTime.text.toString()
                            }
                            workingHour.session.equals("evening", ignoreCase = true) -> {
                                workingHour.startTime = binding.etStartEveningTime.text.toString()
                                workingHour.endTime = binding.etEveningEndTime.text.toString()
                            }
                        }
                    }
                }



                Log.d("print2", "workhoursNew: ${viewmodel.workingHoursNew.joinToString { it.toString() }}")
                val workingHoursArray: Array<WorkingHours> = viewmodel.workingHoursNew.toTypedArray()
                val action = PrivatePracticeWorkingHoursFragmentDirections
                    .actionPrivatePracticeWorkingHoursFragmentToConsultationDurationFragment(
                        doctorOnBoardNavDate = viewmodel.workingHoursNew.toTypedArray())
                findNavController().navigate(action)
            }
        }
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
