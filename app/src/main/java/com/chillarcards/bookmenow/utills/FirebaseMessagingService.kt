package com.chillarcards.bookmenow.utills

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.chillarcards.bookmenow.MainActivity
import com.chillarcards.bookmenow.R
import com.chillarcards.bookmenow.ui.notification.NotificationViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

open class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            // Check if data needs to be processed by long running job
            if (needsToBeScheduled()) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob()
            } else {
                // Handle message within 10 seconds
                handleNow()
            }
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            NotificationViewModel().handleFCMMessage(remoteMessage)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
//
//        if (remoteMessage.notification != null) {
//            Log.d("FirebaseMessagingService", "Message Notification Body: ${remoteMessage.notification?.body}")
//            NotificationViewModel().handleFCMMessage(remoteMessage)
//        }
//        Log.d(TAG, "From: ${remoteMessage.from}")
//
//        // Check if the message contains data payload
//        if (remoteMessage.data.isNotEmpty()) {
//            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
//
//            // TODO: Handle data payload (you can send custom data from your server)
//        }
//
//
//        if (remoteMessage.notification != null) {
//            Log.d(TAG, "Message Notification Body: ${remoteMessage.notification?.body}")
//            // Handle the notification content, update UI, or perform any other actions
//        }
////
////        // Handle FCM messages with data payload
////        remoteMessage.data.isNotEmpty().let {
////            // Extract data from the message
////            val message = remoteMessage.data["message"]
////            Log.d("FCM", "Received message: $message")
////
////            // Update your fragment with the new notification data
////            updateFragmentWithNotification(message)
////        }
//    }

    private fun needsToBeScheduled() = true

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
      //  getFCMToken()
        Log.d("FCM", "New Token: $token")
    }
    // [END on_new_token]

    private fun scheduleJob() {
//        // [START dispatch_job]
//        val work = OneTimeWorkRequest.Builder(MyWorker::class.java)
//            .build()
//        WorkManager.getInstance(this)
//            .beginWith(work)
//            .enqueue()
//        // [END dispatch_job]
    }

    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("FCM Message")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }


    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    // Use the token as needed
                    Log.d("FCM", "FCM Token: $token")
                } else {
                    Log.e("FCM", "Failed to get FCM token: ${task.exception}")
                }
            }
    }


    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}