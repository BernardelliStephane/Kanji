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
 * To use this object, make sure to call [setAppContext] with the application context before checking the network availability.
 *
 * Example:
 * ```
 * ConnectivityChecker.setAppContext(applicationContext)
 * val isNetworkAvailable = ConnectivityChecker.isNetworkAvailable()
 * ```
 */
object ConnectivityChecker {

    private lateinit var appContext: Context

    /**
     * Checks if there is an active network connection and if the device can connect to the internet.
     *
     * @return `true` if the device has an active Wi-Fi, cellular, or Ethernet connection.
     */
    fun isNetworkAvailable(): Boolean {
        if (!ConnectivityChecker::appContext.isInitialized)
            throw IllegalStateException("Application context not set. Call ConnectivityChecker.setAppContext() first.")

        try {
            val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    /**
     * Sets the application context. This method should be called in the application's initialization phase.
     *
     * @param context The application context.
     */
    fun setAppContext(context: Context) {
        appContext = context
    }
}