package com.chillarcards.privatepractice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsBroadcastReceiver : BroadcastReceiver() {

    var otpListener: OTPListener? = null

    fun injectOTPListener(listener: OTPListener) {
        otpListener = listener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status

            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    // Get the SMS message
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as? String
                    message?.let {
                        otpListener?.onOTPReceived(it)
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    // Handle timeout
                    otpListener?.onOTPTimeOut()
                }
            }
        }
    }
}

interface OTPListener {
    fun onOTPReceived(otp: String)
    fun onOTPTimeOut()
}
