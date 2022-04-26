package com.example.codeacademy.hamyarservice

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkGPSPermission()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


    }

    var driverLocation:Location ?= null

   inner class MyLocationListerner : LocationListener
   {
       constructor()
       {
           driverLocation = Location("Start")
           driverLocation!!.longitude = 0.0
           driverLocation!!.latitude = 0.0
       }

       override fun onLocationChanged(location: Location?) {
           driverLocation = location
       }

       override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

       }

       override fun onProviderEnabled(provider: String?) {

       }

       override fun onProviderDisabled(provider: String?) {

       }

   }

    @SuppressLint("MissingPermission")
    fun GetUserLocation()
    {
        Toast.makeText(this,"دسترسی به موقعیت فعال شد",Toast.LENGTH_LONG).show()

        var myLocationListener = MyLocationListerner()

        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocationListener)

        var myThread = myThread()
        myThread.start()

    }


    var oldDriverLocation:Location ?= null
    inner class myThread : Thread
    {

        constructor():super()
        {
            oldDriverLocation = Location("OldDriverLocation")
            oldDriverLocation!!.latitude = 0.0
            oldDriverLocation!!.longitude = 0.0
        }
        override fun run() {
            while(true)
            {


                try
                {
                    if (oldDriverLocation!!.distanceTo(driverLocation) == 0f)
                    {
                        continue
                    }


                    LoadStudents()
                    oldDriverLocation = driverLocation
                    runOnUiThread {
                        mMap!!.clear()

                        // نمایش مارکر راننده
                        val driverLatLng = LatLng(driverLocation!!.latitude, driverLocation!!.longitude)
                        mMap.addMarker(MarkerOptions()
                                .position(driverLatLng)
                                .title("راننده سرویس - احمدی")
                                .snippet("0939123456")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLatLng,17f))



                        // اضافه کردن مارکر دانش آموزان
                        for (i in 0 until listOfStudents.size)
                        {
                            var student = listOfStudents[i]

                            val studentLocation = LatLng(student.location!!.latitude, student.location!!.longitude)

                            mMap.addMarker(MarkerOptions()
                                    .position(studentLocation)
                                    .title(student.name)
                                    .snippet(student.desc)
                                    .icon(BitmapDescriptorFactory.fromResource(student.image!!))
                            )

                            if (driverLocation!!.distanceTo(student.location) < 2)
                            {
                                Toast.makeText(applicationContext, "ارسال پیام برای والدین " + student.name,Toast.LENGTH_LONG).show()
                            }

                        }
                    }

                    Thread.sleep(1000)


                }
                catch (ex:Exception)
                {

                }
            }
            super.run()
        }
    }

    fun checkGPSPermission()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (ActivityCompat.checkSelfPermission(
                    this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),222)
                return
            }
        }

        GetUserLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode)
        {
            222->
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    GetUserLocation()
                }
                else
                {
                    Toast.makeText(this,"برای ادامه اجرای برنامه نیاز به دسترسی موقعیت است!", Toast.LENGTH_LONG).show()

                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    var listOfStudents = ArrayList<Student>()

    fun LoadStudents()
    {
        listOfStudents.add(Student(R.drawable.boy,"محسن یاری","0939123456",32.637313,51.663641))
        listOfStudents.add(Student(R.drawable.girl,"لیلا طهماسب","0936123456",32.637785, ))
        listOfStudents.add(Student(R.drawable.girl,"آناهیتا","0937123456",32.638451,51.664510))
    }
}
