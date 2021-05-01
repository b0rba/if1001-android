package br.cin.ufpe.if1001.taskmanager.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.cin.ufpe.if1001.taskmanager.R

class NotificationUtil {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "6969"
        private const val NOTIFICATION_CHANNEL_NAME = "Main Notification Channel"

        fun buildAndSendNotification(context: Context, title: String, text: String,
                                     autoCancelable: Boolean, notificationId: Int) {
            val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_outline_app_settings_alt_24)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(autoCancelable)

            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        }

        fun createNotificationChannel(context: Context) {
            NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                    (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                        .createNotificationChannel(this)
                }
        }
    }
}