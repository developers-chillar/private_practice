package com.chillarcards.privatepractice.ui.selfonboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentDrSpecialityBinding
import com.chillarcards.privatepractice.utills.Const
import com.chillarcards.privatepractice.utills.PrefManager
import com.chillarcards.privatepractice.utills.Status
import com.chillarcards.privatepractice.viewmodel.DrSpecialityViewModel
import com.chillarcards.privatepractice.viewmodel.MobileScreenViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class DrSpecialityFragment : Fragment(R.layout.fragment_dr_speciality) {
lateinit var binding: FragmentDrSpecialityBinding
lateinit var prefManager: PrefManager
    private val drSpecialityViewModel by viewModel<DrSpecialityViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=FragmentDrSpecialityBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager=PrefManager(requireContext())
        drSpecialityViewModel.run {
            drSpecialityViewModel.getDrSpecialized()
            drSpecialityObserver()

        }
        binding.etGdName.setOnClickListener {
            binding.etGdName.showDropDown()
        }
    }

    private fun drSpecialityObserver(){
        try{
            drSpecialityViewModel.drSpecilaities.observe(viewLifecycleOwner){
                if (it!=null){
                    when(it.status){
                        Status.SUCCESS->{
                            it.data?.let { drSpecialities ->
                                when(drSpecialities.statusCode){
                                    200->{
                                      val adapter=ArrayAdapter(requireContext(),R.layout.drop_down_items,drSpecialities.data.map { it.department_name })
                                        binding.etGdName.setAdapter(adapter)
                                       // binding.etGdName.showDropDown()
                                        val deptId=drSpecialities.data.map { it.department_id }
                                        prefManager.setDeptId(deptId[0])
                                        binding.tvContinue.setOnClickListener {
                                            if (binding.etGdName.text.toString().isEmpty()) {
                                                Toast.makeText(requireContext(),"Choose At least one option",Toast.LENGTH_SHORT).show()

                                            } else {
                                                val action=DrSpecialityFragmentDirections.actionDrSpecialityFragmentToPrivateConsultationFragment(
                                                )
                                               findNavController().navigate(action)
                                            }
                                        }
                                    }

                                }
                            }
                        }

                        Status.ERROR -> {}
                        Status.LOADING -> {}
                    }
                }
            }

        }
        catch(e:Exception){
            e.printStackTrace()
        }
    }


}