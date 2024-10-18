package com.chillarcards.privatepractice.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.data.model.DoctorLeaveResClass
import com.chillarcards.privatepractice.data.model.EntityDetail
import com.chillarcards.privatepractice.databinding.FragmentHomeBinding
import com.chillarcards.privatepractice.ui.adapter.BookingAdapter
import com.chillarcards.privatepractice.ui.adapter.ClinicAdapter
import com.chillarcards.privatepractice.ui.interfaces.IAdapterViewUtills
import com.chillarcards.privatepractice.ui.notification.NotificationViewModel
import com.chillarcards.privatepractice.utills.CommonDBaseModel
import com.chillarcards.privatepractice.utills.Const
import com.chillarcards.privatepractice.utills.CustomCalendarView
import com.chillarcards.privatepractice.utills.CustomDatePickerDialog
import com.chillarcards.privatepractice.utills.PrefManager
import com.chillarcards.privatepractice.utills.Status
import com.chillarcards.privatepractice.viewmodel.BookingViewModel
import com.chillarcards.privatepractice.viewmodel.RegisterViewModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.system.exitProcess

class HomeFragment : Fragment(), IAdapterViewUtills {
    private var toast: Toast? = null
    lateinit var binding: FragmentHomeBinding
    private val args: HomeFragmentArgs by navArgs()
    private lateinit var notificationViewModel: NotificationViewModel
    private val bookingViewModel by viewModel<BookingViewModel>()
    private lateinit var prefManager: PrefManager

    private var doctorName =""
    private var phoneNo =""
    private val PERMISSION_REQUEST_CALL_PHONE = 1
    private var formattedDate = ""
    private var shareLink = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // in here you can do logic when backPress is clicked
                alertMsg(requireContext())
            }
        })
    }

    fun alertMsg(context: Context) {
        try {
            PrefManager(context)
            val builder = AlertDialog.Builder(context)

            builder.setTitle(R.string.alert_heading)
            builder.setMessage(R.string.pop_back_message)
            builder.setIcon(R.mipmap.ic_launcher_new_design)
            builder.setCancelable(false)

            //performing positive action
            builder.setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                ActivityCompat.finishAffinity(requireActivity())
                exitProcess(0)            }
            builder.setNegativeButton(context.getString(R.string.cancel)) { _, _ ->
//                alertDialog.dismiss()
            }
            val alertDialog: AlertDialog = builder.create()

            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()
        } catch (e: Exception) {
            //e.printstackTrace()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomAppBar = activity?.findViewById<BottomAppBar>(R.id.bottomAppBar)
        bottomAppBar?.visibility = View.VISIBLE
        prefManager = PrefManager(requireContext())
        notificationViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)

        getCurrentDate()

        val currentDate = Calendar.getInstance().time
        formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate)

        bookingViewModel.run {
            doctorID.value = prefManager.getDoctorId()
            date.value = formattedDate
            entityId.value = if (prefManager.getEntityId() == "-1") "" else prefManager.getEntityId()
            Log.d("entityIDHome", "onViewCreated: ${prefManager.getEntityId()}")

            //   entityId.value = if (prefManager.getIntEntityId() == "-1") "" else prefManager.getIntEntityId()
         //   handleTokenExpiry()
            Log.d("ref_compl_token_home","tag:${prefManager.getToken()}")
            prefManager.getToken()
            getBookingList()
        }

        bookingViewModel.run {
            getShareLink()
        }
        prefManager.getToken()
        setUpObserver()
        getUpObserver()

        binding.menuIcon.setOnClickListener {
            openOptionsMenu(it)
        }
        binding.tvEditDate.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditDateTimeFragment())
        }

        binding.addLeave.setOnClickListener {
            showEventCalendarDialog()
        }



//        binding.addLeave.setOnClickListener {
//            bookingViewModel.doctorLeaveDate.observe(viewLifecycleOwner) { resource ->
//                when (resource?.status) {
//                    Status.SUCCESS -> {
//                        val leaveDates = resource.data
//
//                        val calendar = Calendar.getInstance()
//                        val currentYear = calendar.get(Calendar.YEAR)
//                        val currentMonth = calendar.get(Calendar.MONTH)
//                        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
//
//                        // Convert leave dates to a set of Calendar objects for easier comparison
//                        val leaveDatesSet = leaveDates?.data?.map { leaveDate ->
//                            Calendar.getInstance().apply {
//                                time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(leaveDate)
//                            }
//                        }?.toSet()
//
//                        // Create a DatePickerDialog
//                        val datePickerDialog = DatePickerDialog(
//                            requireContext(),
//                            { _, year, month, dayOfMonth ->
//                                val selectedDate = "$year-${(month + 1).toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
//                                bookingViewModel.run {
//                                    doctorID.value = prefManager.getDoctorId().toString()
//                                    date.value = selectedDate
//                                    entityId.value = if (prefManager.getEntityId() == "-1") "" else prefManager.getEntityId()
//                                    bookingViewModel.addDoctorOnLeave()
//                                }
//                                doctorAvailableObserver()
//                            },
//                            currentYear, currentMonth, currentDay
//                        )
//
//                        // Set up the logic to handle disabling and coloring leave dates
//                        datePickerDialog.datePicker.init(currentYear, currentMonth, currentDay) { _, year, monthOfYear, dayOfMonth ->
//                            val selectedDate = Calendar.getInstance().apply {
//                                set(year, monthOfYear, dayOfMonth)
//                            }
//
//                            // Disable and show a toast for leave dates
//                            if (leaveDatesSet != null && leaveDatesSet.any { leaveDate ->
//                                    leaveDate.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
//                                            leaveDate.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
//                                            leaveDate.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH)
//                                }) {
//                                Toast.makeText(requireContext(), "This date is marked as a leave day.", Toast.LENGTH_SHORT).show()
//                                // Optionally, you can reset the date picker or just allow showing the message
//                                datePickerDialog.datePicker.updateDate(currentYear, currentMonth, currentDay)
//                            }
//                        }
//
//                        // Set minimum and maximum date
//                        datePickerDialog.datePicker.minDate = calendar.timeInMillis
//                        calendar.add(Calendar.MONTH, 1)  // Set the max date to one month from today
//                        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
//
//                        datePickerDialog.show()
//                    }
//                    Status.ERROR -> {
//                        Toast.makeText(requireContext(), "Failed to fetch leave dates", Toast.LENGTH_SHORT).show()
//                    }
//                    Status.LOADING -> {
//                        // Optionally show a loading indicator
//                    }
//                    null -> TODO()
//                }
//            }
//
//            // Trigger fetching doctor leave dates
//            bookingViewModel.DoctorLeaveDates()
//        }



//        binding.addLeave.setOnClickListener {
//                val calendar = Calendar.getInstance()
//                val currentYear = calendar.get(Calendar.YEAR)
//                val currentMonth = calendar.get(Calendar.MONTH)
//                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
//                val datePickerDialog = DatePickerDialog(
//                    requireContext(),
//                    { _, year, month, day ->
//                        // Handle the selected date
//                        val selectedDate = "$year-${(month + 1).toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
//                        bookingViewModel.run {
//                            doctorID.value = prefManager.getDoctorId().toString()
//                            date.value = selectedDate
//                            entityId.value = if (prefManager.getEntityId() == "-1") "" else prefManager.getEntityId()
//                            bookingViewModel.addDoctorOnLeave()
//                        }
//                        doctorAvailableObserver()
//                    },
//                    currentYear,
//                    currentMonth,
//                    currentDay
//                )
//
////            // Set the minimum date to today
//                datePickerDialog.datePicker.minDate = calendar.timeInMillis
////
////            // Set the maximum date to one week from today
//                calendar.add(Calendar.MONTH, 1)
//                datePickerDialog.datePicker.maxDate = calendar.timeInMillis
//
//                datePickerDialog.show()
//
//
//        }

        binding.share.setOnClickListener {
            if(shareLink!="") {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareLink)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            }else{
                getUpObserver()
                Const.shortToast(requireContext(),"Something went wrong! Try Again")
            }
        }
    }


    private fun getCurrentDate() {
        // Get the current date
        val currentDate = Calendar.getInstance()

        // Extract the day of the week, day of the month, month, and year
        val dayOfWeek = currentDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH) // e.g., Friday
        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH) // e.g., 06
        val month = currentDate.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH) // e.g., September
        val year = currentDate.get(Calendar.YEAR) // e.g., 2024

        // Set values to corresponding TextViews
        binding.daydate.text = dayOfWeek // Show day of the week (e.g., "Friday")
        binding.date.text = dayOfMonth.toString() // Show the day (e.g., "06")
        binding.year.text = "$month $year" // Show month and year (e.g., "September 2024")
    }


//    private fun getCurrentDate(){
//        val currentDate = Calendar.getInstance()
//        val dayOfWeek = currentDate.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH)
//        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)
//        val year = currentDate.get(Calendar.YEAR)
//        binding.daydate.text = "$dayOfWeek $dayOfMonth"
//        binding.date.text= dayOfMonth.toString()
//        binding.year.text = "$dayOfMonth $year"
//    }

    override fun onStop() {
        super.onStop()
        Log.d("abc_mob", "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("abc_mob", "onDestroy: ")
        bookingViewModel.clear()

    }

    private fun doctorAvailableObserver() {
        try {
            bookingViewModel.doctorOnLeave.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            hideProgress()
                            it.data?.let { bookingData ->
                                when (bookingData.statusCode) {
                                    200 -> {
                                        showToast(bookingData.message.toString())
                                    }
                                    400->{
                                        showToast(bookingData.message.toString())
                                    }
                                    403 -> {
                                        showToast(bookingData.message.toString())

                                    }
                                    404->{
                                        showToast(bookingData.message.toString())
                                    }
                                    500-> {
                                        showToast(bookingData.message.toString())

                                    }
                                    else -> Const.shortToast(requireContext(), bookingData.message.toString()
                                    )

                                }
                            }
                        }
                        Status.LOADING -> {
                            showProgress()
                        }
                        Status.ERROR -> {
                            hideProgress()
                            Const.shortToast(requireContext(), it.message.toString())
                            Const.shortToast(requireContext(),"403 profile")
                        }
                    }
                }

            }


        } catch (e: Exception) {
            Log.e("abc_otp", "setUpObserver: ", e)
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
                                      //  handleTokenExpiry()
                                        Log.d("TokenLog", "200")
                                        binding.logoIcon.text= bookingData.data.doctorName
                                        Log.e("doctorName", "doctorName: $doctorName")
                                        binding.ttlApointTv.text = "Total Bookings : "+bookingData.data.totalBooking.toString()
                                        binding.completedTv.text = "Completed  : "+bookingData.data.completedAppointments.toString()
                                        binding.cancelTv.text = "Pending  : "+bookingData.data.pendingAppointments.toString()
                                        doctorName = bookingData.data.doctorName
                                        val bookingAdapter = BookingAdapter(bookingData.data.appointmentList, context,this@HomeFragment)
                                        binding.tranRv.adapter = bookingAdapter
                                        binding.tranRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                                        binding.bookingViewAll.setOnClickListener {
                                            try {
                                                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToBookingFragment())
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                        val entityDetails = bookingData.data.entityDetails
                                        if(entityDetails != null && entityDetails.isNotEmpty()) {
                                            if(bookingData.data.entityDetails.size>1){
                                                binding.topStaffFrame.visibility=View.VISIBLE
                                                val entityDataMastCols: List<EntityDetail>
                                                val entityMastTemp  = bookingData.data.entityDetails.toMutableList()
                                                entityMastTemp.add(0, EntityDetail( -1,"View all","",0,1))
                                                entityDataMastCols  = entityMastTemp

                                                val salesTopPicAdapter = ClinicAdapter(
                                                    entityDataMastCols,
                                                    requireContext(),
                                                    this@HomeFragment
                                                )

                                                binding.topPicRv.adapter = salesTopPicAdapter
                                                binding.topPicRv.layoutManager = LinearLayoutManager(
                                                    context,
                                                    LinearLayoutManager.HORIZONTAL,
                                                    false
                                                )
                                            }else{
                                                binding.topStaffFrame.visibility=View.GONE
                                            }
                                        }else{
                                            binding.topStaffFrame.visibility=View.GONE
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
                                    else -> {
                                        Const.shortToast(requireContext(), bookingData.message)
                                    }
                                }
                            }
                        }
                        Status.LOADING -> {
                            showProgress()
                        }
                        Status.ERROR -> {
                         //   Toast.makeText(requireContext(),"Token not updated in sign in  home frag",Toast.LENGTH_SHORT).show()
                            Log.d("TokenLog", "403: Token expired, refreshing token2")
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
          //  bookingViewModel.clear()
        } catch (e: Exception) {
            Log.e("abc_otp", "setUpObserver: ", e)
        }
    }

    private fun getUpObserver() {
        try {
            bookingViewModel.bookLinkData.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            hideProgress()
                            it.data?.let { booklnkData ->
                                when (booklnkData.statusCode) {
                                    200 -> {
                                        shareLink = booklnkData.data
                                    }
                                    else -> Const.shortToast(requireContext(), booklnkData.message)
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

    override fun getAdapterPosition(
        Position: Int,
        ValueArray: ArrayList<CommonDBaseModel>,
        Mode: String?
    ) {
        if(Mode.equals("VIEW")) {
            setBottomSheet(ValueArray)
        }
        else if(Mode.equals("BOOKVIEW")) {
            val bookingId: String = ValueArray[0].mastIDs.toString()
            findNavController().navigate(
                HomeFragmentDirections.actionHomeToStaffViewBookFragment(bookingId)
            )
        }
        else if(Mode.equals("VIEWBOOKING")) {
            if(ValueArray[0].mastIDs=="-1"){
                prefManager.setEntityId(ValueArray[0].mastIDs.toString())

                bookingViewModel.run {
                    doctorID.value = prefManager.getDoctorId().toString()
                    date.value = formattedDate
                    entityId.value = ""
                    getBookingList()
                }
            }else{
                prefManager.setEntityId(ValueArray[0].mastIDs.toString())
                bookingViewModel.run {
                    doctorID.value = prefManager.getDoctorId().toString()
                    date.value = formattedDate
                    entityId.value = prefManager.getEntityId()
                    getBookingList()
                }
            }
            prefManager.getToken()
            setUpObserver()
        }
    }


    private fun setBottomSheet(selectedData: ArrayList<CommonDBaseModel>) {
        var mediaPlayer: MediaPlayer?

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
                mediaPlayer = MediaPlayer.create(context, R.raw.bell_audio)
                if (!mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.start()
                }
                bookingViewModel.run {
                    bookingId.value = selectedData[0].mastIDs
                    getConfirmBooking()
                }
                viewUpObserver()
//                findNavController().navigate(
//                    HomeFragmentDirections.actionHomeFragmentToEstimateFragment(
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
                                            HomeFragmentDirections.actionEstimateFragmentToSuccessFragment(
                                            )
                                        )

                                    }
                                    403 -> {
                                        prefManager.setRefresh("1")
                                        val authViewModel by viewModel<RegisterViewModel>()
                                        Const.getNewTokenAPI(requireContext(), authViewModel,
                                            viewLifecycleOwner
                                        )

                                    }

                                    404->{
                                        Const.shortToast(requireContext(), bookStatusData.message)

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





    private fun openOptionsMenu(view: View) {

        val contextWrapper = ContextThemeWrapper(requireContext(), R.style.PopupMenuStyle)
        val popup = PopupMenu(contextWrapper, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_top, popup.menu)

        val version = popup.menu.findItem(R.id.version)
        val pInfo = activity?.let { activity?.packageManager!!.getPackageInfo(it.packageName, PackageManager.GET_ACTIVITIES) }
        val versionName = pInfo?.versionName //Version Name
        version.title = "Version $versionName"

//        val notificationItem = popup.menu.findItem(R.id.menu_notification)
//        val notificationCount = getNotificationCount()

//        if (notificationCount > 0) {
//            // Show red dot or notification count
//            notificationItem.setIcon(R.drawable.ic_notification_red_dot)
//        } else {
//            // Hide red dot or notification count
//            notificationItem.setIcon(R.drawable.ic_notification)
//            notificationItem.actionView = null
//        }

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {

                R.id.about->{
                    val bundle = Bundle().apply {
                        putString("aboutURL", "https://www.chillarpayments.com/privatepractice.html")
                    }
                    findNavController().navigate(R.id.aboutFragment, bundle)
                    true
                }

                R.id.terms_and_conditions->{
                    val bundle = Bundle().apply {
                        putString("termsURL", "https://www.chillarpayments.com/terms-and-conditions.html")
                    }
                    findNavController().navigate(R.id.termsAndConditionsFragment, bundle)
                    true
                }
                R.id.privacy_policy->{
                    val bundle = Bundle().apply {
                        putString("privacyURL", "https://www.chillarpayments.com/privacy-policy.html")
                    }
                    findNavController().navigate(R.id.privacyPolicyFragment, bundle)
                    true
                }
                R.id.refund->{
                    val bundle = Bundle().apply {
                        putString("refundURL", "https://www.chillarpayments.com/Cancellation-Policy.html")
                    }
                    findNavController().navigate(R.id.refundPolicyFragment, bundle)
                    true
                }
                R.id.contact_us->{
                    val bundle = Bundle().apply {
                        putString("contactURL", "https://www.chillarpayments.com/contactus.html")
                    }
                    findNavController().navigate(R.id.contactUsFragment, bundle)
                    true
                }

                R.id.action_logout -> {
                    setBottomSheet()
                  //  findNavController().navigate(R.id.action_homeFragment_to_NotificationFragment)
                    true
                }

                else -> false
            }
        }

        popup.show()
    }
    @SuppressLint("MissingInflatedId")
    private fun setBottomSheet() {

        val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.logout, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)
        val doctorName:TextView=bottomSheetView.findViewById(R.id.tvDrName)
        val bookingData = bookingViewModel.bookingData.value?.data
        doctorName.text = bookingData?.data?.doctorName
        val pInfo = activity?.let { it.packageManager.getPackageInfo(it.packageName, PackageManager.GET_ACTIVITIES) }
        val versionName = pInfo?.versionName
        val tvVersion: TextView = bottomSheetView.findViewById(R.id.tvVersion)
        tvVersion.text = "Version $versionName"
        val completeButton: ImageView = bottomSheetView.findViewById(R.id.ivClose)
        completeButton.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        val callButton: LinearLayout = bottomSheetView.findViewById(R.id.okButton)
        callButton.setOnClickListener {
            performLogout()
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()

    }

     fun performLogout() {

        // Clear any user session or authentication data
        val prefManager = PrefManager(requireContext())
        prefManager.clearAll()
        prefManager.setIsLoggedIn(false)

        clearAppCache(requireContext())
//        val navController = findNavController()
//        navController.navigate(R.id.mobileFragment)
//        Handler(Looper.getMainLooper()).postDelayed({
//            ActivityCompat.finishAffinity(requireActivity())
//            // Optional: Call exitProcess(0) if absolutely necessary
//            // exitProcess(0)
//        }, 500)
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

    private fun getNotificationCount(): Int {
        // Retrieve the count from the notification repository or any other source
        return notificationViewModel.getNotifications().size
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

    private fun showToast(message: String) {
        // Cancel any existing toast
        toast?.cancel()

        // Show a new toast
        toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        toast?.show()
    }


    override fun onResume() {
        super.onResume()
        bookingViewModel.getBookingList()
        setUpObserver()
    }

    private fun showEventCalendarDialog() {
        val inflater = requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ll = inflater.inflate(R.layout.custom_calendar, null, false) as LinearLayout
        val cv = ll.findViewById<CustomCalendarView>(R.id.calendarView)
        // Observe leave dates
        bookingViewModel.doctorLeaveDate.observe(viewLifecycleOwner) { resource ->
            when (resource?.status) {
                Status.SUCCESS -> {
                    val leaveDates = resource.data
                    // Convert leave dates to a set of Calendar objects
                    val leaveDatesSet = leaveDates?.data?.map { leaveDate ->
                        Calendar.getInstance().apply {
                            time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(leaveDate)
                        }
                    }?.toSet()

                    leaveDatesSet?.forEach { leaveDate ->
                        Log.d("LeaveDates", "Leave Date: ${leaveDate.time}")
                    }

                    // Mark the leave dates in the custom CalendarView
                    leaveDatesSet?.forEach { leaveDate ->
                        cv.markDate(leaveDate) // For drawing a ring
                        cv.disableDate(leaveDate)
                      //  Toast.makeText(requireContext(),"leave dates",Toast.LENGTH_SHORT).show()
                    }

                    // Set up the listener for date changes
                    cv.setOnDateChangeListener { view, year, month, dayOfMonth ->
                        val selectedDate = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }

                        // Check if the selected date is a leave date
                        if (leaveDatesSet?.any { leaveDate ->
                                leaveDate.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                                        leaveDate.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                                        leaveDate.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH)
                            } == true) {
                            Toast.makeText(requireContext(), "This date is marked as a leave day.", Toast.LENGTH_SHORT).show()
                        } else {
                            // Proceed with normal date selection logic
                            val selectedDateString = "$year-${(month + 1).toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
                            bookingViewModel.run {
                                doctorID.value = prefManager.getDoctorId().toString()
                                date.value = selectedDateString
                                entityId.value = if (prefManager.getEntityId() == "-1") "" else prefManager.getEntityId()
                                addDoctorOnLeave()
                            }
                            doctorAvailableObserver()
                        }
                    }

                    // Set min and max date for CalendarView
                    val calendar = Calendar.getInstance()
                    cv.minDate = calendar.timeInMillis
                    calendar.add(Calendar.MONTH, 1)
                    cv.maxDate = calendar.timeInMillis
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), "Failed to fetch leave dates", Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    // Optionally show a loading indicator
                }
                null -> TODO()
            }
        }

        // Trigger fetching doctor leave dates
        bookingViewModel.DoctorLeaveDates()

        // Create and show the dialog
        AlertDialog.Builder(requireContext())
            .setTitle("Event Calendar")
            .setView(ll)
            .setPositiveButton("Ok") { dialog, _ -> }
            .setNegativeButton("Cancel") { dialog, _ -> }
            .show()
    }


    private fun initScheduleEvent(year: Int, month: Int, dayOfMonth: Int) {
        // Your event scheduling logic
    }



}
