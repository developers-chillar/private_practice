package com.chillarcards.privatepractice.ui.selfonboard

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentGoodNameBinding
import com.chillarcards.privatepractice.utills.PrefManager
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class GoodNameFragment : Fragment(R.layout.fragment_good_name) {

lateinit var binding: FragmentGoodNameBinding
private var prefManager=PrefManager
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      binding=FragmentGoodNameBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val enteredName = binding.etGdName.text.toString()
        binding.etGdName.setOnClickListener {
            binding.etGdName.setText("Dr")
        }
        binding.tvContinue.setOnClickListener {
            val enteredName = binding.etGdName.text.toString()
            if (enteredName.isEmpty()) {
                Toast.makeText(requireContext(), "Please Enter Your Good Name", Toast.LENGTH_SHORT).show()
            } else {
                val prefManager = PrefManager(requireContext())
                prefManager.setDrName(enteredName)
                val action = GoodNameFragmentDirections.actionGoodNameFragmentToDrSpecialityFragment()
                findNavController().navigate(action)
            }
        }
    }


}