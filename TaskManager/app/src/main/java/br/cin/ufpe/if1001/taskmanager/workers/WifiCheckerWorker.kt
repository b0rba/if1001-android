package br.cin.ufpe.if1001.taskmanager.workers

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import br.cin.ufpe.if1001.taskmanager.MainActivity
import br.cin.ufpe.if1001.taskmanager.R
import br.cin.ufpe.if1001.taskmanager.utils.GeneralServices

class WifiCheckerWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val context = super.getApplicationContext()
        val currSSID = GeneralServices.getCurrSSID(context)

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