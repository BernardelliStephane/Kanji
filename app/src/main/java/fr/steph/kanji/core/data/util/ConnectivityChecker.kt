package fr.steph.kanji.core.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * A utility object that provides functionality to check network availability.
 *
 * This object uses the system's `ConnectivityManager` to determine whether the device has an active
 * network connection and if it can connect to the internet via Wi-Fi, cellular, or Ethernet.
 *
 * Example:
 * ```
 * val isNetworkAvailable = ConnectivityChecker.isNetworkAvailable()
 * ```
 */
object ConnectivityChecker {

    /**
     * Checks if there is an active network connection and if the device can connect to the internet.
     *
     * @return `true` if the device has an active Wi-Fi, cellular, or Ethernet connection.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } catch (e: Exception) {
            return false
        }
    }
}