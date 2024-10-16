package com.chillarcards.privatepractice.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.data.model.EntityDetail
import com.chillarcards.privatepractice.databinding.FragmentHomeBaseBinding
import com.chillarcards.privatepractice.ui.adapter.BookingAdapter
import com.chillarcards.privatepractice.ui.adapter.ClinicAdapter
import com.chillarcards.privatepractice.ui.interfaces.IAdapterViewUtills
import com.chillarcards.privatepractice.utills.CommonDBaseModel
import com.chillarcards.privatepractice.utills.Const
import com.chillarcards.privatepractice.utills.PrefManager
import com.chillarcards.privatepractice.utills.Status
import com.chillarcards.privatepractice.viewmodel.BookingViewModel
import com.chillarcards.privatepractice.viewmodel.RegisterViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.Calendar
import kotlin.system.exitProcess

class HomeBaseFragment : Fragment(), IAdapterViewUtills {
    lateinit var binding: FragmentHomeBaseBinding
    private lateinit var prefManager: PrefManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_base, container, false)
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager=PrefManager(requireContext())
        val navHostFragment = childFragmentManager.findFragmentById(R.id.inner_host_nav) as NavHostFragment
       val navController = navHostFragment.navController
//        navController.navigate(R.id.homeFragment)
        if (prefManager.isLoggedIn()){
            val navHostFragment = childFragmentManager
                .findFragmentById(R.id.inner_host_nav) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.homeFragment)
            Log.d("prefmanager Status",prefManager.isLoggedIn().toString())
        }
        else{
            Toast.makeText(requireContext(),"not loaded data",Toast.LENGTH_SHORT).show()
            Log.d("prefmanager-Status",prefManager.isLoggedIn().toString())
        }
        binding.home.setOnClickListener {
            navController.navigate(R.id.homeFragment)
 //           refreshFragment()
//            binding.home.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_theme_color))
//            val drawable = ContextCompat.getDrawable(requireContext(),R.drawable.home)?.mutate()
//            drawable?.setTint(ContextCompat.getColor(requireContext(), R.color.app_theme_color))
//            binding.home.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
//
//            binding.setting.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            val settingDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_setting)?.mutate()
//            settingDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
//            binding.setting.setCompoundDrawablesWithIntrinsicBounds(null, settingDrawable, null, null)
//
//            binding.addProfile.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            val addProfileDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_profile)?.mutate()
//            addProfileDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
//            binding.addProfile.setCompoundDrawablesWithIntrinsicBounds(null, addProfileDrawable, null, null)
//
//            binding.walkBook.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            val walkBookDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_calender)?.mutate()
//            walkBookDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
//            binding.walkBook.setCompoundDrawablesWithIntrinsicBounds(null, walkBookDrawable, null, null)


        }
        binding.report.setOnClickListener {
            navController.navigate(R.id.reportFragment)
        }
        binding.setting.setOnClickListener {
            navController.navigate(R.id.generalFragment)
//            binding.setting.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_theme_color))
//            val drawable = ContextCompat.getDrawable(requireContext(),R.drawable.ic_setting)?.mutate()
//            drawable?.setTint(ContextCompat.getColor(requireContext(), R.color.app_theme_color))
//            binding.setting.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
//
//            binding.home.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            val homeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.home)?.mutate()
//            homeDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
//            binding.setting.setCompoundDrawablesWithIntrinsicBounds(null, homeDrawable, null, null)
//
//            binding.addProfile.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            val addProfileDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_profile)?.mutate()
//            addProfileDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
//            binding.addProfile.setCompoundDrawablesWithIntrinsicBounds(null, addProfileDrawable, null, null)
//
//            binding.walkBook.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            val walkBookDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_calender)?.mutate()
//            walkBookDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
//            binding.walkBook.setCompoundDrawablesWithIntrinsicBounds(null, walkBookDrawable, null, null)
        }
        binding.addProfile.setOnClickListener {
            navController.navigate(R.id.profileFragment)
//            binding.addProfile.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_theme_color))
//            val drawable = ContextCompat.getDrawable(requireContext(),R.drawable.ic_profile)?.mutate()
//            drawable?.setTint(ContextCompat.getColor(requireContext(), R.color.app_theme_color))
//            binding.addProfile.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
//
//            binding.home.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            val homeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.home)?.mutate()
//            homeDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
//            binding.setting.setCompoundDrawablesWithIntrinsicBounds(null, homeDrawable, null, null)
//
//            binding.setting.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            val settingDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_setting)?.mutate()
//            settingDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
//            binding.addProfile.setCompoundDrawablesWithIntrinsicBounds(null, settingDrawable, null, null)
//
//            binding.walkBook.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            val walkBookDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_calender)?.mutate()
//            walkBookDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
//            binding.walkBook.setCompoundDrawablesWithIntrinsicBounds(null, walkBookDrawable, null, null)
        }

        binding.walkBook.setOnClickListener {
//            binding.walkBook.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_theme_color))
//            val drawable = ContextCompat.getDrawable(requireContext(),R.drawable.ic_calender)?.mutate()
//            drawable?.setTint(ContextCompat.getColor(requireContext(), R.color.app_theme_color))
//            binding.walkBook.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
//
//            binding.home.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            val homeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.home)?.mutate()
//            homeDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
//            binding.setting.setCompoundDrawablesWithIntrinsicBounds(null, homeDrawable, null, null)
//
//            binding.setting.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            val settingDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_setting)?.mutate()
//            settingDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
//            binding.addProfile.setCompoundDrawablesWithIntrinsicBounds(null, settingDrawable, null, null)
//
//            binding.addProfile.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
//            val addProfileDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_profile)?.mutate()
//            addProfileDrawable?.setTint(ContextCompat.getColor(requireContext(), R.color.black))
//            binding.walkBook.setCompoundDrawablesWithIntrinsicBounds(null, addProfileDrawable, null, null)


            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    // Handle the selected date
                    val selectedDate = "$year-${(month + 1).toString().padStart(2, '0')}-${
                        day.toString().padStart(2, '0')
                    }"
                    // TODO: Do something with the selected date (e.g., display it)
                    //   navController.navigate(R.id.BookingFragment)

                    // Navigate to BookingFragment with selected date using Safe Args
                    val action =
                        HomeFragmentDirections.actionHomeFragmentToBookingFragment(selectedDate)
                    navController.navigate(action)

                },
                currentYear,
                currentMonth,
                currentDay
            )

            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            calendar.add(Calendar.MONTH, 1)
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis

            datePickerDialog.show()

        }

        binding.logout.setOnClickListener {
          //  setBottomSheet()
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.BookingFragment, R.id.StaffBookFragment, R.id.estimateFragment,
                R.id.successFragment, R.id.walk_book_Fragment, R.id.reportFragment,
                R.id.generalFragment, R.id.profileFragment, R.id.TimeFragment,
                R.id.RegisterFragment, R.id.StaffFragment, R.id.AddStaffFragment,
                R.id.BankFragment, R.id.StaffModuleFragment, R.id.ServiceModuleFragment -> {
                    binding.bottomMenu.visibility = View.GONE
                }

                else -> {
                    binding.bottomMenu.visibility = View.VISIBLE
                }
            }
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
//            bottomSheetDialog.dismiss()
//        }
//
//        val callButton: TextView = bottomSheetView.findViewById(R.id.okButton)
//        callButton.setOnClickListener {
//            performLogout()
//            bottomSheetDialog.dismiss()
//        }
//        bottomSheetDialog.show()
//
//    }

    private fun performLogout() {

        // Clear any user session or authentication data
        val prefManager = PrefManager(requireContext())
        prefManager.clearAll()
        prefManager.setIsLoggedIn(false)

        clearAppCache(requireContext())
        closeApp()

        // Clear all activities and start MainActivity
//        val intent = Intent(requireContext(), MainActivity::class.java)
//        ActivityCompat.finishAffinity(requireActivity())
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        startActivity(intent)

//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
    }

    private fun clearAppCache(context: Context) {
        try {
            val cacheDir = context.cacheDir
            deleteDir(cacheDir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            if (children != null) {
                for (child in children) {
                    val success = deleteDir(File(dir, child))
                    if (!success) {
                        return false
                    }
                }
            }
            return dir.delete()
        } else if (dir != null && dir.isFile) {
            return dir.delete()
        } else {
            return false
        }
    }

    private fun closeApp() {
        ActivityCompat.finishAffinity(requireActivity())
        exitProcess(0)
    }

    override fun getAdapterPosition(
        Position: Int,
        ValueArray: ArrayList<CommonDBaseModel>,
        Mode: String?
    ) {

    }
    override fun onResume() {
        super.onResume()
        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.inner_host_nav) as NavHostFragment
        val navController = navHostFragment.navController

        if (navController.currentDestination?.id == R.id.homeBaseFragment) {
            refreshFragment()
        }
    }

    private fun refreshFragment() {
        // Refresh the HomeFragment data or UI here
        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.inner_host_nav) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.homeFragment) // Navigate to HomeFragment again
    }
//
//    override fun onResume() {
//        super.onResume()
//        refreshFragment()
//    }

}