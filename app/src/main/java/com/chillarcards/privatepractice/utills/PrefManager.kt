package com.chillarcards.privatepractice.utills

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.chillarcards.privatepractice.utills.PrefManager.Companion.DRNAME

class PrefManager(_context: Context) {

    companion object {
        // Shared preferences file name
        private const val PREF_NAME = "Servicexpert"

        private const val IS_LOGGED_IN = "IS_LOGGED_IN"
        private const val REFRESH = "REFRESH"
        private const val REFRESHTOKEN = "REFRESHTOKEN"
        private const val TOKEN = "Token"
        private const val STATUS = "STATUS"
        private const val MOBILENO = "MOBILENO"
        private const val DOCTORID = "DOCTORID"
        private const val entityId = "EntityId"
        private const val DEPTID = "DEPTID"
        private const val DRNAME = "DRNAME"
        private const val PHONENUMBER = "PHONENUMBER"
        private const val CONSULATATIONDURATION = "CONSULATATIONDURATION"
        private const val ENTITYID = "ENTITYID"
        private const val VERIFICATIONID = "VERIFICATIONID"

        // shared pref mode
        private const val PRIVATE_MODE = Context.MODE_PRIVATE
    }

    private val pref: SharedPreferences = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    private val editor: SharedPreferences.Editor = pref.edit()

    fun isLoggedIn(): Boolean {
        return pref.getBoolean(IS_LOGGED_IN, false)
    }

    fun setIsLoggedIn(value: Boolean) {
        editor.putBoolean(IS_LOGGED_IN, value)
        editor.commit()
    }

    fun getToken(): String {
//        val hardcodedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwaG9uZSI6IlUyRnNkR1ZrWDE4MnRpUk0vdVVnWDd6WVhhVk44Mms5NjlCNUhmUmN2YVk9IiwiaWF0IjoxNzI4MzA5MDg2LCJleHAiOjE3MjgzMTI2ODZ9.giVV1Hygg7JYJqtLuzHFVGPYHphFiApVJKupu8Nnf0I" // Replace with your actual token value
//        Log.e("PrefManager1", "token: $hardcodedToken")
//        return hardcodedToken
        Log.e("PrefManager1", "token:${pref.getString(TOKEN, "") ?: ""} ")
        return pref.getString(TOKEN, "") ?: ""

    }
//    fun setToken(token: String) {
//        editor.putString(TOKEN, token)
//        editor.commit()
//    }
fun setToken(token: String?) {
    if (token != null) {
        editor.putString(TOKEN, token)
        editor.apply()
        Log.e("PrefManager", "token: $token")// Use apply() for asynchronous saving
    } else {
      //  Log.e("PrefManager", "Attempted to save a null token.")
        // Handle the case where token is null, if needed
    }
}
    fun getRefToken(): String {
        return pref.getString(REFRESHTOKEN, "") ?: ""
    }
    fun setRefToken(token: String) {
        editor.putString(REFRESHTOKEN, token)
        editor.commit()
    }
    fun getRefresh(): String {
        return pref.getString(REFRESH, "") ?: ""
    }
    fun setRefresh(token: String) {
        editor.putString(REFRESH, token)
        editor.commit()
    }
    fun getMobileNo(): String {
        return pref.getString(MOBILENO, "") ?: ""
    }
    fun setMobileNo(value: String) {
        editor.putString(MOBILENO, value)
        editor.commit()
    }
    fun getDoctorId(): String {
       // return pref.getInt(DOCTORID, 0)
        return pref.getString(DOCTORID, "0") ?: "0"
    }
    fun setDoctorId(value: String) {
        editor.putString(DOCTORID, value)
        editor.commit()
    }
    fun getEntityId(): String {
        return pref.getString(entityId, "0") ?: "0"
    }
    fun setEntityId(value: String) {
        editor.putString(entityId, value)
        editor.commit()
    }
    fun getStatus(): Int {
        return pref.getInt(STATUS, 0)
    }
    fun setStatus(value: Int) {
        editor.putInt(STATUS, value)
        editor.commit()
    }


    fun getDeptId(): Int {
        return pref.getInt(DEPTID,0)
    }
    fun setDeptId(value: Int) {
        editor.putInt(DEPTID, value)
        editor.commit()
    }


    fun getDrName(): String {
        return pref.getString(DRNAME,"") ?: ""
    }
    fun setDrName(value: String) {
        editor.putString(DRNAME, value)
        editor.commit()
    }

    fun getPhoneNumber(): String? {
        return pref.getString(PHONENUMBER, null)
    }

    fun setPhoneNumber(value: String) {
        editor.putString(PHONENUMBER, value)
        editor.commit()

    }



    fun getConsultationDuration(): Int {
        return pref.getInt(CONSULATATIONDURATION,0)
    }
    fun setConsultationDuration(value: Int) {
        editor.putInt(CONSULATATIONDURATION, value)
        editor.commit()
    }

    fun getIntEntityId(): Int {
        return pref.getInt(ENTITYID, 0)
    }
    fun setIntEntityId(value: Int) {
        editor.putInt(ENTITYID, value)
        editor.commit()
    }

    fun getVerificatiobID(): String {
        return pref.getString(VERIFICATIONID,"") ?: ""
    }
    fun SetVerificatiobID(value: String) {
        editor.putString(VERIFICATIONID, value)
        editor.commit()
    }


    fun clearAll() {
        editor.clear()
        editor.commit()
        editor.apply()
    }

    fun clearField(keyName: String) {
        editor.remove(keyName).apply()
    }
}
