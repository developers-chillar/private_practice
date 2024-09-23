package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentPrivatePracticeAvailableBinding
import com.chillarcards.privatepractice.viewmodel.RegistrationCompletedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PrivatePracticeAvailableFragment : Fragment(R.layout.fragment_private_practice_available) {

lateinit var binding: FragmentPrivatePracticeAvailableBinding
    private val viewmodel by viewModel<RegistrationCompletedViewModel>()
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
            if (binding.rgDays.checkedRadioButtonId == -1){
                Toast.makeText(requireContext(), "Please select one option", Toast.LENGTH_SHORT).show()
            }
            else{
                val selectedRadioButton: RadioButton = view.findViewById(binding.rgDays.checkedRadioButtonId)
                val selectSession = selectedRadioButton.text.toString()
                viewmodel.session.value = selectSession
                val action=PrivatePracticeAvailableFragmentDirections.actionPrivatePracticeAvailableFragmentToPrivatePracticeWorkingHoursFragment()
                findNavController().navigate(action)

            }
        }
    }

}