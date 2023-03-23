package com.example.mapspractice_1


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG : String = "MainActivity"
    private val API_KEY = "AIzaSyDZB8WERKMpBOWC9f5k1JNmTaTiXuGjVFU";
    private val permissionArr = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.ACCESS_FINE_LOCATION)

    lateinit var mLocationRequest: LocationRequest;
    lateinit var mMap : GoogleMap;
    lateinit var navigate : FloatingActionButton;

    private var mapFragment : SupportMapFragment? = null;
    var mLastLocation : Location? = null;
    var mCurrLocationMarker: Marker? = null
    var mFusedLocationClient: FusedLocationProviderClient? = null

    var mCurrentLocationCords : LatLng? = null;
    var destinationCords : LatLng? = null;

    private var mLocationCallback : LocationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
           // super.onLocationResult(p0)
            val locationList = p0.locations;
            if(locationList.isNotEmpty()){
                val location = locationList.last()
                //Log.e(TAG, "onLocationResult: Location :: Lat : "+location.latitude + ",  Long : "+location.longitude)

                //  we have location, so clear all previous markers
                mCurrLocationMarker?.remove()
                val pos = LatLng(location.latitude , location.longitude);
                mCurrentLocationCords = pos

                //markLocation(mMap  , pos , MarkerOptions());


            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigate = findViewById(R.id.fab_nav);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mapFragment = supportFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        checkOverLayPermission()

        navigate.setOnClickListener{
            Log.e(TAG, "onCreate:" +"\n"+
                    "A = Lat : " + mCurrentLocationCords?.latitude + ", " +
                    "Long : " + mCurrentLocationCords?.longitude + "\n" +
                    "B = Lat : " + destinationCords?.latitude + ", " +
                    "Long : " + destinationCords?.longitude)

            if(mCurrentLocationCords != null && destinationCords != null) {
                val url: String = getRouteURL(mCurrentLocationCords!!, destinationCords!!)

                Log.e(TAG, "onCreate: $url")

/*//                val saddr : String = ""+mCurrentLocationCords?.latitude+","+mCurrentLocationCords?.longitude
//                val daddr : String = ""+destinationCords?.latitude+","+destinationCords?.longitude
//                val ur : String  = "http://maps.google.com/maps?saddr=$saddr&daddr=$daddr"
//
//                val intent = Intent(
//                    Intent.ACTION_VIEW,
//                    Uri.parse(ur)
//                   // Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345")
//                )
//                startActivity(intent)*/
                navigationIntent()

            }
        }

    }

    private fun navigationIntent(){
        val gmmIntentUri = Uri.parse("google.navigation:q=" + destinationCords?.latitude.toString() + "," + destinationCords?.longitude.toString() + "&mode=d")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    override fun onPause() {

        super.onPause()
        // Stop location updates when Activity is no longer active
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
        startSer()

    }

    override fun onResume() {
        super.onResume()
        stopService(Intent(this, MyService::class.java))
    }

    override fun onStart() {
        super.onStart()
        stopService(Intent(this, MyService::class.java))
    }




    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mLocationRequest = LocationRequest.create().apply {
            interval = 10000;
            priority = Priority.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100;
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED ) {

            checkAndGetPermission()
        }else {
            mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
            mMap.isMyLocationEnabled = true
        }



        googleMap.setOnMapClickListener {
            destinationCords = it;
            markLocation(googleMap , it  , MarkerOptions())
        }
    }



    private fun markLocation(map : GoogleMap, position : LatLng, markerOptions: MarkerOptions) {
        markerOptions.position(position);
        markerOptions.title("Lat : "+position.latitude.toString() + ", Long : " + position.longitude)
        Log.e(TAG, "Co-ordinates :::  Long : " + position.longitude + ", Lat : " + position.latitude +" :::")
        map.clear()    // Remove all marker

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        // Animating to zoom the marker
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
        // Add marker on map
        map.addMarker(markerOptions)



        makeCurve()

    }

    private fun getRouteURL(origin:LatLng, dest:LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +   // destination
                "&sensor=false" +
                "&mode=driving" +  // Transportation MODE
                "&key=$API_KEY";
    }

    private fun makeCurve(){


//        val latLng1 = LatLng(40.7128, 74.0059) // New York
//
//        val latLng2 = LatLng(51.5074, 0.1278) // London

        val latLng1 = mCurrentLocationCords

        val latLng2 = destinationCords


        val marker1 = mMap.addMarker(MarkerOptions().position(latLng1!!).title("Origin"))
        val marker2 = mMap.addMarker(MarkerOptions().position(latLng2!!).title("Destination"))

       // val pattern: List<PatternItem> = Arrays.<PatternItem> asList < PatternItem ? > Dash(30), Gap(20))
        val pattern : List<PatternItem> = listOf(Dash(20F) , Gap(5F))
        val popt = PolylineOptions().add(latLng1).add(latLng2)
            .width(5f)
            .color(Color.BLACK)
            .pattern(pattern)
            .geodesic(true)

            .zIndex(100F)
        mMap.addPolyline(popt)

        val builder = LatLngBounds.Builder()

        builder.include(marker1!!.position)
        builder.include(marker2!!.position)

        val bounds = builder.build()
        val padding = 200 // offset from edges of the map in pixels

        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.moveCamera(cu)
        mMap.animateCamera(cu)
    }


    /// Permissions
    private fun checkAndGetPermission(){
        ActivityCompat.requestPermissions(this , permissionArr , 1009)
    }
    private fun checkOverLayPermission(){
        if(!Settings.canDrawOverlays(this)){
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }
    }


    // background Stuff
    private fun startSer(){
        if(Settings.canDrawOverlays(this)){
            startForegroundService(Intent(this , MyService::class.java))
        }
    }


    private fun gmapsCubicBezier(p1: LatLng, p2: LatLng, pA: LatLng, pB: LatLng) {

        //Polyline options
        val options = PolylineOptions()
        var curveLatLng: LatLng? = null
        var t = 0.0
        while (t < 1.01) {

            // P = (1−t)3P1 + 3(1−t)2tP2 +3(1−t)t2P3 + t3P4; for 4 points
            val arcX =
                (1 - t) * (1 - t) * (1 - t) * p1.latitude + 3 * (1 - t) * (1 - t) * t * pA.latitude + 3 * (1 - t) * t * t * pB.latitude + t * t * t * p2.latitude
            val arcY =
                (1 - t) * (1 - t) * (1 - t) * p1.longitude + 3 * (1 - t) * (1 - t) * t * pA.longitude + 3 * (1 - t) * t * t * pB.longitude + t * t * t * p2.longitude
            curveLatLng = LatLng(arcX, arcY)
            options.add(curveLatLng)
            //Draw polyline
            mMap.addPolyline(options.width(5f).color(Color.DKGRAY).geodesic(false))
            t += 0.01
        }
        mMap.addMarker(MarkerOptions().position(curveLatLng!!))
    }


}