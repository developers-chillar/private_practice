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


class GoodNameFragment : Fragment(R.layout.fragment_good_name) {

lateinit var binding: FragmentGoodNameBinding
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      binding=FragmentGoodNameBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvContinue.setOnClickListener {
            if (binding.tvContinue.text.isNullOrEmpty()){
                Toast.makeText(requireContext(),"Please Enter Your Good Name",Toast.LENGTH_SHORT).show()
            }
            else {
                findNavController().navigate(GoodNameFragmentDirections.actionGoodNameFragmentToDrSpecialityFragment())
            }
        }
    }


}