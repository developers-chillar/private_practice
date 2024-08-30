package com.chillarcards.privatepractice.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillarcards.privatepractice.data.model.UserCheckResClass
import com.chillarcards.privatepractice.data.repository.AuthRepository
import com.chillarcards.privatepractice.utills.NetworkHelper
import com.chillarcards.privatepractice.utills.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MobileScreenViewModel(private val authRepository: AuthRepository,
                            private val networkHelper: NetworkHelper
):ViewModel()
{
    private val _userCheck= MutableLiveData<Resource<UserCheckResClass>?>()
    val userCheck: LiveData<Resource<UserCheckResClass>?> get()=_userCheck
    var phone = MutableLiveData<String>()
     fun userCheck(){
       viewModelScope.launch {
           try {
               _userCheck.postValue(Resource.loading(null))
               if (networkHelper.isNetworkConnected()){
                   authRepository.userCheck(phone.value.toString()).let {
                       if (it.isSuccessful){
                           _userCheck.postValue(Resource.success(it.body()))
                       }
                       else{
                           _userCheck.postValue(Resource.error(it.errorBody().toString(), null))
                       }
                   }
               }
               else{
                   _userCheck.postValue(Resource.error("No Internet Connection", null))
               }
           }
           catch (e:Exception){
               Log.e("abc_otp", "verifyOTP: ", e)
           }
       }
    }

    fun clear(){
        _userCheck.value=null
    }
}