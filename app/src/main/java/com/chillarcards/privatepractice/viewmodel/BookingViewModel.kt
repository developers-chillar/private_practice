package com.chillarcards.privatepractice.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillarcards.privatepractice.data.model.BookingResponseModel
import com.chillarcards.privatepractice.data.model.ShareLinkResponseModel
import com.chillarcards.privatepractice.data.model.StatusResponseModel
import com.chillarcards.privatepractice.data.repository.AuthRepository
import com.chillarcards.privatepractice.utills.NetworkHelper
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
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val _bookingData = MutableLiveData<Resource<BookingResponseModel>?>()
    val bookingData: LiveData<Resource<BookingResponseModel>?> get() = _bookingData
    private val _bookUpdateData = MutableLiveData<Resource<StatusResponseModel>?>()
    val bookStatusData: LiveData<Resource<StatusResponseModel>?> get() = _bookUpdateData
    private val _bookLinkData = MutableLiveData<Resource<ShareLinkResponseModel>?>()
    val bookLinkData: LiveData<Resource<ShareLinkResponseModel>?> get() = _bookLinkData

    var doctorID = MutableLiveData<String>()
    var date = MutableLiveData<String>()
    var entityId = MutableLiveData<String>()
    var bookingId = MutableLiveData<String>()

    fun getBookingList() {
        viewModelScope.launch(NonCancellable) {
            try {
                _bookingData.postValue(Resource.loading(null))
                if (networkHelper.isNetworkConnected()) {
                    authRepository.getBookigDetails(
                        doctorID.value.toString(),
                        date.value.toString(),
                        entityId.value.toString(),
                    ).let {
                        if (it.isSuccessful) {
                            _bookingData.postValue(Resource.success(it.body()))
                        } else {
                            Log.e("abc_otp", "verifyProfile 5: "+it.message().toString())
                            _bookingData.postValue(Resource.error(it.errorBody().toString(), null))
                        }
                    }
                } else {
                    _bookingData.postValue(Resource.error("No Internet Connection", null))
                }
            } catch (e: Exception) {
                Log.e("abc_otp", "verifyOTP: ", e)
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
                            Log.e("abc_otp", "verifyProfile 5: "+it.message().toString())
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
                            Log.e("abc_otp", "verifyProfile 5: "+it.message().toString())
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

    fun clear() {
        _bookingData.value = null
        _bookUpdateData.value = null
        _bookLinkData.value = null
    }
}