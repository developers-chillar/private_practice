package com.chillarcards.privatepractice.ui.editdatetime

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentEditDateTimeBinding
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView

class EditDateTimeFragment : Fragment(R.layout.fragment_edit_date_time) {
    lateinit var binding: FragmentEditDateTimeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=FragmentEditDateTimeBinding.inflate(layoutInflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomAppBar = activity?.findViewById<BottomAppBar>(R.id.bottomAppBar)
        bottomAppBar?.visibility = View.GONE
        binding.toolbar.toolbarBack.setOnClickListener {
            findNavController().popBackStack()
          //  findNavController().navigate(EditDateTimeFragmentDirections.actionEditDateTimeFragmentToHomeFragment())
        }
        binding.toolbar.toolbarTitle.text="Edit Working Days/Hours"
        binding.switch1.isChecked = true
       binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Switch is ON, enable the layout
                binding.layoutTime.isEnabled = true
                binding.layoutTime.alpha = 1f // Optional: set to full opacity
            } else {
                // Switch is OFF, disable the layout
                binding.layoutTime.isEnabled = false
                binding.layoutTime.alpha = 0.5f // Optional: visually indicate disabled state
            }
        }

    }

}