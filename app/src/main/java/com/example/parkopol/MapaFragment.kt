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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class MapaFragment : Fragment(), OnMapReadyCallback {
    private var mapView: MapView? = null
    private var showroomAddresses = arrayOfNulls<String>(5)
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
        buttonMapaZlokalizuj.setOnClickListener {
            Log.d("komunikat", "Pobieram lokalizację")
//            TODO: naprawić dwie poniższe funkcje
//            zoomMyCuurentLocation()
//            setMyLastLocation()
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
            val marker =
                googleMap?.addMarker(MarkerOptions().position(latlng).title(showroomAddresses[i]))
            builder.include(marker!!.position)
        }
//        latlng = LatLng(51.7919374642711, 16.869667087783448)
//        val lokalizacja =
//            googleMap?.addMarker(MarkerOptions().position(latlng).title("Moja lokalizacja"))
//        builder.include(lokalizacja!!.position)

        val bounds = builder.build()
        val padding = 0
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        //CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new MarkerOptions().position(latlng).title(showroomAddresses[3]).getPosition(),7F);
        googleMap?.animateCamera(cu)
    }

    private fun zoomMyCuurentLocation() {
        val locationManager =
            context!!.getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        if (ActivityCompat.checkSelfPermission(
                SecondActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.checkSelfPermission(
                SecondActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
        val location: Location? = locationManager!!.getLastKnownLocation(
            locationManager.getBestProvider(
                criteria,
                false
            )!!
        )
        if (location != null) {
            val lat: Double = location.latitude
            val longi: Double = location.longitude
            val latLng = LatLng(lat, longi)
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
            Log.d("komunikat", "zoomMyCuurentLocation: location not null")
        } else {
            setMyLastLocation()
        }
    }

    private fun setMyLastLocation() {
        Log.d("komunikat", "setMyLastLocation: excecute, and get last location")
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(SecondActivity())
        if (ActivityCompat.checkSelfPermission(
                SecondActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                SecondActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener(
            SecondActivity()
        ) { location ->
            if (location != null) {
                val lat: Double = location.latitude
                val longi: Double = location.longitude
                val latLng = LatLng(lat, longi)
                Log.d("komunikat", "MyLastLocation coordinat :$latLng")
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
            }
        }
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

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }
}
