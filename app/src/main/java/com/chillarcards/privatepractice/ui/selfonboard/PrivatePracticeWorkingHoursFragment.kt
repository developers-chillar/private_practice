package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentPrivatePracticeWorkingHoursBinding


class PrivatePracticeWorkingHoursFragment :
    Fragment(R.layout.fragment_private_practice_working_hours) {

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
        val startMoriningTime = arrayOf("08:00", "09:00", "10:00", "11:00", "12:00", "13:00")
        val endMoriningTime = arrayOf("08:00", "09:00", "10:00", "11:00", "12:00", "13:00")
        val startEveningTime = arrayOf("04:00", "05:00", "06:00", "07:00", "08:00", "09:00")
        val endEveningTime = arrayOf("04:00", "05:00", "06:00", "07:00", "08:00", "09:00")

        val startMorningTimeAdapter =
            ArrayAdapter(requireContext(), R.layout.drop_down_items, startMoriningTime)
        val endMoriningTimeAdapter =
            ArrayAdapter(requireContext(), R.layout.drop_down_items, endMoriningTime)
        val startEveningTimeAdapter =
            ArrayAdapter(requireContext(), R.layout.drop_down_items, startEveningTime)
        val endEveningTimeAdapter =
            ArrayAdapter(requireContext(), R.layout.drop_down_items, endEveningTime)

        binding.etStartTime.setAdapter(startMorningTimeAdapter)
        binding.etStartTime.showDropDown()
        binding.etEndTime.setAdapter(endMoriningTimeAdapter)
        binding.etEndTime.showDropDown()

        binding.etStartEveningTime.setAdapter(startEveningTimeAdapter)
        binding.etStartEveningTime.showDropDown()
        binding.etEveningEndTime.setAdapter(endEveningTimeAdapter)
        binding.etEveningEndTime.showDropDown()

        binding.tvContinue.setOnClickListener {
            when {
                binding.etStartTime.text.toString().isEmpty() -> {
                    binding.etStartTime.error = "Please select start time"
                }

                binding.etEndTime.text.toString().isEmpty() -> {
                    binding.etEndTime.error = "Please select end time"
                }

                binding.etStartEveningTime.text.toString().isEmpty() -> {
                    binding.etStartEveningTime.error = "Please select start time"
                }

                binding.etEveningEndTime.text.toString().isEmpty() -> {
                    binding.etEveningEndTime.error = "Please select end time"
                }

                else -> {
                    findNavController().navigate(R.id.consultationDurationFragment)
                }
            }


        }
    }


}