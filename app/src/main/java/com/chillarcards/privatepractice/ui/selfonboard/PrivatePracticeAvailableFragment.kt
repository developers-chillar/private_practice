package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentPrivatePracticeAvailableBinding

class PrivatePracticeAvailableFragment : Fragment(R.layout.fragment_private_practice_available) {

lateinit var binding: FragmentPrivatePracticeAvailableBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=FragmentPrivatePracticeAvailableBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvContinue.setOnClickListener {
            findNavController().navigate(PrivatePracticeAvailableFragmentDirections.actionPrivatePracticeAvailableFragmentToPrivatePracticeWorkingHoursFragment())
        }
    }

}