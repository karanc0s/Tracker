package com.example.mapspractice_1

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.util.jar.Manifest

class MyService : Service() {

    private val TAG : String = "MyService"
    private lateinit var client : FusedLocationProviderClient;
    private val locCallback : LocationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
//            super.onLocationResult(p0)
            val locationList = p0.locations;
            if(locationList.isNotEmpty()){
                val location = locationList.last();

                Toast.makeText(applicationContext, "Location callback", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "onBackgroundLoc Req : Lat "+location.latitude + " , Long : " + location.longitude )

                
            }

        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        client = LocationServices.getFusedLocationProviderClient(applicationContext);
        startMyService();
        requestLocationUpdatesAtBackground()
        Log.e(TAG, "onCreate: SERVICE IS RUNNING")
        val ic = FloatingIcon(this@MyService)
        ic.open()
    }

    override fun onDestroy() {
        super.onDestroy()
        client.removeLocationUpdates(locCallback)
        Log.e(TAG, "onCreate: SERVICE IS STOPPED")
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun startMyService(){

        val pendingIntent = PendingIntent.getActivity(this , 0 , Intent(this , MainActivity::class.java) , 0)

        val n = NotificationCompat.Builder(this , ChannelBuilder().CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setContentTitle("asdfs")
            .setContentText("sadfsggfgds")
            .setSmallIcon(R.drawable.ic_android_black_24dp)
            .setAutoCancel(true)



        startForeground(2 , n.build())


    }

    private fun requestLocationUpdatesAtBackground(){

        val request = LocationRequest.create().apply {
            interval = 1000;
            fastestInterval = 500
            priority = Priority.PRIORITY_HIGH_ACCURACY

        }


        if(ContextCompat.checkSelfPermission(applicationContext
                    , android.Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED){

            client.requestLocationUpdates(request , locCallback, Looper.myLooper())

        }


    }
}