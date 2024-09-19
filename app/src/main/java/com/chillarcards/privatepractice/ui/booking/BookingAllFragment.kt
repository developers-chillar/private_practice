package com.chillarcards.privatepractice.ui.booking

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentViewAllBinding
import com.chillarcards.privatepractice.ui.adapter.BookingAdapter
import com.chillarcards.privatepractice.ui.interfaces.IAdapterViewUtills
import com.chillarcards.privatepractice.utills.CommonDBaseModel
import com.chillarcards.privatepractice.utills.Const
import com.chillarcards.privatepractice.utills.PrefManager
import com.chillarcards.privatepractice.utills.Status
import com.chillarcards.privatepractice.viewmodel.BookingViewModel
import com.chillarcards.privatepractice.viewmodel.RegisterViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class BookingAllFragment : Fragment(), IAdapterViewUtills {

    lateinit var binding: FragmentViewAllBinding
    private val bookingViewModel by viewModel<BookingViewModel>()
    private lateinit var prefManager: PrefManager
    private val args: BookingAllFragmentArgs by navArgs()
    private var doctorName =""
    private var phoneNo =""
    private val PERMISSION_REQUEST_CALL_PHONE = 1


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewAllBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager(requireContext())

        setToolbar()
        binding.headTran.text = getString(R.string.book_head)

//        bookingViewModel.run {
//            if(prefManager.getEntityId() =="-1"){
//                doctorID.value = prefManager.getDoctorId().toString()
//                date.value = args.date
//                entityId.value = ""
//            }else{
//                doctorID.value = prefManager.getDoctorId().toString()
//                date.value = args.date
//                entityId.value = prefManager.getEntityId()
//            }
//            getBookingList()
//        }

        bookingViewModel.run {
            doctorID.value = prefManager.getDoctorId().toString()
            date.value = args.date
            entityId.value = if (prefManager.getEntityId() == "-1") "" else prefManager.getEntityId()
            getBookingList()
        }
        setUpObserver()




        binding.nextDayTv.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentYear = calendar.get(Calendar.YEAR)
            val currentMonth = calendar.get(Calendar.MONTH)
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    // Handle the selected date
                    val selectedDate = "$year-${(month + 1).toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
                    bookingViewModel.run {
                        doctorID.value = prefManager.getDoctorId().toString()
                        date.value = selectedDate
                        entityId.value = if (prefManager.getEntityId() == "-1") "" else prefManager.getEntityId()
                        getBookingList()
                    }
                    setUpObserver()
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

    }

    private fun setToolbar() {
        binding.toolbar.toolbarTitle.text = getString(R.string.booking_head)
        binding.toolbar.toolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun setUpObserver() {
        try {
            bookingViewModel.bookingData.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            hideProgress()
                            it.data?.let { bookingData ->
                                when (bookingData.statusCode) {
                                    200 -> {
                                        binding.appointmentCard.visibility = View.VISIBLE
                                        doctorName = bookingData.data.doctorName

                                        val dateString = bookingData.data.appointmentDate;
                                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        val date = dateFormat.parse(dateString)

                                        val calendar = Calendar.getInstance()
                                        calendar.time = date
                                        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
                                        val dateOfMonth = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
                                        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
                                        val year = calendar.get(Calendar.YEAR)

//                                        binding.currentDate.text = "$dayOfWeek"
//                                        binding.currentDay.text = dateOfMonth
//                                        binding.currentMonth.text=month
//                                        binding.currentYear.text = year.toString()

                                        binding.daydate.text = dayOfWeek // Show day of the week (e.g., "Friday")
                                        binding.date.text = dateOfMonth.toString() // Show the day (e.g., "06")
                                        binding.year.text = "$month $year" // Show month and year (e.g., "September 2024")

                                        binding.ttlApointTv.text = "Appointments : "+bookingData.data.totalBooking.toString()
                                        binding.completedTv.text = "Completed  : "+bookingData.data.completedAppointments.toString()
                                        binding.cancelTv.text = "Pending  : "+bookingData.data.pendingAppointments.toString()

                                        if(bookingData.data.appointmentList.isNotEmpty()) {
                                            binding.nodata.visibility=View.GONE
                                            binding.tranRv.visibility=View.VISIBLE
                                            val bookingAdapter = BookingAdapter(
                                                bookingData.data.appointmentList,
                                                context,
                                                this@BookingAllFragment
                                            )
                                            binding.tranRv.adapter = bookingAdapter
                                            binding.tranRv.layoutManager = LinearLayoutManager(
                                                context,
                                                LinearLayoutManager.VERTICAL,
                                                false
                                            )
                                        }else{
                                            binding.nodata.visibility=View.VISIBLE
                                            binding.tranRv.visibility=View.GONE
                                        }
                                    }
                                    403 -> {
                                        prefManager.setRefresh("1")
                                        val authViewModel by viewModel<RegisterViewModel>()
                                        Const.getNewTokenAPI(
                                            requireContext(),
                                            authViewModel,
                                            viewLifecycleOwner
                                        )

                                    }
                                    else -> Const.shortToast(requireContext(), bookingData.message)

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
             //   bookingViewModel.clear()
            }


        } catch (e: Exception) {
            Log.e("abc_otp", "setUpObserver: ", e)
        }
    }

    private fun viewUpObserver() {
        try {
            bookingViewModel.bookStatusData.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.data?.let { bookStatusData ->
                                when (bookStatusData.statusCode) {
                                    200 -> {

                                        findNavController().navigate(
                                            BookingAllFragmentDirections.actionEstimateFragmentToSuccessFragment(
                                            )
                                        )

                                    }
                                    403 -> {
                                        prefManager.setRefresh("1")
                                        val authViewModel by viewModel<RegisterViewModel>()
                                        Const.getNewTokenAPI(
                                            requireContext(),
                                            authViewModel,
                                            viewLifecycleOwner
                                        )

                                    }
                                    else -> Const.shortToast(requireContext(), bookStatusData.message)

                                }
                            }
                        }
                        Status.LOADING -> {
                        }
                        Status.ERROR -> {
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
         //   bookingViewModel.clear()

        } catch (e: Exception) {
            Log.e("abc_otp", "setUpObserver: ", e)
        }
    }

    private fun showProgress() {
        binding.otpProgress.visibility = View.VISIBLE
    }
    private fun hideProgress() {
        binding.otpProgress.visibility = View.GONE
    }

    private fun setBottomSheet(selectedData: ArrayList<CommonDBaseModel>) {

        val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_persistent, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)

        val actionButton: LinearLayout = bottomSheetView.findViewById(R.id.action_btn)
        val customerTV: TextView = bottomSheetView.findViewById(R.id.customerNameTextView)
        customerTV.text = "Patient Name : "+selectedData[0].itmName

        val completeButton: TextView = bottomSheetView.findViewById(R.id.completedButton)

        if (selectedData[0].positionVal?.equals(1) == true){
            actionButton.visibility=View.GONE
        }else{
            completeButton.setOnClickListener {

                // Initialize MediaPlayer in onCreate or another appropriate method
                val mediaPlayer: MediaPlayer? = MediaPlayer.create(context, R.raw.bell_audio)
                if (!mediaPlayer!!.isPlaying) {
                    mediaPlayer.start()
                }
                bookingViewModel.run {
                    bookingId.value = selectedData[0].mastIDs
                    getConfirmBooking()
                }
                viewUpObserver()

                //REMOVE THIS PAGE
//                findNavController().navigate(
//                    BookingAllFragmentDirections.actionBookingFragmentToEstimateFragment(
//                        selectedData[0].mastIDs,doctorName
//                    )
//                )
                bottomSheetDialog.dismiss()
            }
        }

        val callButton: TextView = bottomSheetView.findViewById(R.id.callButton)
        callButton.setOnClickListener {
            phoneNo = selectedData[0].mobile.toString()
            makePhoneCall(phoneNo)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()

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

    override fun getAdapterPosition(
        Position: Int,
        ValueArray: ArrayList<CommonDBaseModel>,
        Mode: String?
    ) {
        if(Mode.equals("VIEW")){
            setBottomSheet(ValueArray)
        }
    }


    private fun makePhoneCall(phoneNumber: String) {
        if (phoneNumber.isNotEmpty()) {
            val permission = Manifest.permission.CALL_PHONE
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), PERMISSION_REQUEST_CALL_PHONE)
            } else {
                // Permission has already been granted

                val dialIntent = Intent(Intent.ACTION_CALL)
                dialIntent.data = Uri.parse("tel:$phoneNumber")

                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission to make a phone call if not granted
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_REQUEST_CALL_PHONE)
                }
                try {
                    startActivity(dialIntent)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    // Handle security exception
                }
            }
        } else {
            Log.e("PhoneCall", "Phone number is empty or null")
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_CALL_PHONE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                makePhoneCall(phoneNo)
            } else {
                // Permission denied
                Const.shortToast(requireContext(), "Permission denied")
            }
        }
    }

}
