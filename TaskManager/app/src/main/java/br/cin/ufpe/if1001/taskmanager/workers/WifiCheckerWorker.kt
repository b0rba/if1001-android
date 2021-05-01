package br.cin.ufpe.if1001.taskmanager.workers

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import br.cin.ufpe.if1001.taskmanager.MainActivity
import br.cin.ufpe.if1001.taskmanager.R
import br.cin.ufpe.if1001.taskmanager.utils.GeneralServices
import br.cin.ufpe.if1001.taskmanager.utils.NotificationUtil

class WifiCheckerWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val context = super.getApplicationContext()
        val currSSID = GeneralServices.getCurrSSID(context)

        if (currSSID.equals(inputData.getString(MainActivity.WIFI_OFF_CONFIGURATION_HOME_SSID)))
            return Result.success()

        NotificationUtil.buildAndSendNotification(context,
            context.getString(R.string.wifi_off_notification_title),
            context.getString(R.string.wifi_off_notification_text), true, 1)

        return Result.success()
    }
}