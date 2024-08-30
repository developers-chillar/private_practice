package com.chillarcards.privatepractice.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.chillarcards.privatepractice.R
import com.chillarcards.privatepractice.databinding.FragmentProfileBinding
import com.chillarcards.privatepractice.ui.adapter.FeeAdapter
import com.chillarcards.privatepractice.ui.interfaces.IAdapterViewUtills
import com.chillarcards.privatepractice.utills.CommonDBaseModel
import com.chillarcards.privatepractice.utills.Const
import com.chillarcards.privatepractice.utills.PrefManager
import com.chillarcards.privatepractice.utills.Status
import com.chillarcards.privatepractice.viewmodel.ProfileViewModel
import com.chillarcards.privatepractice.viewmodel.RegisterViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment(), IAdapterViewUtills {

    lateinit var binding: FragmentProfileBinding
    private lateinit var prefManager: PrefManager
    private val profileViewModel by viewModel<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
      //  Const.enableButton(binding.confirmBtn)
        prefManager = PrefManager(requireContext())

        profileViewModel.mob.value = prefManager.getMobileNo()
        profileViewModel.getProfile()

        setUpObserver()

            binding.floatingActionButton.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:+91 9995699899") // Replace with the actual phone number
                startActivity(intent)

            // showChooseImageDialog()
        }

        binding.confirmBtn.setOnClickListener{
            findNavController().popBackStack()
        }

     }
    private fun setUpObserver() {
        try {
            profileViewModel.profileData.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            hideProgress()
                            it.data?.let { profileData ->
                                when (profileData.statusCode) {
                                    200 -> {
                                        // TODO: check if response from verifying otp or sending otp
                                        binding.pName.setText(profileData.data.doctor_name)
                                        binding.pQualifi.setText(profileData.data.qualification)
                                        binding.pDesi.setText(profileData.data.departmentName)
                                        binding.pDetail.setText(profileData.data.description)
                                        val consultationChargeText = profileData.data.consultation_charge.toString()


                                        //HIDE VIEW AS PER FIRST PHASE
                                        if(profileData.data.additionalInfo.isNotEmpty()) {
                                            binding.topFeeFrame.visibility=View.GONE
                                            binding.pFee.visibility=View.GONE

                                            val salesTopPicAdapter = FeeAdapter(
                                                profileData.data.additionalInfo,
                                                requireContext(),
                                            )
                                            binding.topPicRv.adapter = salesTopPicAdapter
                                            binding.topPicRv.layoutManager = LinearLayoutManager(
                                                context,
                                                LinearLayoutManager.VERTICAL,
                                                false
                                            )

                                        }else{
                                            //binding.pFee.setText("₹"+consultationChargeText)

                                        }
                                        Glide.with(requireActivity())
                                            .load(profileData.data.profileImageUrl)
//                                            .load("https://www.chillarpayments.com/Demo/Direct-Book/images/Wc2xbVeJl0d6cyfWGCxlvcsxxYogVqsJElJy5tvN.jpeg")
                                            .centerCrop()
                                            .transform(CircleCrop())
                                            .into(binding.imProfile)
                                    }
                                    403 -> {
                                        prefManager.setRefresh("1")
                                        val authViewModel by viewModel<RegisterViewModel>()
                                        Const.getNewTokenAPI(
                                            requireContext(),
                                            authViewModel,
                                            viewLifecycleOwner
                                        )

                                        profileViewModel.run {
                                            mob.value = prefManager.getMobileNo()
                                            getProfile()
                                        }
                                        setUpObserver()
                                    }
                                    422 -> {
                                        prefManager.setRefresh("1")
                                        val authViewModel by viewModel<RegisterViewModel>()
                                        Const.getNewTokenAPI(
                                            requireContext(),
                                            authViewModel,
                                            viewLifecycleOwner
                                        )

                                        profileViewModel.run {
                                            mob.value = prefManager.getMobileNo()
                                            getProfile()
                                        }
                                        setUpObserver()
                                    }
                                    else -> Const.shortToast(requireContext(),"dfgdfgdfgd"+ profileData.message)

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
                }
             //   profileViewModel.clear()
            }


        } catch (e: Exception) {
            Log.e("abc_otp", "setUpObserver: ", e)
        }
    }
    private fun showProgress() {
    //    binding.confirmBtn.visibility = View.INVISIBLE
        binding.otpProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
    //    binding.confirmBtn.visibility = View.VISIBLE
        binding.otpProgress.visibility = View.GONE
    }
    private fun setToolbar() {
        binding.toolbar.toolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.toolbarTitle.text = getString(R.string.profile)
    }

    override fun getAdapterPosition(
        Position: Int,
        ValueArray: ArrayList<CommonDBaseModel>,
        Mode: String?
    ) {
//        if(Mode.equals("VIEW")) {
//            val bottomSheetFragment = BottomSheetFragment(ValueArray)
//            bottomSheetFragment.show(
//                (context as AppCompatActivity).supportFragmentManager,
//                bottomSheetFragment.tag
//            )
//        }
    }

    private fun showChooseImageDialog() {
        val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.dialog_choose_image, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)

        val galleryOption: TextView = bottomSheetView.findViewById(R.id.galleryOption)
        val cameraOption: TextView = bottomSheetView.findViewById(R.id.cameraOption)
        val cancelOption: TextView = bottomSheetView.findViewById(R.id.cancelOption)

        galleryOption.setOnClickListener {
            bottomSheetDialog.dismiss()
            chooseImageFromGallery()
        }

        cameraOption.setOnClickListener {
            bottomSheetDialog.dismiss()
            openCamera()
        }

        cancelOption.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun chooseImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_IMAGE_FROM_GALLERY)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
//                REQUEST_IMAGE_FROM_GALLERY -> {
//                    data?.data?.let { uri ->
//                        setProfileImage(uri)
//                    }
//                }
                REQUEST_IMAGE_FROM_GALLERY -> {
                    if (resultCode == Activity.RESULT_OK) {
                        val selectedImageUri = data?.data
                        val imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
                        setProfileImage(imageBitmap)
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    data?.extras?.get("data")?.let { bitmap ->
                        setProfileImage(bitmap as Bitmap)
                    }
                }
            }
        }
    }

    private fun setProfileImage(uri: Bitmap) {
        binding.imProfile.setImageBitmap(uri)
    }

    companion object {
        const val REQUEST_IMAGE_FROM_GALLERY = 2
        const val REQUEST_IMAGE_CAPTURE = 1
    }
}