package com.chillarcards.privatepractice.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillarcards.privatepractice.data.model.DoctorOnboardingResponseClass
import com.chillarcards.privatepractice.data.model.WorkingHours
import com.chillarcards.privatepractice.data.repository.AuthRepository
import com.chillarcards.privatepractice.utills.NetworkHelper
import com.chillarcards.privatepractice.utills.Resource
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

class RegistrationCompletedViewModel(
    private val networkHelper: NetworkHelper,
    val authRepository: AuthRepository
):ViewModel()

{
    private val _registrationComplete=MutableLiveData<Resource<DoctorOnboardingResponseClass>?>()
    val registrationComplete:LiveData<Resource<DoctorOnboardingResponseClass>?> get() = _registrationComplete
    val doctorPhone = MutableLiveData<String>()
    val doctorName = MutableLiveData<String>()
    val departmentId = MutableLiveData<Int>()
    val consultationTime = MutableLiveData<Int>()
    val entityId = MutableLiveData<Int>()
    val doctorId = MutableLiveData<Int>()
    val workingHours = MutableLiveData<List<WorkingHours>>()
    var workingHoursNew = mutableListOf<WorkingHours>()
    val selectedDays = MutableLiveData<MutableList<String>>(mutableListOf())
    val startTime = MutableLiveData<String>()
    val endTime = MutableLiveData<String>()
    val session = MutableLiveData<String>()


    fun selfRegistrationCompleted(
    ){
        viewModelScope.launch(NonCancellable) {
            try {
                _registrationComplete.postValue(Resource.loading(null))
                Log.d("WorkingHours2", "Working hours set in ViewModel: ${workingHoursNew.joinToString()}")

                if (networkHelper.isNetworkConnected()){
                    /*val workingHoursList = workingHoursNew.map {
                        WorkingHours(
                            day = it.day,
                            startTime = it.startTime ?: "",
                            endTime = it.endTime ?: "",
                            session = it.session ?: ""
                        )
                    }*/
                    workingHours.postValue(workingHoursNew)
                  //  workingHours.value = workingHoursList
                    authRepository.getDoctorOnboard(
                    doctorPhone.value ?: "",
                    doctorName.value ?: "",
                    departmentId.value ?: 0,
                    consultationTime.value ?: 0,
                    entityId.value ?: 0,
                    doctorId.value ?: 0,
                        workingHoursNew ?: listOf()
                ).let {
                    if (it.isSuccessful){
                        Log.e("Selfregistrtion","succes:${it.body()}")
                        _registrationComplete.postValue(Resource.success(it.body()))
                    }
                    else{
                        Log.e("Selfregistrtion","failed:${it.errorBody()}")
                        _registrationComplete.postValue(Resource.error(it.errorBody().toString(),null))
                    }
                }

                }
                else{
                    _registrationComplete.postValue(Resource.error("No Internet Connection",null))
                }

            }
            catch (e:Exception){
                _registrationComplete.postValue(Resource.error(e.toString(),null))

            }
        }
    }

}