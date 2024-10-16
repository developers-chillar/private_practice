package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.data.model.WorkingHours
import com.chillarcards.privatepractice.databinding.FragmentConsultationDurationBinding
import com.chillarcards.privatepractice.databinding.FragmentPrivateConsultationBinding
import com.chillarcards.privatepractice.utills.PrefManager
import com.chillarcards.privatepractice.viewmodel.RegistrationCompletedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ConsultationDurationFragment : Fragment(R.layout.fragment_consultation_duration) {
    private val viewmodel by viewModel<RegistrationCompletedViewModel>()
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
        val args = PrivatePracticeAvailableFragmentArgs.fromBundle(requireArguments())
        val workingHoursArray = args.doctorOnBoardNavDate
        val selectedSession = args.doctorOnBoardNavDate
        val time = args.doctorOnBoardNavDate
        Log.d("time", "workingHoursArray: ${time.contentToString()}")
        binding.tvContinue.setOnClickListener {
            if (binding.etGdName.text.isNullOrEmpty()) {
                binding.etGdName.error = "Enter Duration"
                return@setOnClickListener
            } else {
                val enteredName = binding.etGdName.text.toString().toIntOrNull()
                if (enteredName != null) {
                    // Log the entered consultation duration
                    Log.d("ConsultationDuration", "Entered Consultation Duration: $enteredName")

                    val prefManager = PrefManager(requireContext())
                    prefManager.setConsultationDuration(enteredName)

                    val action =
                        ConsultationDurationFragmentDirections.actionConsultationDurationFragmentToRegistrationCompletedFragment(
                            doctorOnBoardNavDate = viewmodel.workingHoursNew.toTypedArray(),
                            consultationDuration = enteredName.toString()
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }
}