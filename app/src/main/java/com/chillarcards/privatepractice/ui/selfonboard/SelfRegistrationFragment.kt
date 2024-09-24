package com.chillarcards.privatepractice.ui.selfonboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentSelfRegistrationBinding
import com.chillarcards.privatepractice.ui.register.MobileFragmentArgs
import com.chillarcards.privatepractice.utills.Const
import com.chillarcards.privatepractice.utills.PrefManager
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

class SelfRegistrationFragment : Fragment(R.layout.fragment_self_registration) {

    lateinit var binding: FragmentSelfRegistrationBinding
    private val mobileRegex = "^[7869]\\d{9}$".toRegex() // Regex for valid Indian numbers
    private var tempMobileNo: String = ""
    private val mobileScreenViewModel by viewModel<MobileScreenViewModel>()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var mVerificationId = ""
    private lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelfRegistrationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager=PrefManager(requireContext())
        FirebaseApp.initializeApp(this.requireContext())
        firebaseAuth = FirebaseAuth.getInstance()
        invokeFirebaseOTPService()

        // Add mobile number validation
        binding.etGdName.addTextChangedListener {
            val input = it.toString()
            if (input.isNotEmpty()) {
                if (!input.matches(mobileRegex)) {
                    binding.mobile.error = "Enter a valid mobile number"
                } else {
                    binding.mobile.error = null
                    tempMobileNo = input
                }
            }
        }

        binding.loginBtn.setOnClickListener {
            val input = binding.etGdName.text.toString()
            if (!input.matches(mobileRegex)) {
                binding.mobile.error = getString(R.string.mob_validation)
            } else {
                // Hide the error and trigger the phone verification
                binding.mobile.error = null
                binding.progressBar.visibility = View.VISIBLE
                phoneVerify()
            }
        }
    }

    private fun invokeFirebaseOTPService() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Automatically sign in when the verification is completed (instant verification)
                firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                        } else {
                            binding.progressBar.visibility = View.GONE
                            Log.e("FirebaseAuth", "Sign-in failed: ${task.exception?.message}")
                        }
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                binding.progressBar.visibility = View.GONE
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
                        Const.shortToast(requireContext(), "Error: ${e.message}")
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                binding.progressBar.visibility = View.GONE
                mVerificationId = verificationId
                mResendToken = token
                val verificationID=prefManager.SetVerificatiobID(mVerificationId)
                Log.d("onCodeSent", "OTP Sent. Verification ID: $verificationId")
                findNavController().navigate(SelfRegistrationFragmentDirections.actionSelfRegistrationFragmentToRegstrationOTPFragment(tempMobileNo, verificationID.toString()))
                Const.shortToast(requireContext(), "OTP sent successfully")
            }
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun phoneVerify() {
        val inputMobile = binding.etGdName.text.toString()
        if (inputMobile.matches(mobileRegex)) {
            startPhoneNumberVerification(tempMobileNo)
        } else {
            binding.mobile.error = "Invalid mobile number"
        }
    }
}
