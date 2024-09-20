package com.chillarcards.privatepractice.data.model

data class DrSpecilaityResClass(
    val statusCode: Int,
    val message: String,
    val data: List<Department>
)
data class Department(
    val department_id: Int,
    val department_name: String,
    val status: Int
)
