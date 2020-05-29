package co.ltlabs.testwifimanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    private lateinit var signalChangeCountText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signalChangeCountText = findViewById(R.id.signal_changed)

        var startTime = System.currentTimeMillis()
        val wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                wifiManager.startScan()
                val newRssi = wifiManager.connectionInfo.rssi
                var timePassed = (System.currentTimeMillis() - startTime)
                Toast.makeText(applicationContext, "new RSSI: $newRssi", Toast.LENGTH_SHORT).show()

                val success = intent?.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false) ?: false
//                val success = intent?.getBooleanExtra(WifiManager.EXTRA_NEW_RSSI, false) ?: false
//                wifiManager.connectionInfo
                Log.i("MainActivity", "onReceive success: $success")
                scanWifi(wifiManager)
                Log.i("MainActivity", "time elapsed: $timePassed")
                signalChangeCountText.text = "true"

//                Thread.sleep(1000)
                signalChangeCountText.text= "false"
            }
        }

        val intentFilter = IntentFilter()
//        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION)

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.startScan()

        applicationContext.registerReceiver(wifiScanReceiver, intentFilter)


//        connetToWifi(wifiManager)
//        scanWifi(wifiManager)
    }

    private fun scanWifi(wifiManager: WifiManager) {

        // scan networks
        val wifiList: List<ScanResult> = wifiManager.scanResults
        Log.i("MainActivity", "ScanResults size is ${wifiManager.scanResults.size}")
        for (scanResult in wifiList) {
            val level = WifiManager.calculateSignalLevel(scanResult.level, 5)
            Log.i("MainActivity", "Scanned name is ${scanResult.SSID} Level is: $level")
        }

        // level of current connection
        val rssi = wifiManager.connectionInfo.rssi
        val level = WifiManager.calculateSignalLevel(rssi, 5)
        Log.i("MainActivity", "Current connectio level is $level")
    }

    private fun connetToWifi(wifiManager: WifiManager) {
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }

        val conf = WifiConfiguration()
        conf.SSID = String.format("\"%s\"", "HUAWEI-z2uM")
//        conf.preSharedKey = String.format("\"%s\"", "e9FwxN2z")

        val netId = wifiManager.addNetwork(conf)
        wifiManager.disconnect()
        wifiManager.enableNetwork(netId, true)
        wifiManager.reconnect()

    }
}
