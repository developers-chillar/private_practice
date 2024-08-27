package com.chillarcards.bookmenow.ui.general

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chillarcards.bookmenow.R
import com.chillarcards.bookmenow.databinding.FragmentGeneralBinding
import com.chillarcards.bookmenow.ui.interfaces.IAdapterViewUtills
import com.chillarcards.bookmenow.utills.CommonDBaseModel
import com.chillarcards.bookmenow.utills.Const
import com.chillarcards.bookmenow.utills.PrefManager
import com.chillarcards.bookmenow.utills.Status
import com.chillarcards.bookmenow.viewmodel.GeneralViewModel
import com.chillarcards.bookmenow.viewmodel.RegisterViewModel
import com.chillarcards.bookmenow.viewmodel.StatusViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GeneralFragment : Fragment(), IAdapterViewUtills {

    lateinit var binding: FragmentGeneralBinding
    private val generalViewModel by viewModel<GeneralViewModel>()
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_general, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        prefManager = PrefManager(requireContext())
        Const.enableButton(binding.confirmBtn)

        generalViewModel.run {
            getGeneralSetting()
        }
        setUpObserver()

        binding.intervalFrm.setEndIconOnClickListener {
            binding.interval.setText("")
        }
        binding.profile.setOnClickListener{
            findNavController().navigate(
                GeneralFragmentDirections.actionGeneralFragmentToProfileFragment(
                )
            )
        }
        binding.bankFrm.setOnClickListener{
            findNavController().navigate(
                GeneralFragmentDirections.actionGeneralFragmentToBankFragment(
                )
            )
        }
        binding.workingHrs.setOnClickListener{
            findNavController().navigate(
                GeneralFragmentDirections.actionGeneralFragmentToTimeFragment(
                )
            )
        }
        binding.addStaff.setOnClickListener{
            findNavController().navigate(
                GeneralFragmentDirections.actionGeneralFragmentToModuleStaffFragment(
                )
            )
        }
        binding.addService.setOnClickListener{
            findNavController().navigate(
                GeneralFragmentDirections.actionGeneralFragmentToModuleServiceFragment(
                )
            )
        }
//        binding.shopStatus.setOnCheckedChangeListener( null )
//        setCheckBoxListener()

    }
    private fun setCheckBoxListener(){
        binding.shopStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            alertMsg(requireContext())
        }
    }
    private fun setToolbar() {
        binding.toolbar.toolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.toolbarTitle.text = getString(R.string.general)
    }
    private fun alertMsg(context: Context) {
        try {
            PrefManager(context)

            val message: String = if (generalViewModel.shopStatus.value == 1) { //OFF
                "Are you sure you want to change the status?"
            } else { //ON
                "Are you sure you want to change the status?"
            }

            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.alert_heading)
            builder.setMessage(message)
            builder.setIcon(R.mipmap.ic_launcher)
            builder.setCancelable(false)

            //performing positive action
            builder.setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                generalViewModel.run {
                    getStatus()
                }
            }
            builder.setNegativeButton(context.getString(R.string.cancel)) { _, _ ->
                binding.shopStatus.setOnCheckedChangeListener( null )
                binding.shopStatus.isChecked = (generalViewModel.shopStatus.value == 1)
                setCheckBoxListener()

            }
            val alertDialog: AlertDialog = builder.create()

            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()
        } catch (e: Exception) {
            //e.printstackTrace()
        }
    }

    private fun setUpObserver() {
    try {
        generalViewModel.settingData.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.status) {
                    Status.SUCCESS -> {
                        hideProgress()
                        it.data?.let { settingData ->
                            when (settingData.statusCode) {
                                200 -> {
                                    binding.shopStatus.setOnCheckedChangeListener( null )
                                    binding.shopStatus.isChecked = (settingData.data.entityStatus == 1)
                                    setCheckBoxListener()
                                    if (settingData.data.entityStatus == 0) {
                                        binding.shopStatus.setTextColor(resources.getColor(R.color.theme_end))
                                    }else{
                                        binding.shopStatus.setTextColor(resources.getColor(R.color.onoff))
                                    }

                                    //binding.interval.setText("10")
                                    generalViewModel.shopStatus.value = settingData.data.entityStatus
                                    prefManager.setDoctorId(settingData.data.doctor_id.toString())
                                }
                                else -> Const.shortToast(requireContext(), settingData.message)

                            }
                        }
                    }
                    Status.LOADING -> {
                        showProgress()
                    }
                    Status.ERROR -> {
                        hideProgress()
                        prefManager.setRefresh("1")
                        val authViewModel by viewModel<RegisterViewModel>()
                        Const.getNewTokenAPI(
                            requireContext(),
                            authViewModel,
                            viewLifecycleOwner
                        )

//                            profileViewModel.run {
//                                mob.value = prefManager.getMobileNo()
//                                getProfile()
//                            }
//                            setUpObserver()
                    }
                }
            }
        }
        generalViewModel.statusData.observe(viewLifecycleOwner) {
            if (it != null) {
                when (it.status) {
                    Status.SUCCESS -> {
                        hideProgress()
                        it.data?.let { settingData ->
                            when (settingData.statusCode) {
                                200 -> {
                                    generalViewModel.run {
                                        getGeneralSetting()
                                    }
                                }
                                else -> Const.shortToast(requireContext(), settingData.message)

                            }
                        }
                    }
                    Status.LOADING -> {
                        showProgress()
                    }
                    Status.ERROR -> {
                        hideProgress()
                        prefManager.setRefresh("1")
                        val authViewModel by viewModel<RegisterViewModel>()
                        Const.getNewTokenAPI(
                            requireContext(),
                            authViewModel,
                            viewLifecycleOwner
                        )
                    }
                }
            }
        }

    } catch (e: Exception) {
        Log.e("abc_otp", "setUpObserver: ", e)
    }
    }

    private fun showProgress() {
    //    binding.confirmBtn.visibility = View.INVISIBLE
        binding.otpProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
     //   binding.confirmBtn.visibility = View.VISIBLE
        binding.otpProgress.visibility = View.GONE
    }

    override fun getAdapterPosition(
        Position: Int,
        ValueArray: ArrayList<CommonDBaseModel>,
        Mode: String?
    ) {
//        if(Mode.equals("VIEW")) {
//            val bottomSheetFragment = BottomSheetFragment(ValueArray)
//            bottomSheetFragment.show(
//                (context as AppCompatActivity).supportFragmentManager,
//                bottomSheetFragment.tag
//            )
//        }
    }
}