package com.chillarcards.privatepractice

import android.app.Application
import android.os.Bundle
import com.chillarcards.privatepractice.di.module.appModule
import com.chillarcards.privatepractice.di.module.repoModule
import com.chillarcards.privatepractice.di.module.viewModelModule
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * @Author: Sherin Jaison
 * @Date: 01-11-2023$
 * Chillar
 * for kotlin koin di
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(appModule, repoModule, viewModelModule))
        }
        FirebaseApp.initializeApp(this)
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Example Item")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text")
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        // Generate Hash Key >>>>> GOOGLE SMS
        //  val appSignatureHashHelper = AppSignatureHashHelper(this)
        //  Log.e(TAG, "HashKey: " + appSignatureHashHelper.appSignatures[0])
        //  Last uploaded VrgIsm6rcVw
    }


}