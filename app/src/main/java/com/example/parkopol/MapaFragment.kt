package com.example.parkopol

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class MapaFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private var mapView: MapView? = null
    private var showroomAddresses = arrayOfNulls<String>(5)
    private var aktualnaLokalizacja = LatLng(0.0, 0.0)
    var addressList: List<Address>? = null
    var latlng: LatLng? = null
    var builder = LatLngBounds.Builder()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_mapa, container, false)
        val buttonMapaZlokalizuj = view.findViewById(R.id.mapa_zlokalizuj) as ImageButton
        // Gets the MapView from the XML layout and creates it
        // Gets the MapView from the XML layout and creates it
        mapView = view.findViewById<View>(R.id.mapView) as MapView
        mapView!!.onCreate(savedInstanceState)
        // Set the map ready callback to receive the GoogleMap object
        // Set the map ready callback to receive the GoogleMap object
        mapView!!.getMapAsync(this)
        getLastKnownLocation()
        buttonMapaZlokalizuj.setOnClickListener {
            Log.d("komunikat", "Pobieram lokalizację")
//            TODO: naprawić funkcję
//            zoomMyCuurentLocation()
        }
        return view
    }
    override fun onMapReady(googleMap: GoogleMap?) {
        try {
            MapsInitializer.initialize(SecondActivity())
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
        showroomAddresses[0] = "Leszno"
        showroomAddresses[1] = "Rozstępniewo"
        showroomAddresses[2] = "Osieczna"
        showroomAddresses[3] = "Poznań"
        showroomAddresses[4] = "Widziszewo"
        val geocoder = Geocoder(activity)
        for (i in 0 until 5) {
            try {
                addressList = geocoder.getFromLocationName(showroomAddresses[i], 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val address = addressList?.get(0)
            latlng = LatLng(address!!.latitude, address.longitude)
            val marker = googleMap?.addMarker(MarkerOptions().position(latlng).title(showroomAddresses[i]))
            builder.include(marker!!.position)
        }
        if (ActivityCompat.checkSelfPermission(
                activity!!.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity!!.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        googleMap?.isMyLocationEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        val bounds = builder.build()
        val padding = 0
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        //CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new MarkerOptions().position(latlng).title(showroomAddresses[3]).getPosition(),7F);
        googleMap?.animateCamera(cu)
    }
    private fun handleNewLocation(location: Location) {
        Log.d("komunikat", location.toString())
        val currentLatitude = location.latitude
        val currentLongitude = location.longitude
        val latLng = LatLng(currentLatitude, currentLongitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 21f))
    }
//    private fun zoomMyCuurentLocation() {
//        Log.d("komunikat", "Błąd 1")
//        val locationManager =
//            context!!.getSystemService(LOCATION_SERVICE) as LocationManager
//        val criteria = Criteria()
//        if (ActivityCompat.checkSelfPermission(activity!!.applicationContext,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.d("komunikat", "Błąd 2")
//            ActivityCompat.checkSelfPermission(
//                activity!!.applicationContext,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//        }
//        Log.d("komunikat", "Błąd 3")
//        val location= locationManager.getBestProvider(criteria,false)?.let {
//            locationManager.getLastKnownLocation(it)
//        }
//        Log.d("komunikat", "Błąd 4")
//        if (location != null) {
//            Log.d("komunikat", "Błąd 5")
//            val lat: Double = location.latitude
//            val longi: Double = location.longitude
//            val latLng = LatLng(lat, longi)
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
//            Log.d("komunikat", "zoomMyCuurentLocation: location not null")
//        } else {
//            Log.d("komunikat", "Błąd 6")
//            setMyLastLocation()
//        }
//    }
//    private fun setMyLastLocation() {
//        Log.d("komunikat", "setMyLastLocation: excecute, and get last location")
//        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!.applicationContext)
//                Log.d("komunikat", "setlocation 1")
//    if (ActivityCompat.checkSelfPermission(
//            activity!!.applicationContext,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//            activity!!.applicationContext,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//        Log.d("komunikat", "setlocation 2")
//        return
//        }
//        Log.d("komunikat", "setlocation 3")
//        fusedLocationClient.lastLocation.addOnSuccessListener(SecondActivity()) { location ->
//            Log.d("komunikat", "setlocation 4")
//            if (location != null) {
//                Log.d("komunikat", "setlocation 5")
//                val lat: Double = location.latitude
//                val longi: Double = location.longitude
//                val latLng = LatLng(lat, longi)
//                Log.d("komunikat", "MyLastLocation coordinat :$latLng")
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
//            }
//        }
//    }
    private fun getLastKnownLocation() {
        val locationManager: LocationManager = context?.getSystemService(LOCATION_SERVICE) as LocationManager
        val providers: List<String> = locationManager.getProviders(true)
        var location: Location? = null
        Log.d("komunikat", "getLastKnownLocation 1")
        for (i in providers.size - 1 downTo 0) {
            Log.d("komunikat", "getLastKnownLocation 2")
            if (ActivityCompat.checkSelfPermission(
                    activity!!.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity!!.applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("komunikat", "getLastKnownLocation 3")
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            Log.d("komunikat", "getLastKnownLocation 4")
            location= locationManager.getLastKnownLocation(providers[i])
            if (location != null)
                break
        }
        Log.d("komunikat", "getLastKnownLocation 5")
        val gps = DoubleArray(2)
        if (location != null) {
            gps[0] = location.latitude
            gps[1] = location.longitude
            aktualnaLokalizacja = LatLng(gps[0], gps[1])
            Log.e("komunikat",gps[0].toString())
            Log.e("komunikat",gps[1].toString())
        }
    return
    }
    //    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
//            when (grantResults[0]) {
//                PackageManager.PERMISSION_GRANTED -> getLocation()
//                PackageManager.PERMISSION_DENIED -> Toast.makeText(SecondActivity(), "Zezwól na lokalizację", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
    override fun onResume() {
        mapView?.onResume();
        super.onResume();
    }
    override fun onDestroyView() {
        super.onDestroyView()
        super.onDestroy();
        mapView?.onDestroy();
    }
}
