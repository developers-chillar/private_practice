package com.chillarcards.privatepractice.data.api

import com.chillarcards.privatepractice.data.model.*
import retrofit2.Response

/**
 * @Author: Sherin Jaison
 * @Date: 01-11-2023
 * Chillar
 */
interface ApiHelper {

    suspend fun verifyMobile(
        phone: String
    ): Response<RegisterModel>

    suspend fun userCheck(phone: String):Response<UserCheckResClass>

    suspend fun getProfile(
        phone: String
    ): Response<ProfileResponseModel>
    suspend fun getWork(
        doctor_id: String,entity_id: String
    ): Response<WorkResponseModel>
    suspend fun getShareLink():  Response<ShareLinkResponseModel>
    suspend fun getGeneral():  Response<GeneralResponseModel>
    suspend fun getBankDetails():  Response<BankResponseModel>
    suspend fun getShopClose():  Response<StatusResponseModel>
    suspend fun getCategory():  Response<CategoryResponseModel>
    suspend fun getBookigDetails(doctor_id: String, date: String, entity_id: String): Response<BookingResponseModel>
    suspend fun getReport(doctorId: String, date: String, ): Response<BookingReportResponseModel>

    suspend fun getUpdate(bookingId: String): Response<StatusResponseModel>
    suspend fun getDoctorAvailability(doctorId: String,date:String): Response<AddLeaveResClass>
    suspend fun registrationAuthorization(phone:String):Response<SelfOnBoardingAuthRegisterResClass>
    suspend fun getDrSpecialities():Response<DrSpecilaityResClass>
    suspend fun getDoctorOnboard(doctor_phone:String, doctor_name:String, department_id:Int, consultation_time:Int, entity_id:Int, doctor_id:Int, workingHours:List<WorkingHours>):
            Response<DoctorOnboardingResponseClass>

}