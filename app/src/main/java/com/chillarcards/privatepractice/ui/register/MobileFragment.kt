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
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentMobileBinding
import com.chillarcards.privatepractice.utills.Const
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class MobileFragment : Fragment() {

    lateinit var binding: FragmentMobileBinding

    private val mobileRegex = "^[7869]\\d{9}$".toRegex()

    private var statusTrue: Boolean = false
    private var tempMobileNo: String = ""
    private var selectedItemId: Int = -1

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var mVerificationId = ""
    private lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken

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
            activity?.let { activity?.packageManager!!.getPackageInfo(it.packageName, PackageManager.GET_ACTIVITIES) }
        val versionName = pInfo?.versionName //Version Name
        FirebaseApp.initializeApp(this.requireContext())
        firebaseAuth = FirebaseAuth.getInstance()

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)

                when (e) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        // Invalid request
                        Const.shortToast(requireContext(), "Invalid phone number format.")
                    }
                    is FirebaseTooManyRequestsException -> {
                        // The SMS quota for the project has been exceeded
                        Const.shortToast(requireContext(), "SMS quota exceeded. Please try again later.")
                    }
                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        // reCAPTCHA verification attempted with null Activity
                        Const.shortToast(requireContext(), "reCAPTCHA verification failed.")
                    }
                    else -> {
                        // Handle other Firebase exceptions
                        Const.shortToast(requireContext(), "An error occurred: ${e.message}")
                    }
                }

                binding.loginBtn.visibility = View.VISIBLE
                binding.waitingBtn.visibility = View.GONE
                hideProgress()
            }


//            override fun onVerificationFailed(e: FirebaseException) {
//                // This callback is invoked in an invalid request for verification is made,
//                // for instance if the the phone number format is not valid.
//                Log.w(TAG, "onVerificationFailed", e)
//
//                Const.shortToast(requireContext(),"Failed to send OTP to given mobile number")
//                binding.loginBtn.visibility =View.VISIBLE
//                binding.waitingBtn.visibility =View.GONE
//                hideProgress()
//            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number,
                // we now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                mVerificationId = verificationId
                mResendToken = token
                Log.d(TAG, "onCodeSent:$verificationId")
                findNavController().navigate(
                    MobileFragmentDirections.actionMobileFragmentToOTPFragment(tempMobileNo,
                        mVerificationId
                    )
                )
                Const.shortToast(requireContext(),"OTP sent successfully")
            }
        }

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
                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                } else {
                    binding.mobile.error = null
                    binding.mobile.isErrorEnabled = false
                    Const.enableButton(binding.loginBtn)
                    tempMobileNo = input
                }
            }
            else {
                binding.mobile.error = null
                binding.mobile.isErrorEnabled = false
            }
        }

        setUpObserver()

//        binding.loginBtn.setOnClickListener {
//            val input = binding.mobileEt.text.toString()
//            when {
//                !mobileRegex.containsMatchIn(input) -> {
//                    binding.mobile.error = getString(R.string.mob_validation)
//                }
//
//                else -> {
//                    // Perform reCAPTCHA validation
//                    safetyNetClient.verifyWithRecaptcha(siteKey)
//                        .addOnSuccessListener { response ->
//                            val userResponseToken = response.tokenResult
//                            if (!userResponseToken.isNullOrEmpty()) {
//                                // Validate the reCAPTCHA token on your server
//                                validateTokenOnServer(userResponseToken) { isValid ->
//                                    if (isValid) {
//                                        // Proceed with phone number verification if reCAPTCHA is valid
//                                        startPhoneNumberVerification(input)
//                                    } else {
//                                        // Handle invalid reCAPTCHA token
//                                        Toast.makeText(
//                                            requireContext(),
//                                            "reCAPTCHA validation failed. Please try again.",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                        binding.loginBtn.visibility = View.VISIBLE
//                                        binding.waitingBtn.visibility = View.GONE
//
//                                    }
//                                }
//                            } else {
//                                // Handle empty token case
//                                Toast.makeText(
//                                    requireContext(),
//                                    "reCAPTCHA validation failed. Please try again.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                binding.loginBtn.visibility = View.VISIBLE
//                                binding.waitingBtn.visibility = View.GONE
//
//                            }
//                        }
//                        .addOnFailureListener { e ->
//                            // Handle reCAPTCHA failure
//                            Log.e("reCAPTCHA", "Error: ${e.message}")
//                            Toast.makeText(
//                                requireContext(),
//                                "reCAPTCHA verification failed. Please try again.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            binding.loginBtn.visibility = View.VISIBLE
//                            binding.waitingBtn.visibility = View.GONE
//
//                        }
//                }
//            }
//        }


        binding.loginBtn.setOnClickListener {
            val input = binding.mobileEt.text.toString()
            when {
                !mobileRegex.containsMatchIn(input) -> {
                    binding.mobile.error = getString(R.string.mob_validation)
                }
                else -> {
                    binding.loginBtn.visibility =View.GONE
                    binding.waitingBtn.visibility =View.VISIBLE
                    showProgress()
                    startPhoneNumberVerification(input)
                }
            }
        }

        binding.waitingBtn.setOnClickListener{
            Const.shortToast(requireContext(),"Please check your message inbox")
        }

        setTextColorForTerms()

    }


    fun onLoadSMS(){
        // on the below line we are creating a try and catch block
        try {

            val message ="123456 is your verification OTP for accessing the KR COIN wallet. Do not share this OTP or your number with anyone.yaMqX9A+vNH"
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

    companion object {
        private const val TAG = "MobileFragment"
    }
}