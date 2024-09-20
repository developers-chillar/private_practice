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

class PrivateConsultationFragment : Fragment(R.layout.fragment_private_consultation), View.OnClickListener {
    lateinit var binding: FragmentPrivateConsultationBinding
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
                findNavController().navigate(PrivateConsultationFragmentDirections.actionPrivateConsultationFragmentToPrivatePracticeAvailableFragment())
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
           }
           R.id.tvMon->{
               binding.tvMon.isSelected=!binding.tvMon.isSelected
               weekClick()
           }
           R.id.tvTue->{
               binding.tvTue.isSelected=!binding.tvTue.isSelected
               weekClick()
           }
           R.id.tvWed->{
               binding.tvWed.isSelected=!binding.tvWed.isSelected
               weekClick()
           }

           R.id.tvThu->{
               binding.tvThu.isSelected=!binding.tvThu.isSelected
               weekClick()
           }

           R.id.tvFri->{
               binding.tvFri.isSelected=!binding.tvFri.isSelected
               weekClick()
           }
           R.id.tvSat->{
               binding.tvSat.isSelected=!binding.tvSat.isSelected
               weekClick()
           }
           else->{
               Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
           }
       }
    }


}