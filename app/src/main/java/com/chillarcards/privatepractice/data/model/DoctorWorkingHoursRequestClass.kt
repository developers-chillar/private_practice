package com.chillarcards.privatepractice.data.model

data class DoctorWorkingHoursRequestClass(
    val doctor_phone: String,
    val doctor_name: String,
    val department_id: Int,
    val consultation_time: Int,
    val entity_id: Int,
    val doctor_id: Int,
    val workingHours: List<WorkingHours>
)

data class WorkingHours(
    val day: String,
    val startTime: String,
    val endTime: String,
    val session: String
)
