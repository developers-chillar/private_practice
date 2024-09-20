package com.chillarcards.privatepractice.data.model

data class DoctorOnboardingResponseClass(
    val statusCode: Int,
    val message: String,
    val data: DoctorData
)
data class DoctorData(
    val entity_id: Int,
    val doctor_id: Int,
    val phone: String,
    val access_token: String,
    val refresh_token: String
)
