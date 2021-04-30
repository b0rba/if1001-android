package br.cin.ufpe.if1001.taskmanager.workers

import android.content.Context
import android.net.wifi.WifiManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import br.cin.ufpe.if1001.taskmanager.MainActivity
import br.cin.ufpe.if1001.taskmanager.R

class WifiCheckerWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val context = super.getApplicationContext()

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (!wifiManager.isWifiEnabled)
            return Result.success()

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        val cmClass = Class.forName(connectivityManager.javaClass.name)
        val method = cmClass.getDeclaredMethod("getMobileDataEnabled")
        method.isAccessible = true
        val mobileDataEnabled = method.invoke(connectivityManager) as Boolean
        if (!mobileDataEnabled)
            return Result.success()

        val builder = NotificationCompat.Builder(context, MainActivity.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(context.getString(R.string.mobile_data_off_notification_title))
            .setContentText(context.getString(R.string.mobile_data_off_notification_text))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(context.getString(R.string.mobile_data_off_notification_text))
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build())
        }

        return Result.success()
    }
}