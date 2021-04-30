package br.cin.ufpe.if1001.taskmanager.workers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import br.cin.ufpe.if1001.taskmanager.MainActivity
import br.cin.ufpe.if1001.taskmanager.R
import kotlin.math.log

class WifiCheckerWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val context = super.getApplicationContext()

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled)
            return Result.success()

        val currSSID = wifiManager.connectionInfo.ssid

        if (currSSID.equals(inputData.getString(MainActivity.WIFI_OFF_CONFIGURATION_HOME_SSID)))
            return Result.success()

        val builder = NotificationCompat.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(context.getString(R.string.wifi_off_notification_title))
            .setContentText(context.getString(R.string.wifi_off_notification_text))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(context.getString(R.string.wifi_off_notification_text))
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(4, builder.build())
        }

        return Result.success()
    }
}