package com.chillarcards.privatepractice.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillarcards.privatepractice.data.model.AddLeaveResClass
import com.chillarcards.privatepractice.data.model.BookingResponseModel
import com.chillarcards.privatepractice.data.model.DoctorLeaveResClass
import com.chillarcards.privatepractice.data.model.ShareLinkResponseModel
import com.chillarcards.privatepractice.data.model.StatusResponseModel
import com.chillarcards.privatepractice.data.repository.AuthRepository
import com.chillarcards.privatepractice.utills.NetworkHelper
import com.chillarcards.privatepractice.utills.PrefManager
import com.chillarcards.privatepractice.utills.Resource
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

/**
 * @Author: Sherin
 * @Date: 07-02-2024$
 * Chillar
 */
class BookingViewModel(
    private val authRepository: AuthRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {
    private val _bookingData = MutableLiveData<Resource<BookingResponseModel>?>()
    val bookingData: LiveData<Resource<BookingResponseModel>?> get() = _bookingData
    private val _bookUpdateData = MutableLiveData<Resource<StatusResponseModel>?>()
    val bookStatusData: LiveData<Resource<StatusResponseModel>?> get() = _bookUpdateData
    private val _bookLinkData = MutableLiveData<Resource<ShareLinkResponseModel>?>()
    val bookLinkData: LiveData<Resource<ShareLinkResponseModel>?> get() = _bookLinkData
    private val _doctorOnLeave=MutableLiveData<Resource<AddLeaveResClass>?>()
    val doctorOnLeave:LiveData<Resource<AddLeaveResClass>?> get() = _doctorOnLeave

    private val _doctorLeaveDate=MutableLiveData<Resource<DoctorLeaveResClass>?>()
    val doctorLeaveDate:LiveData<Resource<DoctorLeaveResClass>?> get() = _doctorLeaveDate

    var doctorID = MutableLiveData<String>()
    var date = MutableLiveData<String>()
    var entityId = MutableLiveData<String>()
    var bookingId = MutableLiveData<String>()

    var doctorId = MutableLiveData<Int>()
    var entityID = MutableLiveData<Int>()

    fun getBookingList() {
        viewModelScope.launch(NonCancellable) {
            try {
                _bookingData.postValue(Resource.loading(null))
                if (networkHelper.isNetworkConnected()) {
                    authRepository.getBookigDetails(
                        doctorID.value.toString(),
                        date.value.toString(),
                        entityId.value.toString()
                    ).let {
                        if (it.isSuccessful) {
                            Log.e("getBookingList0", "API call succes with code: ${it.code()}, message: ${it.message()}, error: ${it.body()}")
                            _bookingData.postValue(Resource.success(it.body()))
                        }
                        else {
                            val errorBody = it.errorBody()?.string() ?: "Unknown error"
                            Log.e("getBookingList0", "API call failed with code: ${it.code()}, message: ${it.message()}, error: $errorBody")
                            _bookingData.postValue(Resource.error("Error ${it.code()}: ${it.message()}", null))


                        }
                    }
                } else {
                    _bookingData.postValue(Resource.error("No Internet Connection", null))
                }
            } catch (e: Exception) {
                Log.e("getBookingList0", "Exception occurred: ", e)
                _bookingData.postValue(Resource.error("An unexpected error occurred", null))

            }
        }
    }

    fun getConfirmBooking() {
        viewModelScope.launch(NonCancellable) {
            try {
                _bookUpdateData.postValue(Resource.loading(null))
                if (networkHelper.isNetworkConnected()) {
                    authRepository.getUpdate(
                        bookingId.value.toString()
                    ).let {
                        if (it.isSuccessful) {
                            _bookUpdateData.postValue(Resource.success(it.body()))
                        } else {
                            Log.e("getBookingList0", "API call failed with code: ${it.code()}, message: ${it.message()}, ")

                            _bookUpdateData.postValue(Resource.error(it.errorBody().toString(), null))
                        }
                    }
                } else {
                    _bookUpdateData.postValue(Resource.error("No Internet Connection", null))
                }
            } catch (e: Exception) {
                Log.e("abc_otp", "verifyOTP: ", e)
            }
        }
    }
    fun getShareLink() {
        viewModelScope.launch(NonCancellable) {
            try {
                _bookLinkData.postValue(Resource.loading(null))
                if (networkHelper.isNetworkConnected()) {
                    authRepository.getShareLink().let {
                        if (it.isSuccessful) {
                            _bookLinkData.postValue(Resource.success(it.body()))
                        } else {
                            Log.e("getBookingList0", "API call failed with code: ${it.code()}, message: ${it.message()}")

                            _bookLinkData.postValue(Resource.error(it.errorBody().toString(), null))
                        }
                    }
                } else {
                    _bookLinkData.postValue(Resource.error("No Internet Connection", null))
                }
            } catch (e: Exception) {
                Log.e("abc_otp", "verifyOTP: ", e)
            }
        }
    }

    fun addDoctorOnLeave(){
        viewModelScope.launch(NonCancellable) {
            try {
                _doctorOnLeave.postValue(Resource.loading(null))
                if (networkHelper.isNetworkConnected()) {
                    authRepository.getDoctorAvailability(
                        doctorID.value.toString(),
                        date.value.toString()
                    ).let {
                        if (it.isSuccessful) {
                            _doctorOnLeave.postValue(Resource.success(it.body()))
                        } else {
                            Log.e("getBookingList0", "API call failed with code: ${it.code()}, message: ${it.message()}")

                            _doctorOnLeave.postValue(Resource.error(it.errorBody().toString(), null))
                        }
                    }
                } else {
                    _doctorOnLeave.postValue(Resource.error("No Internet Connection", null))
                }
            } catch (e: Exception) {
                Log.e("abc_otp", "verifyOTP: ", e)
            }
        }
    }

    fun DoctorLeaveDates(){
        viewModelScope.launch(NonCancellable) {
            try {
                _doctorLeaveDate.postValue(Resource.loading(null))
                if (networkHelper.isNetworkConnected()) {
                    doctorID.value?.toInt()?.let {
                        entityId.value?.toInt()?.let { it1 ->
                            authRepository.getDoctorLeave(
                                it,
                                it1
                            ).let {
                                if (it.isSuccessful) {
                                    Log.e("getBookingList0", "API call failed with code: ${it.body()}")
                                    _doctorLeaveDate.postValue(Resource.success(it.body()))
                                } else {
                                    Log.e("getBookingList0", "API call failed with code: ${it.code()}, message: ${it.message()}")

                                    _doctorLeaveDate.postValue(Resource.error(it.errorBody().toString(), null))
                                }
                            }
                        }
                    }
                } else {
                    _doctorLeaveDate.postValue(Resource.error("No Internet Connection", null))
                }
            } catch (e: Exception) {
                Log.e("DoctorLeaveDates", "Error fetching leave dates: ", e)
            }
        }

    }





    fun clear() {
        _bookingData.value = null
        _bookUpdateData.value = null
        _bookLinkData.value = null
        _doctorOnLeave.value=null
    }
}