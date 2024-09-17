package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentWelcomeOnBoardScreenBinding

class WelcomeOnBoardScreenFragment : Fragment(R.layout.fragment_welcome_on_board_screen) {
    lateinit var binding:FragmentWelcomeOnBoardScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentWelcomeOnBoardScreenBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layoutRegistration.setOnClickListener {
            findNavController().navigate(WelcomeOnBoardScreenFragmentDirections.actionWelcomeOnBoardScreenFragmentToMobileFragment())
        }
    }

}