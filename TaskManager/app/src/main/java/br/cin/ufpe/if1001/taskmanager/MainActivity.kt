package br.cin.ufpe.if1001.taskmanager


import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import br.cin.ufpe.if1001.taskmanager.databinding.ActivityMainBinding
import br.cin.ufpe.if1001.taskmanager.workers.BluetoothCheckerWorker
import br.cin.ufpe.if1001.taskmanager.workers.GPSCheckerWorker
import br.cin.ufpe.if1001.taskmanager.workers.MobileDataCheckerWorker
import br.cin.ufpe.if1001.taskmanager.workers.WifiCheckerWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object {
        const val SHARED_PREFERENCE = "br.cin.ufpe.if1001.taskmanager.MainActivity"

        const val MOBILE_DATA_OFF_BUTTON_STATE = "mobile_data_off_wifi_button_state"
        const val MOBILE_DATA_OFF_WORKER_TAG = "mobile_data_off_wifi_worker_tag"

        const val GPS_OFF_BUTTON_STATE = "gps_off_button_state"
        const val GPS_OFF_WORKER_TAG = "gps_off_worker_tag"

        const val BLUETOOTH_OFF_BUTTON_STATE = "bluetooth_off_button_state"
        const val BLUETOOTH_OFF_WORKER_TAG = "bluetooth_off_worker_tag"

        const val WIFI_OFF_BUTTON_STATE = "wifi_off_button_state"
        const val WIFI_OFF_CONFIGURATION_HOME_SSID = "wifi_off_home_ssid_configured"
        const val WIFI_OFF_WORKER_TAG = "wifi_off_worker_tag"

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
            val shEditor = sharedPreferences.edit()
            if (isChecked) {
                val workRequest = PeriodicWorkRequest
                    .Builder(MobileDataCheckerWorker::class.java, 1, TimeUnit.HOURS)
                    .addTag(MOBILE_DATA_OFF_WORKER_TAG)
                    .build()

                workManager.enqueue(workRequest)

                shEditor.putString(MOBILE_DATA_OFF_WORKER_TAG, MOBILE_DATA_OFF_WORKER_TAG)
                shEditor.putBoolean(MOBILE_DATA_OFF_BUTTON_STATE, true)
            } else {
                val mobileDataOffWorkerTag =
                    sharedPreferences.getString(MOBILE_DATA_OFF_WORKER_TAG, null)
                mobileDataOffWorkerTag?.let { workManager.cancelAllWorkByTag(it) }

                shEditor.remove(MOBILE_DATA_OFF_WORKER_TAG)
                shEditor.putBoolean(MOBILE_DATA_OFF_BUTTON_STATE, false)
            }
            shEditor.commit()
        }
        // Gps Off
        val gpsOffCard = binding.gpsOff
        gpsOffCard.cardText.text = getString(R.string.gps_off_card_text)
        gpsOffCard.buttonToggle.isChecked =
            sharedPreferences.getBoolean(GPS_OFF_BUTTON_STATE, false)

        gpsOffCard.buttonToggle.setOnCheckedChangeListener { _, isChecked ->
            val shEditor = sharedPreferences.edit()
            if (isChecked) {
                val workerRequest = PeriodicWorkRequest
                    .Builder(GPSCheckerWorker::class.java, 1, TimeUnit.HOURS)
                    .addTag(GPS_OFF_WORKER_TAG)
                    .build()

                workManager.enqueue(workerRequest)

                shEditor.putString(GPS_OFF_WORKER_TAG, GPS_OFF_WORKER_TAG)
                shEditor.putBoolean(GPS_OFF_BUTTON_STATE, true)
            } else {
                val gpsDataOffWorkerTag =
                    sharedPreferences.getString(GPS_OFF_WORKER_TAG, null)
                gpsDataOffWorkerTag?.let { workManager.cancelAllWorkByTag(it) }

                shEditor.remove(GPS_OFF_WORKER_TAG)
                shEditor.putBoolean(GPS_OFF_BUTTON_STATE, false)
            }
            shEditor.commit()
        }

        // Bluetooth Off
        val bluetoothOffCard = binding.bluetoothOff
        bluetoothOffCard.cardText.text = getString(R.string.bluetooth_off_card_text)
        bluetoothOffCard.buttonToggle.isChecked =
            sharedPreferences.getBoolean(BLUETOOTH_OFF_BUTTON_STATE, false)

        bluetoothOffCard.buttonToggle.setOnCheckedChangeListener { _, isChecked ->
            val shEditor = sharedPreferences.edit()
            if (isChecked) {
                val workerRequest = PeriodicWorkRequest
                    .Builder(BluetoothCheckerWorker::class.java, 1, TimeUnit.HOURS)
                    .addTag(BLUETOOTH_OFF_WORKER_TAG)
                    .build()

                workManager.enqueue(workerRequest)

                shEditor.putString(BLUETOOTH_OFF_WORKER_TAG, BLUETOOTH_OFF_WORKER_TAG)
                shEditor.putBoolean(BLUETOOTH_OFF_BUTTON_STATE, true)
            } else {
                val bluetoothOffWorkerTag =
                    sharedPreferences.getString(BLUETOOTH_OFF_WORKER_TAG, null)
                bluetoothOffWorkerTag?.let { workManager.cancelAllWorkByTag(it) }

                shEditor.remove(BLUETOOTH_OFF_WORKER_TAG)
                shEditor.putBoolean(BLUETOOTH_OFF_BUTTON_STATE, false)
            }
            shEditor.commit()
        }

        //Turn off wifi when leave home
        // TODO: ask permission at runtime
        val wifiOffCard = binding.wifiOff
        wifiOffCard.cardText.text = getString(R.string.wifi_off_card_text)
        wifiOffCard.buttonToggle.isChecked =
            sharedPreferences.getBoolean(WIFI_OFF_BUTTON_STATE, false)

        wifiOffCard.buttonToggle.setOnCheckedChangeListener { _, isChecked ->
            val shEditor = sharedPreferences.edit()

            if (isChecked) {
                var homeWifiSSID = sharedPreferences.getString(WIFI_OFF_CONFIGURATION_HOME_SSID, null)
                if (homeWifiSSID == null){
                    // flow to configure feature
                    val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
                    if (!wifiManager.isWifiEnabled){
                        Toast.makeText(this, "Wifi need to be on to continue", Toast.LENGTH_SHORT).show()
                        wifiOffCard.buttonToggle.isChecked = false
                        return@setOnCheckedChangeListener
                    }

                    val scanResultsSSID = wifiManager.scanResults.map { it.SSID }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, scanResultsSSID)

                    AlertDialog.Builder(this)
                        .setTitle("Choose your home wifi")
                        .setAdapter(adapter, object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                shEditor.putString(WIFI_OFF_CONFIGURATION_HOME_SSID, scanResultsSSID[which])
                                shEditor.commit()
                            }
                        }).create().show()
                }
                homeWifiSSID = sharedPreferences.getString(WIFI_OFF_CONFIGURATION_HOME_SSID, null)
                homeWifiSSID?.let {
                    val workerRequest = PeriodicWorkRequest
                        .Builder(WifiCheckerWorker::class.java, 1, TimeUnit.HOURS)
                        .addTag(WIFI_OFF_WORKER_TAG)
                        .setInputData(Data.Builder().putString(WIFI_OFF_CONFIGURATION_HOME_SSID, homeWifiSSID).build())
                        .build()

                    workManager.enqueue(workerRequest)
                    shEditor.putString(WIFI_OFF_WORKER_TAG, WIFI_OFF_WORKER_TAG)
                    shEditor.putBoolean(WIFI_OFF_BUTTON_STATE, true)
                    shEditor.commit()
                    return@setOnCheckedChangeListener
                }
                wifiOffCard.buttonToggle.isChecked = false
            } else {
                val wifiOffWorkerTag =
                    sharedPreferences.getString(WIFI_OFF_WORKER_TAG, null)
                wifiOffWorkerTag?.let { workManager.cancelAllWorkByTag(it) }
                shEditor.remove(WIFI_OFF_WORKER_TAG)
                shEditor.putBoolean(WIFI_OFF_BUTTON_STATE, false)
                shEditor.commit()
            }
        }
    }
}