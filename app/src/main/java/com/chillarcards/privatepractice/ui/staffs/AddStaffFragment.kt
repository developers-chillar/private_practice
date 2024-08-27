package com.chillarcards.privatepractice.ui.staffs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentAddStaffBinding
import com.chillarcards.privatepractice.utills.Const

class AddStaffFragment : Fragment() {

    lateinit var binding: FragmentAddStaffBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddStaffBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        Const.enableButton(binding.confirmBtn)

        binding.confirmBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpObserver() {

    }

    private fun setToolbar() {
        binding.toolbar.toolbarTitle.text = getString(R.string.add_staff_head)
        binding.toolbar.toolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("abc_mob", "onStop: ")
       // mobileViewModel.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("abc_mob", "onDestroy: ")
    }
}