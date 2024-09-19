package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentRegistrationCompletedBinding

class RegistrationCompletedFragment : Fragment(R.layout.fragment_registration_completed) {

    lateinit var binding: FragmentRegistrationCompletedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationCompletedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layoutRegistartionDone.setOnClickListener {
            findNavController().navigate(RegistrationCompletedFragmentDirections.actionRegistrationCompletedFragmentToHomeBaseFragment())
        }

    }

}