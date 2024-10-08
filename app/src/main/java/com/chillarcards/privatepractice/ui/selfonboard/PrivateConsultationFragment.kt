package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentPrivateConsultationBinding
import com.chillarcards.privatepractice.viewmodel.RegistrationCompletedViewModel
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
            if (isAnyDaySelected()) {
                val action = PrivateConsultationFragmentDirections.actionPrivateConsultationFragmentToPrivatePracticeAvailableFragment()
                findNavController().navigate(action)
            } else {
                Toast.makeText(requireContext(), "Please select at least one day", Toast.LENGTH_SHORT).show()
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
        // Set the selected day in the ViewModel, or manage selected days in a list
        viewmodel.selectedDay.value = day
      //  Toast.makeText(requireContext(), "$day selected", Toast.LENGTH_SHORT).show()
    }
}
