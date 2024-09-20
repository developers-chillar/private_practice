package com.chillarcards.privatepractice.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillarcards.privatepractice.data.model.DrSpecilaityResClass
import com.chillarcards.privatepractice.data.repository.AuthRepository
import com.chillarcards.privatepractice.utills.NetworkHelper
import com.chillarcards.privatepractice.utills.Resource
import kotlinx.coroutines.launch

class DrSpecialityViewModel(
    private val networkHelper: NetworkHelper,
    val authRepository: AuthRepository):ViewModel()
{
        private val _drSpecilaities=MutableLiveData<Resource<DrSpecilaityResClass>?>()
    val drSpecilaities:LiveData<Resource<DrSpecilaityResClass>?> get() = _drSpecilaities

     fun getDrSpecialized(){
        viewModelScope.launch {
            try {
                if (networkHelper.isNetworkConnected()){authRepository.getDrSpecialities().let {
                    if (it.isSuccessful){
                        _drSpecilaities.postValue(Resource.success(it.body()))
                        Log.d("drSpeciality1",it.body().toString())
                    }
                    else{
                        _drSpecilaities.postValue(Resource.error(it.errorBody().toString(),null))
                        Log.d("drSpeciality2",it.errorBody().toString())
                    }

                }}
                else{
                    _drSpecilaities.postValue(Resource.error("No Internet Connection",null))
                }

            }
            catch (e:Exception){
                Log.d("drSpeciality3",e.message.toString())
                _drSpecilaities.postValue(Resource.error(e.message.toString(),null))
            }
        }

    }
}