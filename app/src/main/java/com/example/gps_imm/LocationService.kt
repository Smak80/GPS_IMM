package com.example.gps_imm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService

class LocationService : LifecycleService()
{
    companion object {
        var running = false
    }

    private fun locationChanged(l: Location){
        LocationData.location.postValue(l)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.apply{
            when(action){
                Constants.START_LOCATION_SERVICE ->{
                    startLocationService(intent)
                }
                Constants.STOP_LOCATING_SERVICE ->{
                    stopLocationService(intent)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stopLocationService(intent: Intent) {
        running = false
        LocatingHelper.stopLocating()
        stopService(intent)
    }

    private fun startLocationService(intent: Intent) {
        running = true
        LocatingHelper.startLocating(applicationContext, ::locationChanged)
        val channelID = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel()
        } else ""
        val notificationBuilder = NotificationCompat.Builder(
                this, channelID
        )
        val notification = notificationBuilder
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()
        startForeground(Constants.FOREGROUND_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelName = "Location Service Cannel"
        val chan = NotificationChannel(Constants.LOCATION_CHANNEL_ID,
                channelName, NotificationManager.IMPORTANCE_DEFAULT)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(chan)
        return Constants.LOCATION_CHANNEL_ID
    }
}