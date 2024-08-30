package com.chillarcards.privatepractice

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.chillarcards.privatepractice.databinding.ActivityNetworkErrorBinding
import com.chillarcards.privatepractice.utills.ConnectivityReceiver
import com.chillarcards.privatepractice.utills.NetworkHelper

/**
 * Created by Sherin on 02-11-2023.
 */
class NetworkErrorActivity : AppCompatActivity(),
    ConnectivityReceiver.ConnectivityReceiverListener {

    lateinit var binding: ActivityNetworkErrorBinding

    companion object {
        var active = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        active = true
        binding = ActivityNetworkErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )

        binding.retryButton.setOnClickListener {
            val networkHelper = NetworkHelper(this)
            if (networkHelper.isNetworkConnected())
                finish()
        }
    }

    override fun onResume() {
        super.onResume()
        active = true
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onPause() {
        super.onPause()
        active = false
      //  unregisterReceiver(ConnectivityReceiver())
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        active = false
//
//    }


    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected)
            finish()
    }


}