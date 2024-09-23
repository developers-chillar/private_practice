package com.chillarcards.privatepractice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillarcards.privatepractice.data.model.DoctorOnboardingResponseClass
import com.chillarcards.privatepractice.data.model.WorkingHours
import com.chillarcards.privatepractice.data.repository.AuthRepository
import com.chillarcards.privatepractice.utills.NetworkHelper
import com.chillarcards.privatepractice.utills.Resource
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
    val selectedDay = MutableLiveData<String>()
    val startTime = MutableLiveData<String>()
    val endTime = MutableLiveData<String>()
    val session = MutableLiveData<String>()
    fun selfRegistrationCompleted(
    ){
        viewModelScope.launch {
            try {
                _registrationComplete.postValue(Resource.loading(null))
                if (networkHelper.isNetworkConnected()){
                    val workingHoursList = listOf(
                        WorkingHours(
                            day = selectedDay.value ?: "",
                            startTime = startTime.value ?: "",
                            endTime = endTime.value ?: "",
                            session = session.value ?: ""
                        )
                    )
                    workingHours.value = workingHoursList
                    authRepository.getDoctorOnboard(
                    doctorPhone.value ?: "",
                    doctorName.value ?: "",
                    departmentId.value ?: 0,
                    consultationTime.value ?: 0,
                    entityId.value ?: 0,
                    doctorId.value ?: 0,
                    workingHours.value ?: listOf()
                ).let {
                    if (it.isSuccessful){
                        _registrationComplete.postValue(Resource.success(it.body()))
                    }
                    else{
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