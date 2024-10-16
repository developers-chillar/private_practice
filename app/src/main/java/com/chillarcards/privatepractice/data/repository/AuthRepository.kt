package com.chillarcards.privatepractice.data.repository

import com.chillarcards.privatepractice.data.api.ApiHelper
import com.chillarcards.privatepractice.data.model.WorkingHours

/**
 * @Author: Sherin Jaison
 * @Date: 01-11-2023
 * Chillar
 */
class AuthRepository(private val apiHelper: ApiHelper) {
    suspend fun verifyMobile(mobileNumber: String) = apiHelper.verifyMobile(mobileNumber)
    suspend fun userCheck(phone:String)=apiHelper.userCheck(phone)
   suspend fun getProfile(mobileNumber: String) =
        apiHelper.getProfile(mobileNumber)
   suspend fun getWork(doctorId: String,entity_id: String) =
        apiHelper.getWork(doctorId,entity_id)
   suspend fun getShareLink() =
        apiHelper.getShareLink()
    suspend fun getGeneral() =
        apiHelper.getGeneral()
   suspend fun getBankDetails() =
        apiHelper.getBankDetails()
    suspend fun getShopClose() =
        apiHelper.getShopClose()

    suspend fun getCategory() =
        apiHelper.getCategory()

    suspend fun getBookigDetails(doctor_id: String,date: String,entity_id: String) =
        apiHelper.getBookigDetails(doctor_id,date,entity_id)

    suspend fun getReport(doctorId: String,date: String) =
        apiHelper.getReport(doctorId,date)
    suspend fun getUpdate(bookingId: String) =
        apiHelper.getUpdate(bookingId)
    suspend fun getDoctorAvailability(doctorId: String,date: String) =apiHelper.getDoctorAvailability(doctorId, date)

    suspend fun getRegistrationAuthorization(phone: String) =apiHelper.registrationAuthorization(phone)
    suspend fun getDrSpecialities()=apiHelper.getDrSpecialities()
    suspend fun getDoctorOnboard(doctor_phone:String, doctor_name:String, department_id:Int, consultation_time:Int, entity_id:Int, doctor_id:Int, workingHours:List<WorkingHours>)=
        apiHelper.getDoctorOnboard( doctor_phone, doctor_name, department_id, consultation_time, entity_id, doctor_id, workingHours)
}