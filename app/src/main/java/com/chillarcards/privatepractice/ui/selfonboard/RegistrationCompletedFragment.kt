package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentRegistrationCompletedBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        lifecycleScope.launch {
            delay(3000)

            findNavController().navigate(RegistrationCompletedFragmentDirections.actionRegistrationCompletedFragmentToHomeBaseFragment())
        }
    }
}