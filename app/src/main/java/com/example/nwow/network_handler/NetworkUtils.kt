package com.example.nwow.network_handler

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.nwow.utils.NwowApplication

class NetworkUtils {

    companion object {

        fun isInternetAvailable(): Boolean {
            (NwowApplication.getCtx()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
                return this.getNetworkCapabilities(this.activeNetwork)?.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                ) ?: false
            }
        }
    }
}