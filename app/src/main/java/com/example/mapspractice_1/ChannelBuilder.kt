package com.example.mapspractice_1

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager


private const val TAG = "ChannelBuilder"

class ChannelBuilder : Application() {
    val CHANNEL_ID = "CH1009"
    val CHANNEL_NAME = "MapChannel"



    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

    }

    private fun createNotificationChannel(){
        val channel : NotificationChannel = NotificationChannel(
            CHANNEL_ID ,
            CHANNEL_NAME ,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}