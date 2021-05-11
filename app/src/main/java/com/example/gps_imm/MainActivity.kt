package com.example.gps_imm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: FloatingActionButton
    private lateinit var output: LinearLayout
    private lateinit var currentCoords: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btnStart)
        output = findViewById(R.id.output)
        currentCoords = findViewById(R.id.currentCoords)

        btnStart.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=
                    PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=
                    PackageManager.PERMISSION_GRANTED){
                requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                0
                )
            } else {
                changeServiceState()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && permissions.size > 1 &&
            permissions[1] == Manifest.permission.ACCESS_COARSE_LOCATION &&
            permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && (
                    grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[1] == PackageManager.PERMISSION_GRANTED
                    )){
            changeServiceState(true)
        }
    }



    private fun changeServiceState(forceStart: Boolean = false) {
        if (!LocationService.running || forceStart){
            sendCommand(Constants.START_LOCATION_SERVICE)
            LocationData.location.observe(this){
                //Информация об изменении координат
            }
        } else {
            sendCommand(Constants.STOP_LOCATING_SERVICE)
            LocationData.location.removeObservers(this)
        }
    }

    private fun sendCommand(command: String) {
        val intent = Intent(this, LocationService::class.java).apply{
            action = command
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        }
    }

    private fun onLocationUpdate(l: Location){
        currentCoords.text = "Lat: ${l.latitude}, Lon: ${l.longitude}"

        val tv = TextView(this)
        tv.textSize = 18F
        tv.text = "Lat: ${l.latitude}, Lon: ${l.longitude}"
        output.addView(tv, 0)

    }
}