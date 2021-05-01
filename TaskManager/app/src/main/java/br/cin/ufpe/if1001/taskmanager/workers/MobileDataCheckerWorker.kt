package br.cin.ufpe.if1001.taskmanager.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import br.cin.ufpe.if1001.taskmanager.R
import br.cin.ufpe.if1001.taskmanager.utils.GeneralServices
import br.cin.ufpe.if1001.taskmanager.utils.NotificationUtil

class MobileDataCheckerWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {
    override fun doWork(): Result {
        val context = super.getApplicationContext()

        if (!GeneralServices.isWifiOn(context))
            return Result.success()

        if (!GeneralServices.isMobileDataOn(context))
            return Result.success()

        NotificationUtil.buildAndSendNotification(context,
            context.getString(R.string.mobile_data_off_notification_title),
            context.getString(R.string.mobile_data_off_notification_text), true, 2)

        return Result.success()
    }
}