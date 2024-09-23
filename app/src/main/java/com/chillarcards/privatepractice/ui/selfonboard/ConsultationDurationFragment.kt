package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentConsultationDurationBinding
import com.chillarcards.privatepractice.databinding.FragmentPrivateConsultationBinding
import com.chillarcards.privatepractice.utills.PrefManager


class ConsultationDurationFragment : Fragment(R.layout.fragment_consultation_duration) {

lateinit var binding:FragmentConsultationDurationBinding
private var prefManager=PrefManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentConsultationDurationBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvContinue.setOnClickListener {
            if (binding.etGdName.text.isNullOrEmpty()){
                binding.etGdName.error="Enter Duration"
                return@setOnClickListener
            }
            else{
                val enteredName = binding.etGdName.text.toString().toIntOrNull()
                val prefManager = PrefManager(requireContext())
                enteredName?.let { it1 -> prefManager.setConsultationDuration(it1) }
                val action=ConsultationDurationFragmentDirections.actionConsultationDurationFragmentToRegistrationCompletedFragment()
                findNavController().navigate(action)

            }
        }
    }
}