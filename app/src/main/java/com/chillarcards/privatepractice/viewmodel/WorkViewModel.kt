package com.chillarcards.privatepractice.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillarcards.privatepractice.data.model.WorkResponseModel
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
class WorkViewModel(
    private val authRepository: AuthRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val _workData = MutableLiveData<Resource<WorkResponseModel>?>()
    val workData: LiveData<Resource<WorkResponseModel>?> get() = _workData

    var doctorID = MutableLiveData<String>()

    fun getWork() {
        viewModelScope.launch(NonCancellable) {
            try {
                _workData.postValue(Resource.loading(null))
                if (networkHelper.isNetworkConnected()) {
                    authRepository.getWork(
                        doctorID.value.toString()
                    ).let {
                        if (it.isSuccessful) {
                            _workData.postValue(Resource.success(it.body()))
                        } else {
                            _workData.postValue(Resource.error(it.errorBody().toString(), null))
                        }
                    }
                } else {
                    _workData.postValue(Resource.error("No Internet Connection", null))
                }
            } catch (e: Exception) {
                Log.e("abc_otp", "verifyOTP: ", e)
            }
        }
    }

    fun clear() {
        _workData.value = null
    }
}