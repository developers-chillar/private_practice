package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.data.model.WorkingHours
import com.chillarcards.privatepractice.databinding.FragmentPrivatePracticeAvailableBinding
import com.chillarcards.privatepractice.viewmodel.RegistrationCompletedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PrivatePracticeAvailableFragment : Fragment(R.layout.fragment_private_practice_available) {

lateinit var binding: FragmentPrivatePracticeAvailableBinding
    private val viewmodel by viewModel<RegistrationCompletedViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=FragmentPrivatePracticeAvailableBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = PrivatePracticeAvailableFragmentArgs.fromBundle(requireArguments())
        val workingHoursArrayArgs = args.doctorOnBoardNavDate
        if (workingHoursArrayArgs != null) {
            viewmodel.workingHoursNew=workingHoursArrayArgs.toMutableList()
        }

        binding.tvContinue.setOnClickListener {
            if (binding.rgDays.checkedRadioButtonId == -1){
                Toast.makeText(requireContext(), "Please select one option", Toast.LENGTH_SHORT).show()
            }
            else{
                val selectedRadioButton: RadioButton = view.findViewById(binding.rgDays.checkedRadioButtonId)
                when (selectedRadioButton.id) {
                    R.id.rdMoring -> {
                        val selectSession = selectedRadioButton.text.toString()

                        viewmodel.session.value = selectSession
                        Log.d("SelectedSession", "Session selected: $selectSession")
                        viewmodel.workingHoursNew.let {
                            it.forEach {
                                it.session = selectSession
                            }
                        }
                        Log.d("SessionSelection", "Morning session selected")
                    }

                    R.id.rdEvening -> {
                        val selectSession = selectedRadioButton.text.toString()

                        viewmodel.session.value = selectSession
                        Log.d("SelectedSession", "Session selected: $selectSession")
                        viewmodel.workingHoursNew.let {
                            it.forEach {
                                it.session = selectSession
                            }
                        }
                        Log.d("SessionSelection", "Evening session selected")
                    }

                    R.id.rdBoth -> {
                        val selectSession = selectedRadioButton.text.toString()
                        viewmodel.session.value = selectSession
                        Log.d("print4.1", "Session selected: $selectSession")
                        Log.d("print4", "workhoursNew: ${viewmodel.workingHoursNew.joinToString { it.toString() }}")
                        if(selectSession=="Both"){
                            duplicateData()
                        }
                        else{
                            viewmodel.workingHoursNew.let {
                                it.forEach {
                                    it.session = selectSession
                                }
                            }

                        }


//                        val duplicateWorkingHours = viewmodel.workingHoursNew.flatMap { listOf(it, it) }.toTypedArray()
//                        viewmodel.workingHoursNew = duplicateWorkingHours.toMutableList()
                       Log.d("print5", "duplicateWorkingHours: ${viewmodel.workingHoursNew.joinToString { it.toString() }}")
/*
                        viewmodel.workingHoursNew.let {
                            it.forEach {
                                it.session = selectSession
                            }
                        }
*/
                        Log.d("print6", "Both morning and evening sessions selected")
                    }

                    else -> {
                        Log.d("SessionSelection", "No valid session selected")
                    }
                }

                Log.d("print1", "workhoursNew: ${viewmodel.workingHoursNew.joinToString { it.toString() }}")
//                val workingHoursArray: Array<WorkingHours> = viewmodel.workingHoursNew.toTypedArray()
                val action=PrivatePracticeAvailableFragmentDirections.actionPrivatePracticeAvailableFragmentToPrivatePracticeWorkingHoursFragment(
                    doctorOnBoardNavDate = viewmodel.workingHoursNew.toTypedArray()
                )
                findNavController().navigate(action)

            }
        }
    }

    private fun duplicateData() {

        val duplicateWorkingHours = mutableListOf<WorkingHours>()

        viewmodel.workingHoursNew.forEach { workingHour ->
            // Create two new WorkingHour objects, one for morning and one for evening
            val morningHour = workingHour.copy(session = "morning")
            val eveningHour = workingHour.copy(session = "evening")

            // Add to the list
            duplicateWorkingHours.add(morningHour)
            duplicateWorkingHours.add(eveningHour)
        }

        // If you need to convert it to an array
        val duplicateWorkingHoursArray: Array<WorkingHours> = duplicateWorkingHours.toTypedArray()

        viewmodel.workingHoursNew = duplicateWorkingHoursArray.toMutableList()
        Log.d("print7", "workhoursNew: ${viewmodel.workingHoursNew.joinToString { it.toString() }}")

    }

}