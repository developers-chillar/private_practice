package com.chillarcards.privatepractice.data.model

data class SelfOnBoardingAuthRegisterResClass(
    val statusCode: Int?,
    val message: String?,
    val data: PhoneNumberData
)
data class PhoneNumberData(
    val entityId: Int?,
    val doctorId: Int?,
    val doctorPhone: String?
)
