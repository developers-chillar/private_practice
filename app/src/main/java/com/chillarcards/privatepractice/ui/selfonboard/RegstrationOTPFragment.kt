package com.chillarcards.privatepractice.ui.selfonboard

import android.content.ClipData
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.ClipboardManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentRegstrationOTPBinding
import com.chillarcards.privatepractice.utills.Const
import com.chillarcards.privatepractice.utills.GenericKeyEvent
import com.chillarcards.privatepractice.utills.GenericTextWatcher
import com.chillarcards.privatepractice.utills.PrefManager
import com.chillarcards.privatepractice.utills.Status
import com.chillarcards.privatepractice.viewmodel.MobileScreenViewModel
import com.chillarcards.privatepractice.viewmodel.RegisterViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

open class RegstrationOTPFragment : Fragment(R.layout.fragment_regstration_o_t_p) {

    lateinit var binding: FragmentRegstrationOTPBinding

    private lateinit var prefManager: PrefManager
    private lateinit var timer: CountDownTimer
    private val digitRegex = "^\\d$".toRegex()
    private val args: RegstrationOTPFragmentArgs by navArgs()
    private lateinit var firebaseAuth: FirebaseAuth
   // private val mobileViewModel by viewModel<RegisterViewModel>()
    private val mobileViewModel by viewModel<MobileScreenViewModel>()
    private var mVerificationId = ""
    private var mResendToken: ForceResendingToken? = null

    private var aOk = false
    private var bOk = false
    private var cOk = false
    private var dOk = false
    private var eOk = false
    private var fOk = false

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
            builder.setMessage(R.string.pop_alert_message)
            builder.setIcon(R.mipmap.ic_launcher_new_design)
            builder.setCancelable(false)

            //performing positive action
            builder.setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                findNavController().popBackStack()
            }
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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_regstration_o_t_p, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pInfo =
            activity?.let { activity?.packageManager!!.getPackageInfo(it.packageName, PackageManager.GET_ACTIVITIES) }
        val versionName = pInfo?.versionName //Version Name
        binding.version.text = "${getString(R.string.version)}" + Const.ver_title + versionName

        firebaseAuth = FirebaseAuth.getInstance()
        prefManager = PrefManager(requireContext())

        if (binding.timer.text == "00:60")
            startTimer()

        otpViewActions()

        //val maskedPhoneNumber = maskPhoneNumber(args.mobile.toString())
        val maskedPhoneNumber = maskPhoneNumber(prefManager.getMobileNo())
        Log.d("maskedPhoneNumber","maskedPhoneNumber is:${maskedPhoneNumber}")
        binding.otpHeadMsg.text="We have send a 6 digit OTP to $maskedPhoneNumber"
        if (binding.otpA.text.isNullOrEmpty() || binding.otpB.text.isNullOrEmpty() || binding.otpC.text.isNullOrEmpty() || binding.otpD.text.isNullOrEmpty() || binding.otpE.text.isNullOrEmpty() || binding.otpF.text.isNullOrEmpty()) {
            binding.textinputError.visibility=View.GONE

        }
        binding.resendText.visibility = View.GONE
        binding.confirmBtn.setOnClickListener {
            binding.textinputError.visibility=View.GONE
            if (binding.otpA.text.isNullOrEmpty() || binding.otpB.text.isNullOrEmpty() || binding.otpC.text.isNullOrEmpty() || binding.otpD.text.isNullOrEmpty() || binding.otpE.text.isNullOrEmpty() || binding.otpF.text.isNullOrEmpty()) {
                Const.shortToast(requireContext(), "please enter the 6 digit OTP code")
            } else {
                val otp =
                    "${binding.otpA.text.toString()}${binding.otpB.text.toString()}${binding.otpC.text.toString()}${binding.otpD.text.toString()}${binding.otpE.text.toString()}${binding.otpF.text.toString()}"
                Log.d("abc_otp", "onViewCreated: $otp")
                if (otp.isNotEmpty()){
                    verifyPhoneNumberWithCode(otp)
                }
                else
                    Const.shortToast(requireContext(), getString(R.string.enter_otp))
            }
        }

        binding.resendText.setOnClickListener {
            clearOTP()
            resendVerificationCode(args.mobile.toString())
        }

        try {
            val s = "Didn't receive OTP? RESEND"
            val wordToSpan: Spannable = SpannableString(s)
            wordToSpan.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.primary_peach
                    )
                ),
                s.indexOf("?") + 1, s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            wordToSpan.setSpan(
                StyleSpan(Typeface.BOLD), s.indexOf("?") + 1, s.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.resendText.text = wordToSpan
        }
        catch (e: Exception) {
            //e.printstackTrace()
        }

    }

    private fun createCallbacks(): OnVerificationStateChangedCallbacks {
        return object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                binding.textinputError.visibility=View.VISIBLE
                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        binding.textinputError.text = e.message
                    }
                    is FirebaseTooManyRequestsException -> {
                        binding.textinputError.text = e.message
                    }
                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        binding.textinputError.text = e.message
                    }
                    else -> {
                        binding.textinputError.text = "An error occurred: ${e.message}"
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: ForceResendingToken
            ) {
                mVerificationId = verificationId
                mResendToken = token
                Log.d(TAG, "onCodeSent:$verificationId")

                if (binding.timer.text == "00:60")
                    startTimer()
                Const.shortToast(requireContext(),"OTP sent successfully")
            }
        }
    }
    private fun maskPhoneNumber(phoneNumber: String): String {
        if (phoneNumber.length < 5) {
            return phoneNumber
        }
        val maskedLength = phoneNumber.length - 5
        val maskedString =
            "*".repeat(maskedLength)
        return maskedString + phoneNumber.substring(phoneNumber.length - 5)
    }

    private fun clearOTP() {
        binding.textinputError.visibility=View.GONE
        binding.otpA.setText("")
        binding.otpB.setText("")
        binding.otpC.setText("")
        binding.otpD.setText("")
        binding.otpE.setText("")
        binding.otpF.setText("")
        binding.otpA.requestFocus()
    }

    private fun EditText.isEmpty(): Boolean {
        return text.toString().isEmpty()
    }

    private fun focusOnFirstIfEmpty() {
        if (binding.otpA.isEmpty()) {
            binding.otpA.requestFocus()
        }else{
            binding.textinputError.visibility=View.GONE
        }
    }
    private fun otpViewActions() {
        // Attach TextWatcher and focus listener to the EditText fields
        binding.otpA.addTextChangedListener(GenericTextWatcher(binding.otpA, binding.otpB))
        binding.otpA.addTextChangedListener {

            aOk = it != null && it.matches(digitRegex)
            checkValidationStatus()
            focusOnFirstIfEmpty()
            setClipboard(requireContext(), binding.otpA.text.toString())
        // Focus on the first EditText if it's empty
        }
        binding.otpB.addTextChangedListener(GenericTextWatcher(binding.otpB, binding.otpC))
        binding.otpB.addTextChangedListener {
            bOk = it != null && it.matches(digitRegex)
            checkValidationStatus()
            setClipboard(requireContext(), binding.otpB.text.toString())
        }
        binding.otpC.addTextChangedListener(GenericTextWatcher(binding.otpC, binding.otpD))
        binding.otpC.addTextChangedListener {
            cOk = it != null && it.matches(digitRegex)
            checkValidationStatus()
            setClipboard(requireContext(), binding.otpC.text.toString())
        }
        binding.otpD.addTextChangedListener(GenericTextWatcher(binding.otpD, binding.otpE))
        binding.otpD.addTextChangedListener {
            dOk = it != null && it.matches(digitRegex)
            checkValidationStatus()
            setClipboard(requireContext(), binding.otpD.text.toString())
        }
        binding.otpE.addTextChangedListener(GenericTextWatcher(binding.otpE, binding.otpF))
        binding.otpE.addTextChangedListener {
            eOk = it != null && it.matches(digitRegex)
            checkValidationStatus()
            setClipboard(requireContext(), binding.otpE.text.toString())
        }
        binding.otpF.addTextChangedListener(GenericTextWatcher(binding.otpF, null))
        binding.otpF.addTextChangedListener {
            fOk = it != null && it.matches(digitRegex)
            checkValidationStatus()
            setClipboard(requireContext(), binding.otpF.text.toString())
        }

        // Attach key listener to handle navigation between EditText fields
        binding.otpA.setOnKeyListener(GenericKeyEvent(binding.otpA, null))
        binding.otpB.setOnKeyListener(GenericKeyEvent(binding.otpB, binding.otpA))
        binding.otpC.setOnKeyListener(GenericKeyEvent(binding.otpC, binding.otpB))
        binding.otpD.setOnKeyListener(GenericKeyEvent(binding.otpD, binding.otpC))
        binding.otpE.setOnKeyListener(GenericKeyEvent(binding.otpE, binding.otpD))
        binding.otpF.setOnKeyListener(GenericKeyEvent(binding.otpF, binding.otpE))
    }


    private fun setTimer() {
        binding.resendText.visibility = View.GONE
        binding.timerGrp.visibility = View.VISIBLE
        startTimer()
    }

    private fun startTimer() {
        if (this@RegstrationOTPFragment::timer.isInitialized)
            timer.cancel()
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val s = "00:${(millisUntilFinished / 1000)}"
                binding.timer.text = s
                binding.resendText.visibility = View.GONE
            }

            override fun onFinish() {
                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        Const.disableButton(binding.confirmBtn)
                        binding.sec.visibility = View.GONE
                        if (isAdded) {
                            binding.timer.text = getString(R.string.otp_expired)
                        }
                        binding.resendText.visibility = View.VISIBLE
                        binding.textinputError.visibility =View.GONE

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 1000)
            }
        }
        timer.start()
    }


    private fun checkValidationStatus() {
        val textName = binding.timer.text

        if (aOk && bOk && cOk && dOk && eOk && fOk && !textName.equals(getString(R.string.otp_expired))) {
            Const.enableButton(binding.confirmBtn)
            binding.textinputError.visibility=View.GONE

        } else
            Const.disableButton(binding.confirmBtn)
    }

    private fun verifyPhoneNumberWithCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(args.verificationID.toString(), code)
        Log.d("verificationregotp", "verifyPhoneNumberWithCode: ${args.verificationID}")
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode(phoneNumber: String) {
        setTimer()
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(createCallbacks()
            )
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val user = task.result?.user
                    mobileVerify()
                } else {
                    // Sign in failed
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        binding.textinputError.visibility=View.VISIBLE
                        binding.textinputError.text="Invalid OTP"

                        binding.otpA.setText("")
                        binding.otpB.setText("")
                        binding.otpC.setText("")
                        binding.otpD.setText("")
                        binding.otpE.setText("")
                        binding.otpF.setText("")
                        binding.otpA.requestFocus()
                    }
                }
            }
    }

    companion object {
        private const val TAG = "OTPFragment"
    }

    private fun mobileVerify() {
        mobileViewModel.run {
            doctorPhone.value = prefManager.getMobileNo()
            verifyRegistrationMobileNumber()
        }
        phoneRegister()

    }

    private fun phoneRegister(){
        try{mobileViewModel.verifyPhoneNumber.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.SUCCESS->{
                        it.data?.let { resData->
                            when(resData.statusCode){
                                200->{
                                    Log.d("userAuth", "User is verified.")
                                    val prefManager = PrefManager(requireContext())
                                    //prefManager.setMobileNo(args.mobile.toString())
                                   // Log.d("setMobileNo", "setMobileNo:${prefManager.setMobileNo(args.mobile.toString())}")
                                    val entityId = resData.data.entity_id ?: 0 // Provide a default if entityId is null
                                    prefManager.setEntityId(entityId.toString())
                                    val doctorId = resData.data.doctor_id ?: 0 // Provide a default if doctorId is null
                                    prefManager.setDoctorId(doctorId.toString())
                                    val profileCompleted = resData.data.profile_completed
                                    if (profileCompleted==0){
                                        findNavController().navigate(RegstrationOTPFragmentDirections.actionRegstrationOTPFragmentToGoodNameFragment())
                                    }


//                                    if (profileCompleted == 0) {
//                                        // If profile is not completed, navigate to NameFragment
//                                    }
//                                    else{
//                                        Toast.makeText(requireContext(),"fill al fileds",Toast.LENGTH_SHORT).show()
//                                    }

                                }

                                400->{
                                    Log.d("userAuth", "User is verified.")
                                    val prefManager = PrefManager(requireContext())
                                    prefManager.setMobileNo(args.mobile.toString())
                                    Log.d("setMobileNo", "setMobileNo:${prefManager.setMobileNo(args.mobile.toString())}")
                                    val entityId = resData.data.entity_id ?: 0 // Provide a default if entityId is null
                                    prefManager.setEntityId(entityId.toString())
                                    Log.d("entityIDDD",entityId.toString())
                                    val doctorId = resData.data.doctor_id ?: 0 // Provide a default if doctorId is null
                                    prefManager.setDoctorId(doctorId.toString())
                                  //  findNavController().navigate(RegstrationOTPFragmentDirections.actionRegstrationOTPFragmentToGoodNameFragment())
                                    val profileCompleted = resData.data.profile_completed
                                    if(profileCompleted==1){
                                        findNavController().navigate(R.id.mobileFragment)
                                    }

                                //    Const.shortToast(requireContext(),resData.message.toString())

                                }

                                409->{
                                }

                                else -> Const.shortToast(requireContext(), resData.message.toString())
                            }

                        }
                    }

                    Status.ERROR -> {
                        Toast.makeText(requireContext(),"Token not updated in register login regi otp frag",Toast.LENGTH_SHORT).show()
                        Log.d("TokenLog", "403: Token expired, refreshing token1")
                        prefManager.setRefresh("1")
                        val authViewModel by viewModel<RegisterViewModel>()
                        Const.getNewTokenAPI(
                            requireContext(),
                            authViewModel,
                            viewLifecycleOwner
                        )
                    }
                    Status.LOADING->{

                    }
                }
            }
        }
        }
        catch(e:Exception){

        }
    }

    private fun setClipboard(context: Context, text: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.text = text
        } else {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)
        }
    }

}

