package br.cin.ufpe.if1001.taskmanager.activities


import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import br.cin.ufpe.if1001.taskmanager.R
import br.cin.ufpe.if1001.taskmanager.databinding.ActivityMainBinding
import br.cin.ufpe.if1001.taskmanager.utils.GeneralServices
import br.cin.ufpe.if1001.taskmanager.utils.NotificationUtil
import br.cin.ufpe.if1001.taskmanager.utils.PermissionHelper
import br.cin.ufpe.if1001.taskmanager.workers.BluetoothCheckerWorker
import br.cin.ufpe.if1001.taskmanager.workers.GPSCheckerWorker
import br.cin.ufpe.if1001.taskmanager.workers.MobileDataCheckerWorker
import br.cin.ufpe.if1001.taskmanager.workers.WifiCheckerWorker
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    companion object {
        const val SHARED_PREFERENCE = "br.cin.ufpe.if1001.taskmanager.activities.MainActivity"

        const val MOBILE_DATA_OFF_BUTTON_STATE = "mobile_data_off_wifi_button_state"
        const val MOBILE_DATA_OFF_WORKER_TAG = "mobile_data_off_wifi_worker_tag"

        const val GPS_OFF_BUTTON_STATE = "gps_off_button_state"
        const val GPS_OFF_WORKER_TAG = "gps_off_worker_tag"

        const val BLUETOOTH_OFF_BUTTON_STATE = "bluetooth_off_button_state"
        const val BLUETOOTH_OFF_WORKER_TAG = "bluetooth_off_worker_tag"

        const val WIFI_OFF_BUTTON_STATE = "wifi_off_button_state"
        const val WIFI_OFF_CONFIGURATION_HOME_SSID = "wifi_off_home_ssid_configured"
        const val WIFI_OFF_WORKER_TAG = "wifi_off_worker_tag"
    }

    private val workManager = WorkManager.getInstance(this)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE)

        NotificationUtil.createNotificationChannel(this)

        // Mobile Data Off Wifi
        binding.mobileDataOffWifi.apply {
            cardText.apply {
                text = getString(R.string.mobile_data_off_card_text)
                setOnClickListener {
                    AlertDialog.Builder(this@MainActivity)
                        .setMessage(getString(R.string.mobile_data_off_dialog_text))
                        .create().show()
                }
            }
            buttonToggle.apply {
                isChecked = sharedPreferences.getBoolean(MOBILE_DATA_OFF_BUTTON_STATE, false)
                setOnCheckedChangeListener(mobileOffDataButtonListener)
            }
        }

        // Gps Off
        binding.gpsOff.apply {
            cardText.apply {
                text = getString(R.string.gps_off_card_text)
                setOnClickListener {
                    AlertDialog.Builder(this@MainActivity)
                        .setMessage(getString(R.string.gps_off_dialog_text))
                        .create().show()
                }
            }
            buttonToggle.apply {
                isChecked = sharedPreferences.getBoolean(GPS_OFF_BUTTON_STATE, false)
                setOnCheckedChangeListener(gpsOffButtonListener)
            }
        }

        // Bluetooth Off
        binding.bluetoothOff.apply {
            cardText.apply {
                text = getString(R.string.bluetooth_off_card_text)
                setOnClickListener {
                    AlertDialog.Builder(this@MainActivity)
                        .setMessage(getString(R.string.bluetooth_off_dialog_text))
                        .create().show()
                }
            }
            buttonToggle.apply {
                isChecked = sharedPreferences.getBoolean(BLUETOOTH_OFF_BUTTON_STATE, false)
                setOnCheckedChangeListener(bluetoothOffButtonListener)
            }
        }

        //Turn off wifi when leave home
        binding.wifiOff.apply {
            cardText.apply {
                text = getString(R.string.wifi_off_card_text)
                setOnClickListener {
                    AlertDialog.Builder(this@MainActivity)
                        .setMessage(getString(R.string.wifi_off_dialog_text))
                        .setPositiveButton("reset") { dialog, _ ->
                            sharedPreferences.getString(WIFI_OFF_WORKER_TAG, null).apply {
                                this?.let { workManager.cancelAllWorkByTag(it) }
                            }
                            sharedPreferences.edit().apply {
                                putBoolean(WIFI_OFF_BUTTON_STATE, false)
                                remove(WIFI_OFF_CONFIGURATION_HOME_SSID)
                                remove(WIFI_OFF_WORKER_TAG)
                                commit()
                            }
                            this@MainActivity.binding.wifiOff.buttonToggle.isChecked = false
                            dialog.dismiss()
                        }
                        .create().show()
                }
            }
            buttonToggle.apply {
                isChecked = sharedPreferences.getBoolean(WIFI_OFF_BUTTON_STATE, false)
                setOnCheckedChangeListener(wifiOffButtonListener)
            }
        }
    }

    private val mobileOffDataButtonListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            val sharedPreferences = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE)
            val shEditor = sharedPreferences.edit()
            if (isChecked) {
                PeriodicWorkRequest.Builder(MobileDataCheckerWorker::class.java, 1, TimeUnit.HOURS)
                    .addTag(MOBILE_DATA_OFF_WORKER_TAG)
                    .build()
                    .apply { workManager.enqueue(this) }

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

    private val gpsOffButtonListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            val sharedPreferences = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE)
            val shEditor = sharedPreferences.edit()
            if (isChecked) {
                PeriodicWorkRequest.Builder(GPSCheckerWorker::class.java, 1, TimeUnit.HOURS)
                    .addTag(GPS_OFF_WORKER_TAG)
                    .build()
                    .apply { workManager.enqueue(this) }

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

    private val bluetoothOffButtonListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            val sharedPreferences = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE)
            val shEditor = sharedPreferences.edit()
            if (isChecked) {
                PeriodicWorkRequest.Builder(BluetoothCheckerWorker::class.java, 1, TimeUnit.HOURS)
                    .addTag(BLUETOOTH_OFF_WORKER_TAG)
                    .build()
                    .apply { workManager.enqueue(this) }

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

    private val wifiOffButtonListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            val sharedPreferences = getSharedPreferences(SHARED_PREFERENCE, MODE_PRIVATE)
            val shEditor = sharedPreferences.edit()

            if (isChecked) {
                var homeWifiSSID = sharedPreferences.getString(WIFI_OFF_CONFIGURATION_HOME_SSID, null)
                if (homeWifiSSID == null){
                    // request permissions
                    val multiplePermissionsListener = object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            when {
                                report == null -> throw IllegalStateException()
                                report.areAllPermissionsGranted() -> {
                                    // flow to configure feature

                                    if (!(GeneralServices.isWifiOn(this@MainActivity) && GeneralServices.isGPSOn(this@MainActivity))){
                                        Toast.makeText(this@MainActivity, "Wifi and GPS need to be on to continue", Toast.LENGTH_SHORT).show()
                                        buttonView.isChecked = false
                                    }
                                    val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
                                    val scanResultsSSID = wifiManager.scanResults.map { it.SSID }

                                    ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, scanResultsSSID).apply {
                                        AlertDialog.Builder(this@MainActivity)
                                            .setTitle("Choose your home wifi")
//                                            .setView(R.attr.selectableItemBackground)
                                            .setAdapter(this) { _, which ->
                                                shEditor.putString(WIFI_OFF_CONFIGURATION_HOME_SSID, scanResultsSSID[which])
                                                shEditor.commit()
                                            }.create().show()
                                    }
                                }
                                report.isAnyPermissionPermanentlyDenied -> {
                                    AlertDialog.Builder(this@MainActivity)
                                        .setTitle("Permission needed")
                                        .setMessage("This permission is needed to access wifi devices, u will need to activate on settings")
                                        .setPositiveButton("ok") { dialog, _ ->
                                            dialog.dismiss()
                                            this@MainActivity.startActivity(
                                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", this@MainActivity.packageName, null)).addFlags(
                                                    Intent.FLAG_ACTIVITY_NEW_TASK))
                                        }
                                        .setNegativeButton("cancel") { dialog, _ ->
                                            dialog.dismiss()
                                        }
                                        .create().show()
                                }
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                            token?.continuePermissionRequest()
                        }
                    }
                    PermissionHelper.requestPermission(this, arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION), multiplePermissionsListener)
                }
                homeWifiSSID = sharedPreferences.getString(WIFI_OFF_CONFIGURATION_HOME_SSID, null)
                Log.i("XABLAU", "home wifi: $homeWifiSSID")
                homeWifiSSID?.let {
                    PeriodicWorkRequest.Builder(WifiCheckerWorker::class.java, 1, TimeUnit.HOURS)
                        .addTag(WIFI_OFF_WORKER_TAG)
                        .setInputData(Data.Builder().putString(WIFI_OFF_CONFIGURATION_HOME_SSID, homeWifiSSID).build())
                        .build()
                        .apply { workManager.enqueue(this) }

                    shEditor.putString(WIFI_OFF_WORKER_TAG, WIFI_OFF_WORKER_TAG)
                    shEditor.putBoolean(WIFI_OFF_BUTTON_STATE, true)
                    shEditor.commit()
                    return@OnCheckedChangeListener
                }
                buttonView.isChecked = false
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