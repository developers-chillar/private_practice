package com.chillarcards.privatepractice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chillarcards.privatepractice.data.model.DoctorOnboardingResponseClass
import com.chillarcards.privatepractice.data.repository.AuthRepository
import com.chillarcards.privatepractice.utills.NetworkHelper
import com.chillarcards.privatepractice.utills.Resource

class RegistrationCompletedViewModel(
    private val networkHelper: NetworkHelper,
    val authRepository: AuthRepository
):ViewModel()

{
    private val _registrationComplete=MutableLiveData<Resource<DoctorOnboardingResponseClass>?>()
    val registrationComplete:LiveData<Resource<DoctorOnboardingResponseClass>?> get() = _registrationComplete
}