package com.chillarcards.privatepractice.ui.register


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentBankBinding
import com.chillarcards.privatepractice.utills.Const
import com.chillarcards.privatepractice.utills.PrefManager
import com.chillarcards.privatepractice.utills.Status
import com.chillarcards.privatepractice.viewmodel.BankViewModel
import com.chillarcards.privatepractice.viewmodel.RegisterViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class BankFragment : Fragment() {

    lateinit var binding: FragmentBankBinding
    private val bankViewModel by viewModel<BankViewModel>()
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bank, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager(requireContext())

        Const.enableButton(binding.confirmBtn)
        setToolbar()
        bankViewModel.run {
            getBankDetails()
        }
        setUpObserver()

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+91 9995699899") // Replace with the actual phone number
            startActivity(intent)
        }

        binding.confirmBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setToolbar() {
        binding.toolbar.toolbarTitle.text = getString(R.string.bank_head)
        binding.toolbar.toolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun setUpObserver() {
        try {
            bankViewModel.bankData.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            hideProgress()
                            it.data?.let { bankData ->
                                when (bankData.statusCode) {
                                    200 -> {
                                        // TODO: check if response from verifying otp or sending otp
                                        binding.bankAccountEt.setText(bankData.data.bankdata.account_no)
                                        binding.bankNameEt.setText(bankData.data.bankdata.bank_name)
                                        binding.bankHolderNameEt.setText(bankData.data.bankdata.account_holder_name)
                                        binding.bankIfscEt.setText(bankData.data.bankdata.ifsc_code)

                                    }
                                    else -> Const.shortToast(requireContext(), bankData.message)

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

                //    bankViewModel.clear()
                }
            }

        } catch (e: Exception) {
            Log.e("abc_otp", "setUpObserver: ", e)
        }
    }

    private fun showProgress() {
     //   binding.confirmBtn.visibility = View.INVISIBLE
        binding.otpProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
      //  binding.confirmBtn.visibility = View.VISIBLE
        binding.otpProgress.visibility = View.GONE
    }

}
