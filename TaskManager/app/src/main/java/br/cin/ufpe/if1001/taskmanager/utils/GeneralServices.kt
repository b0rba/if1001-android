package br.cin.ufpe.if1001.taskmanager.utils

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.util.Log

class GeneralServices {
    companion object {
        fun isBluetoothOn(): Boolean = BluetoothAdapter.getDefaultAdapter().run { isEnabled }

        fun isGPSOn(context: Context): Boolean =
            (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                .run { isProviderEnabled(LocationManager.GPS_PROVIDER) }

        fun isMobileDataOn(context: Context): Boolean =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
                val method = Class.forName(this.javaClass.name).getDeclaredMethod("getMobileDataEnabled")
                method.isAccessible = true
                method.invoke(this) as Boolean
            }

        fun isWifiOn(context: Context): Boolean =
            (context.getSystemService(Context.WIFI_SERVICE) as WifiManager).run { isWifiEnabled }

        fun getCurrSSID(context: Context): String =
            (context.getSystemService(Context.WIFI_SERVICE) as WifiManager)
                .run {
                    var ssid = connectionInfo.ssid
                    if (!ssid.equals(WifiManager.UNKNOWN_SSID)) ssid = ssid.substring(1, ssid.length-1)
                    ssid
                }
    }
}