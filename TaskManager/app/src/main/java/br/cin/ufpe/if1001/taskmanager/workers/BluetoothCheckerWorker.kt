package br.cin.ufpe.if1001.taskmanager.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import br.cin.ufpe.if1001.taskmanager.R
import br.cin.ufpe.if1001.taskmanager.utils.GeneralServices
import br.cin.ufpe.if1001.taskmanager.utils.NotificationUtil

class BluetoothCheckerWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val context = super.getApplicationContext()
        if (GeneralServices.isBluetoothOn()) {
            NotificationUtil.buildAndSendNotification(context,
                context.getString(R.string.bluetooth_off_notification_title),
                context.getString(R.string.bluetooth_off_notification_text), true, 4)
        }

        return Result.success()
    }
}