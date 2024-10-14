package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentRegistrationCompletedBinding
import com.chillarcards.privatepractice.utills.Const
import com.chillarcards.privatepractice.utills.PrefManager
import com.chillarcards.privatepractice.utills.Status
import com.chillarcards.privatepractice.viewmodel.RegisterViewModel
import com.chillarcards.privatepractice.viewmodel.RegistrationCompletedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrationCompletedFragment : Fragment(R.layout.fragment_registration_completed) {

    lateinit var binding: FragmentRegistrationCompletedBinding
    lateinit var prefManager: PrefManager
    private val viewmodel by viewModel<RegistrationCompletedViewModel>()
    private val args: RegistrationCompletedFragmentArgs by navArgs()
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
            viewmodel.doctorPhone.value = prefManager.getMobileNo()
            viewmodel.doctorName.value =prefManager.getDrName()
            viewmodel.departmentId.value =prefManager.getDeptId()
            viewmodel.entityId.value = prefManager.getEntityId().takeIf { it.isNotEmpty() && it != "null" }?.toInt() ?: 0
            Log.d("prefernceses","entityId is:${viewmodel.entityId.value}")
            viewmodel.doctorId.value = prefManager.getDoctorId().takeIf { it.isNotEmpty() && it != "null" }?.toInt() ?: 0
            Log.d("prefernceses","doctorId is:${viewmodel.doctorId.value}")
            viewmodel.consultationTime.value =prefManager.getConsultationDuration()
            viewmodel.selectedDay.observe(viewLifecycleOwner) { day ->
            }
           viewmodel.session.observe(viewLifecycleOwner){ session->
           }
            startTime.observe(viewLifecycleOwner) { startTime ->
            }

            endTime.observe(viewLifecycleOwner) { endTime ->
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
                                            prefManager.setToken(it.data.data.access_token.trim())
                                            Log.d("ref_compl_token","tag:${prefManager.getToken()}")
                                            prefManager.setRefToken(it.data.data.refresh_token.trim())
                                            Log.d("ref_compl_token_refresh","tag:${prefManager.getToken()}")
                                            prefManager.setIsLoggedIn(true)
                                            prefManager.setRefresh("0")
                                            findNavController().navigate(RegistrationCompletedFragmentDirections.actionRegistrationCompletedFragmentToHomeFragment())

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
                            Toast.makeText(requireContext(),"Token not updated in register login regi completed frag",Toast.LENGTH_SHORT).show()
                            Log.d("TokenLog", "403: Token expired, refreshing token1")
                            prefManager.setRefresh("1")
                            val authViewModel by viewModel<RegisterViewModel>()
                            Const.getNewTokenAPI(
                                requireContext(),
                                authViewModel,
                                viewLifecycleOwner
                            )
                        }
                        Status.LOADING -> {
                          //  Toast.makeText(requireContext(),"Loading",Toast.LENGTH_SHORT).show()
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