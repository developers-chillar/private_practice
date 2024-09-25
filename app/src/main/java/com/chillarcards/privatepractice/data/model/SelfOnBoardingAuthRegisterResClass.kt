package com.chillarcards.privatepractice.data.model

data class SelfOnBoardingAuthRegisterResClass(
    val statusCode: Int?,
    val message: String?,
    val data: PhoneNumberData
)
data class PhoneNumberData(
    val entity_id: Int?,
    val doctor_id: Int?,
    val profile_completed: String?
)
