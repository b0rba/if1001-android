package br.cin.ufpe.if1001.taskmanager


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import br.cin.ufpe.if1001.taskmanager.databinding.ActivityMainBinding
import br.cin.ufpe.if1001.taskmanager.workers.WifiCheckerWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object {
        const val SHARED_PREFERENCE = "br.cin.ufpe.if1001.taskmanager.MainActivity"
        const val MOBILE_DATA_OFF_BUTTON_STATE = "mobile_data_off_wifi_button_state"
        const val MOBILE_DATA_OFF_WORKER_TAG = "mobile_data_off_wifi_worker_tag"
        const val NOTIFICATION_CHANNEL_ID = "6969"
        const val NOTIFICATION_CHANNEL_NAME = "Main Notification Channel"
    }

    private val workManager = WorkManager.getInstance(this)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE)

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        // Mobile Data Off Wifi
        val mobileDataOffCard = binding.mobileDataOffWifi
        mobileDataOffCard.cardText.text = getString(R.string.mobile_data_off_card_text)
        mobileDataOffCard.buttonToggle.isChecked =
            sharedPreferences.getBoolean(MOBILE_DATA_OFF_BUTTON_STATE, false)

        mobileDataOffCard.buttonToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val workRequest = PeriodicWorkRequest
                    .Builder(WifiCheckerWorker::class.java, 1, TimeUnit.HOURS)
                    .addTag(MOBILE_DATA_OFF_WORKER_TAG)
                    .build()

                workManager.enqueue(workRequest)
                val shEditor = sharedPreferences.edit()
                shEditor.putString(MOBILE_DATA_OFF_WORKER_TAG, MOBILE_DATA_OFF_WORKER_TAG)
                shEditor.putBoolean(MOBILE_DATA_OFF_BUTTON_STATE, true)
                shEditor.commit()

            } else {
                val mobileDataOffWorkerTag =
                    sharedPreferences.getString(MOBILE_DATA_OFF_WORKER_TAG, null)
                mobileDataOffWorkerTag?.let { workManager.cancelAllWorkByTag(it) }

                val shEditor = sharedPreferences.edit()
                shEditor.putBoolean(MOBILE_DATA_OFF_BUTTON_STATE, false)
                shEditor.commit()
            }
        }

    }
}