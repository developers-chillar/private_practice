package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentRegistrationCompletedBinding
import com.chillarcards.privatepractice.utills.PrefManager
import com.chillarcards.privatepractice.utills.Status
import com.chillarcards.privatepractice.viewmodel.DrSpecialityViewModel
import com.chillarcards.privatepractice.viewmodel.RegistrationCompletedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrationCompletedFragment : Fragment(R.layout.fragment_registration_completed) {

    lateinit var binding: FragmentRegistrationCompletedBinding
    lateinit var prefManager: PrefManager
    private val viewmodel by viewModel<RegistrationCompletedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationCompletedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager(requireContext())
        viewmodel.run {
            viewmodel.doctorPhone.value = prefManager.getPhoneNumber()
            Log.d("PrefManager", "Doctor Phone: ${prefManager.getPhoneNumber()}")
            viewmodel.doctorName.value =prefManager.getDrName()
            Log.d("PrefManager", "Doctor Name: ${prefManager.getDrName()}")
            viewmodel.departmentId.value =prefManager.getDeptId()
            Log.d("PrefManager", "Department ID: ${prefManager.getDeptId()}")
            viewmodel.entityId.value = prefManager.getEntityId().toIntOrNull()
            Log.d("PrefManager", "Entity ID: ${prefManager.getIntEntityId()}")
            viewmodel.doctorId.value = prefManager.getDoctorId().toIntOrNull()
            Log.d("PrefManager", "Doctor ID: ${prefManager.getDoctorId().toIntOrNull() ?: 0}")
            viewmodel.consultationTime.value =prefManager.getConsultationDuration()
            Log.d("PrefManager", "Consultation Time: ${prefManager.getConsultationDuration()}")
            viewmodel.selectedDay.observe(viewLifecycleOwner) { day ->
                Log.d("Selected Day", "Selected day is: $day")
            }
           viewmodel.session.observe(viewLifecycleOwner){ session->
           Log.d("Selected session", "Selected session is: $session")
           }
            startTime.observe(viewLifecycleOwner) { startTime ->
                Log.d("Selected start time", "Selected start time is: $startTime")
            }

            endTime.observe(viewLifecycleOwner) { endTime ->
                Log.d("Selected end time", "Selected end time is: $endTime")
            }
            selfRegistrationCompleted()
        }
        getDoctorOnboard()
    }

    fun getDoctorOnboard(){
        try{
            viewmodel.registrationComplete.observe(viewLifecycleOwner){
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS->{
                            it.data?.let { doctorOnboardingResponseClass ->
                                when(doctorOnboardingResponseClass.statusCode){
                                    200->{
                                        lifecycleScope.launch {
                                            delay(3000)
                                            findNavController().navigate(RegistrationCompletedFragmentDirections.actionRegistrationCompletedFragmentToHomeBaseFragment())
                                        }

                                    }
                                    400->{
                                        Toast.makeText(requireContext(),doctorOnboardingResponseClass.message,Toast.LENGTH_SHORT).show()
                                    }

                                    else -> {
                                        Toast.makeText(requireContext(),doctorOnboardingResponseClass.message,Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }

                        Status.ERROR -> {
                            Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                        }
                        Status.LOADING -> {
                            Toast.makeText(requireContext(),"Loading",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        catch (e:Exception){
           e.printStackTrace()
        }
    }
}