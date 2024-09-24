package com.chillarcards.privatepractice.data.api

import com.chillarcards.privatepractice.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * @Author: Sherin Jaison
 * @Date: 02-11-2023$
 * Chillar
 */

interface ApiService {

    @POST("auth/register")
    suspend fun verifyMobile(
        @Body reqModel: RegisterRequestModel
    ): Response<RegisterModel>

    @POST("auth/user-check ")
    suspend fun userCheck(@Body reqModel: UserCheckReqClass): Response<UserCheckResClass>

//    @Headers("Content-Type: application/json;charset=UTF-8") //no need
//    @POST("getProfile")
//    suspend fun getProfile(
//        @Query("phone") phone: String
//    ): Response<ProfileResponseModel>


    @POST("auth/getProfile")
    suspend fun getProfile(
        @Body reqModel: RegisterRequestModel
    ): Response<ProfileResponseModel>

    @POST("work/get-work-schedule")
    suspend fun getWork(
        @Body reqModel: WorkRequestModel
    ): Response<WorkResponseModel>

    @POST("booking/listBooking")
    suspend fun getBookigDetails(
        @Body reqModel: BookingRequestModel
    ): Response<BookingResponseModel>

    @POST("booking/bookingReport")
    suspend fun getReport(
        @Body reqModel: BookingReportModel
    ): Response<BookingReportResponseModel>

    @POST("booking/updateBooking")
    suspend fun getUpdate(
        @Body reqModel: BookUpdateRequestModel
    ): Response<StatusResponseModel>

    @POST("booking/get-booking-link")
    suspend fun getShareLink(): Response<ShareLinkResponseModel>

    @GET("auth/generalSettings")
    suspend fun getGeneral(): Response<GeneralResponseModel>

    @POST("auth/bankdata")
    suspend fun getBankDetails(): Response<BankResponseModel>

    @POST("auth/update-status")
    suspend fun getShopClose(): Response<StatusResponseModel>

    @POST("app/list-category")
    suspend fun getCategory(): Response<CategoryResponseModel>

    @POST("work/doc-availability")
    suspend fun getDoctorAvailability(@Body reqModel: AddLeaveReqClass): Response<AddLeaveResClass>

@POST("auth/phone-register")
suspend fun registrationAuthorization(@Body reqModel:SelfOnBoardingAuthRegisterReqClass):Response<SelfOnBoardingAuthRegisterResClass>

@POST("auth/list-specialities")
suspend fun getDrSpecialities():Response<DrSpecilaityResClass>

@POST("auth/onboard-doctor")
suspend fun getDoctorOnboard(@Body reqModel:DoctorWorkingHoursRequestClass):Response<DoctorOnboardingResponseClass>
}