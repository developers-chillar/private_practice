package com.chillarcards.privatepractice.ui.general

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.chillarcards.privatepractice.MainActivity
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentGeneralHomeBinding
import com.chillarcards.privatepractice.ui.adapter.ClinicAdapter
import com.chillarcards.privatepractice.ui.interfaces.IAdapterViewUtills
import com.chillarcards.privatepractice.utills.CommonDBaseModel
import com.chillarcards.privatepractice.utills.Const
import com.chillarcards.privatepractice.utills.PrefManager
import com.chillarcards.privatepractice.utills.Status
import com.chillarcards.privatepractice.viewmodel.GeneralViewModel
import com.chillarcards.privatepractice.viewmodel.RegisterViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeGeneralFragment : Fragment(), IAdapterViewUtills {

    lateinit var binding: FragmentGeneralHomeBinding
    private val generalViewModel by viewModel<GeneralViewModel>()
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       //  return binding.root
        binding = FragmentGeneralHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager(requireContext())

        generalViewModel.run {
            getGeneralSetting()
        }
        setUpObserver()


        binding.profile.setOnClickListener{
            findNavController().navigate(
                HomeGeneralFragmentDirections.actionGeneralFragmentToRegisterFragment(
                )
            )
        }
        binding.workingHrs.setOnClickListener{
            findNavController().navigate(
                HomeGeneralFragmentDirections.actionGeneralFragmentToTimeFragment(
                    generalViewModel.doctorID.value
                )
            )
        }
        binding.bankFrm.setOnClickListener{
            findNavController().navigate(
                HomeGeneralFragmentDirections.actionGeneralFragmentToBankFragment(
                )
            )
        }
        binding.confirmBtn.setOnClickListener{
            findNavController().navigate(
                HomeGeneralFragmentDirections.actionGeneralFragmentToHomeFragment(
                )
            )
        }

        binding.logout.setOnClickListener {
          //  setBottomSheet()
        }

    }

    private fun setCheckBoxListener(){
        binding.shopStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            alertMsg(requireContext())
        }
    }
   // private fun alertMsg(context: Context, message :String) {
    private fun alertMsg(context: Context) {
        try {
            PrefManager(context)

            val message: String = if (generalViewModel.shopStatus.value == 1) {
                "Are you sure you want to OFF the booking link ?"
            } else {
                "Are you sure you want to ON the booking link ?"
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
            // Close the dialog when OK is clicked
            builder.setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                alertDialog.dismiss()
            }
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

                                        generalViewModel.shopStatus.value = settingData.data.entityStatus
                                        generalViewModel.doctorID.value = settingData.data.doctor_id.toString()
                                        prefManager.setDoctorId(settingData.data.doctor_id.toString())

                                        if(settingData.data.profile_completed==1) {
                                            binding.confirmBtn.visibility =View.VISIBLE
                                            Const.enableButton(binding.confirmBtn)
                                        }else{
                                            binding.confirmBtn.visibility =View.GONE
                                        }

                                        if(settingData.data.entityDetails.isNotEmpty()) {
                                            binding.topStaffFrame.visibility=View.VISIBLE

                                            val salesTopPicAdapter = ClinicAdapter(
                                                settingData.data.entityDetails,
                                                requireContext(),
                                                this@HomeGeneralFragment
                                            )
                                            binding.topPicRv.adapter = salesTopPicAdapter
                                            binding.topPicRv.layoutManager = LinearLayoutManager(
                                                context,
                                                LinearLayoutManager.HORIZONTAL,
                                                false
                                            )
                                        }
                                        else{
                                            binding.topStaffFrame.visibility=View.GONE
                                        }
                                    }
                                    422 -> {
                                        Const.shortToast(requireContext(), "Profile is not yet completed")
                                        binding.confirmBtn.visibility=View.GONE
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
             //   generalViewModel.clear()
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
             //   generalViewModel.clear()
            }

        } catch (e: Exception) {
            Log.e("abc_otp", "setUpObserver: ", e)
        }
    }

    private fun showProgress() {
        binding.confirmBtn.visibility = View.INVISIBLE
        binding.otpProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.confirmBtn.visibility = View.VISIBLE
        binding.otpProgress.visibility = View.GONE
    }


    override fun getAdapterPosition(
        Position: Int,
        ValueArray: ArrayList<CommonDBaseModel>,
        Mode: String?
    ) {
        if(Mode.equals("STAFFVIEW")) {
            val entityId = ValueArray[0].mastIDs.toString()
            prefManager.setEntityId(entityId)
            Log.d("entityIDhomefra",entityId)

            findNavController().navigate(
                HomeGeneralFragmentDirections.actionGeneralFragmentToHomeFragment(

                )
            )

        }
    }

//    private fun setBottomSheet() {
//
//        val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.logout, null)
//        val bottomSheetDialog = BottomSheetDialog(requireContext())
//        bottomSheetDialog.setContentView(bottomSheetView)
//
//        val completeButton: TextView = bottomSheetView.findViewById(R.id.cancelButton)
//        completeButton.setOnClickListener {
//
//            bottomSheetDialog.dismiss()
//        }
//
//        val callButton: TextView = bottomSheetView.findViewById(R.id.okButton)
//        callButton.setOnClickListener {
//            performLogout()
//            bottomSheetDialog.dismiss()
//        }
//        bottomSheetDialog.show()
//    }

    private fun performLogout() {

        // Clear any user session or authentication data
        clearUserSession()

        Log.d("abc_home", "showLogoutAlert: recreating activity.. all data cleared")
        val intent = Intent(this.requireContext(), MainActivity::class.java)
        ActivityCompat.finishAffinity(requireActivity())
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)


    }

    private fun clearUserSession() {
        val prefManager = PrefManager(requireContext())
        prefManager.clearAll()
        prefManager.setIsLoggedIn(false)
    }

}