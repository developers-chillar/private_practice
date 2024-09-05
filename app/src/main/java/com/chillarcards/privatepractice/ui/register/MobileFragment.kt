package com.chillarcards.privatepractice.ui.register

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentMobileBinding
import com.chillarcards.privatepractice.utills.Const
import com.chillarcards.privatepractice.utills.Status
import com.chillarcards.privatepractice.viewmodel.MobileScreenViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class MobileFragment : Fragment() {

    lateinit var binding: FragmentMobileBinding

    private val mobileRegex = "^[7869]\\d{9}$".toRegex()

    private var statusTrue: Boolean = false
    private var tempMobileNo: String = ""
    private var selectedItemId: Int = -1
    private val mobileScreenViewModel by viewModel<MobileScreenViewModel>()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var mVerificationId = ""
    private lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken
    private val args: MobileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMobileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pInfo =
            activity?.let {
                activity?.packageManager!!.getPackageInfo(
                    it.packageName,
                    PackageManager.GET_ACTIVITIES
                )
            }
        val versionName = pInfo?.versionName //Version Name
        FirebaseApp.initializeApp(this.requireContext())
        firebaseAuth = FirebaseAuth.getInstance()
        invokeFirebaseOTPService()

        binding.version.text = "${getString(R.string.version)}" + Const.ver_title + versionName

        binding.mobileEt.addTextChangedListener {
            val input = it.toString()
            if (input.isNotEmpty()) {
                if (!input.matches(mobileRegex)) {
                    binding.mobile.error = "Enter a valid mobile number"
                    Const.disableButton(binding.loginBtn)
                } else if (input.length == 10 && input.matches(mobileRegex)) {
                    binding.mobile.error = null
                    binding.mobile.isErrorEnabled = false
                    Const.enableButton(binding.loginBtn)
                    tempMobileNo = input
                    val imm =
                        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                } else {
                    binding.mobile.error = null
                    binding.mobile.isErrorEnabled = false
                    Const.enableButton(binding.loginBtn)
                    tempMobileNo = input
                }
            } else {
                binding.mobile.error = null
                binding.mobile.isErrorEnabled = false
            }
        }

        setUpObserver()

        binding.loginBtn.setOnClickListener {

            val input = binding.mobileEt.text.toString()
            when {
                !mobileRegex.containsMatchIn(input) -> {
                    showProgress()
                    binding.mobile.error = getString(R.string.mob_validation)
                }

                else -> {

                    binding.loginBtn.visibility = View.GONE
                    binding.waitingBtn.visibility = View.VISIBLE
                    phoneVerify()

                }
            }
        }

        binding.waitingBtn.setOnClickListener {
            Const.shortToast(requireContext(), "Please check your message inbox")
        }

        setTextColorForTerms()

    }



    private fun invokeFirebaseOTPService() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithCredential:success")
                            val user = task.result?.user
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                        }
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        Const.shortToast(requireContext(), "Invalid phone number format.")
                    }
                    is FirebaseTooManyRequestsException -> {
                        Const.shortToast(requireContext(), "SMS quota exceeded. Please try again later.")
                    }
                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        Const.shortToast(requireContext(), "reCAPTCHA verification failed.")
                    }
                    else -> {
                        Const.shortToast(requireContext(), "An error occurred: ${e.message}")
                    }
                }

                binding.loginBtn.visibility = View.VISIBLE
                binding.waitingBtn.visibility = View.GONE
                hideProgress()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                hideProgress()
                mVerificationId = verificationId
                mResendToken = token
                Log.d(TAG, "onCodeSent:$verificationId")
                findNavController().navigate(
                    MobileFragmentDirections.actionMobileFragmentToOTPFragment(
                        tempMobileNo,
                        mVerificationId
                    )
                )
                hideProgress()
                Const.shortToast(requireContext(), "OTP sent successfully")
            }
        }

    }


    fun onLoadSMS() {
        // on the below line we are creating a try and catch block
        try {

            val message =
                "123456 is your verification OTP for accessing the KR COIN wallet. Do not share this OTP or your number with anyone.yaMqX9A+vNH"
            val uri: Uri = Uri.parse("smsto:+91")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            intent.putExtra("sms_body", message)
            startActivity(intent)

        } catch (e: Exception) {
            // on catch block we are displaying toast message for error.
        }
    }

    private fun setUpObserver() {

    }

    private fun showProgress() {
        binding.loginProgress.visibility = View.VISIBLE
        binding.loginBtn.visibility = View.INVISIBLE
    }

    private fun hideProgress() {
        binding.loginProgress.visibility = View.GONE
        binding.loginBtn.visibility = View.VISIBLE
    }

    private fun setTextColorForTerms() {
        try {
            val s = "Terms and Conditions"
            val wordToSpan: Spannable = SpannableString(s)
            val click: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val browserIntent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.chillarpayments.com/terms-and-conditions.html")
                        )
                    startActivity(browserIntent)
                }
            }
            wordToSpan.setSpan(
                click, s.indexOf("Terms"), s.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            wordToSpan.setSpan(
                StyleSpan(Typeface.BOLD), s.indexOf("Terms"), s.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            wordToSpan.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                ),
                s.indexOf("Terms"), s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.terms1.text = wordToSpan
            binding.terms1.movementMethod = LinkMovementMethod.getInstance()
        } catch (e: Exception) {
            Log.e("abc_mobile", "setTextColorForTerms: msg: ", e)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("abc_mob", "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("abc_mob", "onDestroy: ")
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun forceCrash() {
        throw RuntimeException("Test Crash") // Force a crash
    }

    private fun phoneVerify() {
        mobileScreenViewModel.clear()
        mobileScreenViewModel.run {
            phone.value = binding.mobileEt.text.toString()
            userCheck()
        }
        userCheckObserver()
    }

    private fun userCheckObserver() {
        try {
            mobileScreenViewModel.userCheck.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.data?.let { userCheck ->
                                when (userCheck.statusCode) {
                                    200 -> {
                                        Log.d(TAG, "User is verified.")
                                        tempMobileNo.let {
                                            startPhoneNumberVerification(it)
                                        }


                                        /*Const.shortToast(requireContext(),"User Verified")
                                       findNavController().navigate(MobileFragmentDirections.actionMobileFragmentToOTPFragment(tempMobileNo,
                                           mVerificationId))*/
                                    }

                                    404 -> {
                                        Log.d(TAG, "Status Code 404: ${userCheck.message}")
                                        Const.shortToast(
                                            requireContext(),
                                            "Not a registered phone number. Please contact customer support!"
                                        )
                                        binding.loginBtn.visibility = View.VISIBLE
                                        binding.waitingBtn.visibility = View.GONE
                                        hideProgress()
                                    }

                                    else ->   Log.d(TAG, "Unhandled status code: ${userCheck.statusCode}")
                                }
                            }
                        }

                        Status.ERROR -> {
                            Log.e(TAG, "Error response: ${it.message}")
                            Const.shortToast(requireContext(), "Error!")


                        }

                        Status.LOADING -> {
                            Log.e(TAG, "Error: ${it.message}")
                            binding.loginBtn.visibility = View.VISIBLE
                            binding.waitingBtn.visibility = View.GONE
                            hideProgress()
                        }
                    }
                }

            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ", e)
            Const.shortToast(requireContext(), "An error occurred. Please try again.")
            binding.loginBtn.visibility = View.VISIBLE
            binding.waitingBtn.visibility = View.GONE
            hideProgress()
        }
    }

    companion object {
        private const val TAG = "MobileFragment"
    }

}