package com.chillarcards.privatepractice.ui.report

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentReportBinding
import com.chillarcards.privatepractice.ui.adapter.PaymentAdapter
import com.chillarcards.privatepractice.ui.home.HomeFragmentDirections
import com.chillarcards.privatepractice.utills.Const
import com.chillarcards.privatepractice.utills.PrefManager
import com.chillarcards.privatepractice.utills.Status
import com.chillarcards.privatepractice.viewmodel.RegisterViewModel
import com.chillarcards.privatepractice.viewmodel.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReportFragment : Fragment() {

    lateinit var binding: FragmentReportBinding
    private val reportViewModel by viewModel<ReportViewModel>()
    private lateinit var prefManager: PrefManager
    private var formattedDate = ""
    private var formattedDateApi = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager(requireContext())

        setToolbar()
        //2024-07-15
        val currentDate = Calendar.getInstance().time
        formattedDate = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(currentDate)
        formattedDateApi = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate)
        binding.date.text = formattedDate

        reportViewModel.run {
            doctorID.value = prefManager.getDoctorId().toString()
            date.value = formattedDateApi
            getReport()
        }
        setUpObserver()

        binding.bookingViewAll.setOnClickListener {
            try {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToBookingFragment(
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

//        binding.chooseDate.setOnClickListener {
//            val calendar = Calendar.getInstance()
//
//            // Set the maximum date to today's date
//            val maxYear = calendar.get(Calendar.YEAR)
//            val maxMonth = calendar.get(Calendar.MONTH)
//            val maxDay = calendar.get(Calendar.DAY_OF_MONTH)
//
//// Set the minimum date to 3 months ago
//            calendar.add(Calendar.MONTH, -3)
//            val minYear = calendar.get(Calendar.YEAR)
//            val minMonth = calendar.get(Calendar.MONTH)
//            val minDay = calendar.get(Calendar.DAY_OF_MONTH)
//
//            val datePickerDialog = DatePickerDialog(
//                requireContext(),
//                { _, year, month, day ->
//                    // Handle the selected date
//                    val selectedDate = formatDate(day, month, year)
//                    binding.date.text = selectedDate
//
//                    reportViewModel.run {
//                        doctorID.value = prefManager.getDoctorId().toString()
//                        date.value = selectedDate
////                        date.value = "12 Jul 2024"
//                        getReport()
//                    }
//                    setUpObserver()
//                },
//                maxYear,
//                maxMonth,
//                maxDay
//            )
//
//            datePickerDialog.datePicker.minDate = calendar.timeInMillis
//            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
//
//            datePickerDialog.datePicker.init(
//                maxYear, maxMonth, maxDay
//            ) { _, year, month, day ->
//                // Handle the date change
//            }
//
//
//            datePickerDialog.show()
//
//        }

    }
    private fun formatDate(day: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val sdf = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    private fun setToolbar() {
        binding.toolbar.toolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.toolbarTitle.text = getString(R.string.sales_report)
    }
    private fun setUpObserver() {
        try {
            reportViewModel.reportData.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            hideProgress()
                            it.data?.let { reportData ->
                                when (reportData.statusCode) {
                                    200 -> {
                                        binding.custCount.text = reportData.data.bookingReport.size.toString()
                                        if(reportData.data.bookingReport.isNotEmpty()) {
                                            binding.nodata.visibility=View.GONE
                                            binding.tranRv.visibility=View.VISIBLE

                                            val transactionAdapter = PaymentAdapter(
                                                reportData.data.bookingReport, context
                                            )
                                            binding.tranRv.adapter = transactionAdapter
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
                                    else -> Const.shortToast(requireContext(), reportData.message)

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
              //  reportViewModel.clear()
            }

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


}