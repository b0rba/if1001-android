package br.cin.ufpe.if1001.taskmanager.workers

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import br.cin.ufpe.if1001.taskmanager.activities.MainActivity
import br.cin.ufpe.if1001.taskmanager.R
import br.cin.ufpe.if1001.taskmanager.utils.GeneralServices
import br.cin.ufpe.if1001.taskmanager.utils.NotificationUtil

class WifiCheckerWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val context = super.getApplicationContext()
        val currSSID = GeneralServices.getCurrSSID(context)
        if (!GeneralServices.isGPSOn(context))
            return Result.success()

        if (currSSID == inputData.getString(MainActivity.WIFI_OFF_CONFIGURATION_HOME_SSID)
            || currSSID != WifiManager.UNKNOWN_SSID)
            return Result.success()

        NotificationUtil.buildAndSendNotification(context,
            context.getString(R.string.wifi_off_notification_title),
            context.getString(R.string.wifi_off_notification_text), true, 1)

        return Result.success()
    }
}