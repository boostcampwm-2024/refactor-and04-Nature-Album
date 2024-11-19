package com.and04.naturealbum.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.and04.naturealbum.NatureAlbum

object NetworkState {
    private val connectivityManager =
        NatureAlbum.getInstance().getSystemService(ConnectivityManager::class.java)
    private val currentNetwork = connectivityManager.activeNetwork

    fun isActiveNetwork(): Boolean {
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork) ?: return false

        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}
