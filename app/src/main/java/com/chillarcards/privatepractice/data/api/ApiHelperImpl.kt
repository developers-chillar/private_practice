package com.chillarcards.privatepractice.data.api

import com.chillarcards.privatepractice.data.model.*
import retrofit2.Response

/**
 * @Author: Sherin Jaison
 * @Date: 01-11-2023
 * Chillar
 */
class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {
    override suspend fun verifyMobile(
        phone: String
    ): Response<RegisterModel> = apiService.verifyMobile(
        RegisterRequestModel(phone)
    )

    override suspend fun userCheck(phone: String): Response<UserCheckResClass> = apiService.userCheck(UserCheckReqClass(phone))



//    override suspend fun getProfile(phone: String):
//            Response<ProfileResponseModel> {
//        return apiService.getProfile(phone)
//    }

    override suspend fun getProfile(
        phone: String
    ): Response<ProfileResponseModel> = apiService.getProfile(
        RegisterRequestModel(phone)
    )
    override suspend fun getWork(
        doctor_id: String
    ): Response<WorkResponseModel> = apiService.getWork(
        WorkRequestModel(doctor_id)
    )
    override suspend fun getShareLink(): Response<ShareLinkResponseModel> =
        apiService.getShareLink()
    override suspend fun getGeneral(): Response<GeneralResponseModel> =
        apiService.getGeneral()
    override suspend fun getBankDetails(): Response<BankResponseModel> =
        apiService.getBankDetails()
   override suspend fun getShopClose(): Response<StatusResponseModel> =
        apiService.getShopClose()
   override suspend fun getCategory(): Response<CategoryResponseModel> =
        apiService.getCategory()

    override suspend fun getBookigDetails(
        doctorId: String,
        date: String,
        entityId: String
    ): Response<BookingResponseModel> = apiService.getBookigDetails(
        BookingRequestModel(doctorId,date,entityId)
    )
    override suspend fun getReport(
        doctorId: String,
        date: String
    ): Response<BookingReportResponseModel> = apiService.getReport(
        BookingReportModel(doctorId,date)
    )
    override suspend fun getUpdate(bookingId: String): Response<StatusResponseModel> = apiService.getUpdate(BookUpdateRequestModel(bookingId))

    override suspend fun getDoctorAvailability(doctorId: String, date: String):Response<AddLeaveResClass> =
        apiService.getDoctorAvailability(AddLeaveReqClass(doctorId,date))

    override suspend fun registrationAuthorization(phone: String): Response<SelfOnBoardingAuthRegisterResClass> =
        apiService.registrationAuthorization(SelfOnBoardingAuthRegisterReqClass(phone))

    override suspend fun getDrSpecialities(): Response<DrSpecilaityResClass> =apiService.getDrSpecialities()
    override suspend fun getDoctorOnboard(doctor_phone:String, doctor_name:String, department_id:Int, consultation_time:Int, entity_id:Int, doctor_id:Int, workingHours:List<WorkingHours>)
    : Response<DoctorOnboardingResponseClass> =apiService.getDoctorOnboard(DoctorWorkingHoursRequestClass(doctor_phone,doctor_name,department_id,consultation_time,entity_id,doctor_id,workingHours))


}