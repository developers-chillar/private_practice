package com.chillarcards.privatepractice.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillarcards.privatepractice.data.model.BankResponseModel
import com.chillarcards.privatepractice.data.repository.AuthRepository
import com.chillarcards.privatepractice.utills.NetworkHelper
import com.chillarcards.privatepractice.utills.Resource
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

class BankViewModel(
    private val authRepository: AuthRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _bankData = MutableLiveData<Resource<BankResponseModel>?>()
    val bankData: MutableLiveData<Resource<BankResponseModel>?> get() = _bankData

    fun getBankDetails() {
        viewModelScope.launch(NonCancellable) {
            try {
                _bankData.postValue(Resource.loading(null))
                if (networkHelper.isNetworkConnected()) {
                    authRepository.getBankDetails().let {
                        if (it.isSuccessful) {
                            _bankData.postValue(Resource.success(it.body()))
                        } else {
                            _bankData.postValue(Resource.error(it.errorBody().toString(), null))
                        }
                    }
                } else {
                    _bankData.postValue(Resource.error("No Internet Connection", null))
                }
            } catch (e: Exception) {
                Log.e("abc_otp", "verifyOTP: ", e)
            }
        }
    }


    fun clear() {
        _bankData.value = null
    }
}
