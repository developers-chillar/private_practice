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
    private val mobileRegex = "^[7869]\\d{9}$".toRegex()
    private var tempMobileNo: String = ""
    private val mobileScreenViewModel by viewModel<MobileScreenViewModel>()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var mVerificationId = ""
    private lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
    binding=FragmentSelfRegistrationBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseApp.initializeApp(this.requireContext())
        firebaseAuth = FirebaseAuth.getInstance()
        invokeFirebaseOTPService()
        binding.etGdName.addTextChangedListener {
            val input = it.toString()
            if (input.isNotEmpty()) {
                if (!input.matches(mobileRegex)) {
                    binding.mobile.error = "Enter a valid mobile number"
                 //   Const.disableButton(binding.loginBtn)
                } else if (input.length == 10 && input.matches(mobileRegex)) {
                    binding.mobile.error = null
                    binding.mobile.isErrorEnabled = false
                 //   Const.enableButton(binding.loginBtn)
                    tempMobileNo = input
                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                } else {
                    binding.mobile.error = null
                    binding.mobile.isErrorEnabled = false
                 //   Const.enableButton(binding.loginBtn)
                    tempMobileNo = input
                }
            } else {
                binding.mobile.error = null
                binding.mobile.isErrorEnabled = false
            }
        }



        binding.loginBtn.setOnClickListener {
            val input = binding.etGdName.text.toString()
            when {
                !mobileRegex.containsMatchIn(input) -> {
                    //   showProgress()
                    binding.mobile.error = getString(R.string.mob_validation)
                }

                else -> {

                 //   binding.loginBtn.visibility = View.GONE
                  //  binding.waitingBtn.visibility = View.VISIBLE
                    phoneVerify()

                }
            }
            binding.progressBar.visibility=View.VISIBLE
        }
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
//                        binding.waitingBtn.visibility = View.GONE
                        binding.progressBar.visibility=View.GONE
                    }
                    is FirebaseTooManyRequestsException -> {
                        Const.shortToast(requireContext(), "SMS quota exceeded. Please try again later.")
//                        binding.waitingBtn.visibility = View.GONE
//                        binding.progressBar.visibility=View.GONE
                    }
                    is FirebaseAuthMissingActivityForRecaptchaException -> {
                        Const.shortToast(requireContext(), "reCAPTCHA verification failed.")
//                        binding.waitingBtn.visibility = View.GONE
                       binding.progressBar.visibility=View.GONE
                    }
                    else -> {
                        Const.shortToast(requireContext(), "An error occurred: ${e.message}")
//                        binding.waitingBtn.visibility = View.GONE
                        binding.progressBar.visibility=View.GONE
                    }
                }
                binding.progressBar.visibility=View.GONE

                binding.loginBtn.visibility = View.VISIBLE
              //  binding.waitingBtn.visibility = View.GONE
              //  hideProgress()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
              //  hideProgress()
                binding.progressBar.visibility=View.GONE
                mVerificationId = verificationId
                Log.d("verification","id verify:$mVerificationId")
                mResendToken = token
                Log.d("onCodeSent", "onCodeSent:$verificationId")
                findNavController().navigate(SelfRegistrationFragmentDirections.actionSelfRegistrationFragmentToRegstrationOTPFragment(tempMobileNo,mVerificationId))
             //   hideProgress()
                Const.shortToast(requireContext(), "OTP sent successfully")
            }
        }

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

    private fun phoneVerify() {
        mobileScreenViewModel.clear()
        mobileScreenViewModel.run {
            phone.value = binding.etGdName.text.toString()
            verifyRegistrationMobileNumber()
        }
        phoneRegister()
    }

    private fun phoneRegister(){
        try{mobileScreenViewModel.verifyPhoneNumber.observe(viewLifecycleOwner){
            if(it!=null){
                when(it.status){
                    Status.SUCCESS->{
                        it.data?.let { resData->
                            when(resData.statusCode){
                                200->{
                                    Log.d("userAuth", "User is verified.")
                                    val prefManager = PrefManager(requireContext())
                                    prefManager.setPhoneNumber(binding.etGdName.text.toString())
                                    val entityId = resData.data.entityId ?: 0
                                    prefManager.setIntEntityId(entityId)
                                    val doctorId=resData.data.doctorId
                                    prefManager.setDoctorId(doctorId.toString())
                                    tempMobileNo.let {
                                        startPhoneNumberVerification(it)
                                    }

                                }

                                400->{
                                    Const.shortToast(requireContext(),resData.message.toString())
                                    binding.progressBar.visibility=View.GONE
                                }

                                409->{
                                    binding.progressBar.visibility=View.GONE
                                 //   findNavController().navigate(SelfRegistrationFragmentDirections.actionSelfRegistrationFragmentToOTPFragment(tempMobileNo))
                                }

                                else -> Const.shortToast(requireContext(), resData.message.toString())
                            }

                        }
                    }

                    Status.ERROR->{
                        Const.shortToast(requireContext(),it.message.toString())
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

    companion object {
       private const val TAG = "SelfRegistrationFragment"
    }

}