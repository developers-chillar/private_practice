package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding=FragmentPrivateConsultationBinding.inflate(layoutInflater,container,false)
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
            if (isAnyDaySelected()){
                val action=PrivateConsultationFragmentDirections.actionPrivateConsultationFragmentToPrivatePracticeAvailableFragment()
              findNavController().navigate(action)
            }
            else{
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

    private fun weekClick() {
        resetWeekBackgrounds()

        when {
            binding.tvSun.isSelected -> {

                binding.tvSun.setBackgroundResource(R.drawable.background_orange)
                binding.tvSun.setTextColor(resources.getColor(R.color.white))
            }
            binding.tvMon.isSelected -> {
                binding.tvMon.setBackgroundResource(R.drawable.background_orange)
                binding.tvMon.setTextColor(resources.getColor(R.color.white))
            }
            binding.tvTue.isSelected -> {
                binding.tvTue.setBackgroundResource(R.drawable.background_orange)
                binding.tvTue.setTextColor(resources.getColor(R.color.white))
            }
            binding.tvWed.isSelected -> {
                binding.tvWed.setBackgroundResource(R.drawable.background_orange)
                binding.tvWed.setTextColor(resources.getColor(R.color.white))
            }
            binding.tvThu.isSelected -> {
                binding.tvThu.setBackgroundResource(R.drawable.background_orange)
                binding.tvThu.setTextColor(resources.getColor(R.color.white))
            }
            binding.tvFri.isSelected -> {
                binding.tvFri.setBackgroundResource(R.drawable.background_orange)
                binding.tvFri.setTextColor(resources.getColor(R.color.white))
            }
            binding.tvSat.isSelected -> {
                binding.tvSat.setBackgroundResource(R.drawable.background_orange)
                binding.tvSat.setTextColor(resources.getColor(R.color.white))
            }
        }
    }

    private fun resetWeekBackgrounds() {
        binding.tvSun.setBackgroundResource(R.drawable.background_grey)
        binding.tvSun.setTextColor(resources.getColor(R.color.black))

        binding.tvMon.setBackgroundResource(R.drawable.background_grey)
        binding.tvMon.setTextColor(resources.getColor(R.color.black))

        binding.tvTue.setBackgroundResource(R.drawable.background_grey)
        binding.tvTue.setTextColor(resources.getColor(R.color.black))

        binding.tvWed.setBackgroundResource(R.drawable.background_grey)
        binding.tvWed.setTextColor(resources.getColor(R.color.black))

        binding.tvThu.setBackgroundResource(R.drawable.background_grey)
        binding.tvThu.setTextColor(resources.getColor(R.color.black))

        binding.tvFri.setBackgroundResource(R.drawable.background_grey)
        binding.tvFri.setTextColor(resources.getColor(R.color.black))

        binding.tvSat.setBackgroundResource(R.drawable.background_grey)
        binding.tvSat.setTextColor(resources.getColor(R.color.black))
    }

    override fun onClick(p0: View?) {
       when(p0?.id){
           R.id.tvSun->{
               binding.tvSun.isSelected=!binding.tvSun.isSelected
               weekClick()
               handleDaySelection("Sunday")
           }
           R.id.tvMon->{
               binding.tvMon.isSelected=!binding.tvMon.isSelected
               weekClick()
               handleDaySelection("Monday")
           }
           R.id.tvTue->{
               binding.tvTue.isSelected=!binding.tvTue.isSelected
               weekClick()
               handleDaySelection("Tuesday")
           }
           R.id.tvWed->{
               binding.tvWed.isSelected=!binding.tvWed.isSelected
               weekClick()
               handleDaySelection("Wednesday")
           }

           R.id.tvThu->{
               binding.tvThu.isSelected=!binding.tvThu.isSelected
               weekClick()
               handleDaySelection("Thursday")
           }

           R.id.tvFri->{
               binding.tvFri.isSelected=!binding.tvFri.isSelected
               weekClick()
               handleDaySelection("Friday")

           }
           R.id.tvSat->{
               binding.tvSat.isSelected=!binding.tvSat.isSelected
               weekClick()
               handleDaySelection("Saturday")
           }
           else->{
               Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
           }
       }
    }

    private fun handleDaySelection(day: String) {
        // Set the selected day in the ViewModel
        viewmodel.selectedDay.value = day
        Toast.makeText(requireContext(), "$day selected", Toast.LENGTH_SHORT).show()

        // Optionally, update the UI to show the selected day
        highlightSelectedDay(day)
    }
    private fun highlightSelectedDay(day: String) {
        // Reset all day TextViews to default state
        binding.tvSun.isSelected = false
        binding.tvMon.isSelected = false
        binding.tvTue.isSelected = false
        binding.tvWed.isSelected = false
        binding.tvThu.isSelected = false
        binding.tvFri.isSelected = false
        binding.tvSat.isSelected = false

        // Highlight the selected day
        when (day) {
            "Sunday" -> binding.tvSun.isSelected = true
            "Monday" -> binding.tvMon.isSelected = true
            "Tuesday" -> binding.tvTue.isSelected = true
            "Wednesday" -> binding.tvWed.isSelected = true
            "Thursday" -> binding.tvThu.isSelected = true
            "Friday" -> binding.tvFri.isSelected = true
            "Saturday" -> binding.tvSat.isSelected = true
        }
    }


}