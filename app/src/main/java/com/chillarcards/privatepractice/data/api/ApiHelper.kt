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
        doctor_id: String
    ): Response<WorkResponseModel>
    suspend fun getShareLink():  Response<ShareLinkResponseModel>
    suspend fun getGeneral():  Response<GeneralResponseModel>
    suspend fun getBankDetails():  Response<BankResponseModel>
    suspend fun getShopClose():  Response<StatusResponseModel>
    suspend fun getCategory():  Response<CategoryResponseModel>
    suspend fun getBookigDetails(
        doctorId: String,
        date: String,
        entityId: String
    ): Response<BookingResponseModel>
    suspend fun getReport(
        doctorId: String,
        date: String,
    ): Response<BookingReportResponseModel>

    suspend fun getUpdate(bookingId: String): Response<StatusResponseModel>
    suspend fun getDoctorAvailability(doctorId: String,date:String): Response<AddLeaveResClass>

}