package com.chillarcards.privatepractice.data.model

data class UserCheckResClass(
    val statusCode: Int,
    val message: String,
    val data: PhoneData?
)
data class PhoneData(
    val phone: String
)
