package com.example.parkopol

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions


class MapaFragment : Fragment(), OnMapReadyCallback {
    private var listaLokalizacji = ArrayList<KontoFragment.MiejsceParkingowe>()
    private val REQUEST_LOCATION = 123
    private lateinit var googleMap: GoogleMap
    private var mapView: MapView? = null

    //private var listaLokalizacji = ArrayList<LatLng>()
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
        listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji);
        // dodowanieMarker(googleMap)
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
//            wyswietlDostepneMiejscaParkingowe()
//            TODO(naprawić funkcję)
//            zoomMyCuurentLocation()
        }
        // onMapReady(googleMap);
        return view
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
        listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji)
        // listaLokalizacji.add(0, KontoFragment.MiejsceParkingowe(false,false,"wlascielkasd",LatLng(-33.1,151.2),1.8))
        for (i in listaLokalizacji) {

            val marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(i.lokalizacja)
                    .title("Wolne miejsce")
            )
            Log.d("baza", "test9")
            val address = listaLokalizacji?.get(0)
            latlng = LatLng(address!!.lokalizacja!!.latitude, address.lokalizacja!!.longitude)
            builder.include(marker!!.position)
        }
        Log.d("baza", "test9")
        if (ActivityCompat.checkSelfPermission(
                context!!.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context!!.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        googleMap?.isMyLocationEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        val bounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, 0)
        //CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new MarkerOptions().position(latlng).title(showroomAddresses[3]).getPosition(),7F);
        googleMap?.animateCamera(cu)
    }

    private fun handleNewLocation(location: Location) {
        Log.d("komunikat", location.toString())
        val currentLatitude = location.latitude
        val currentLongitude = location.longitude
        val latLng = LatLng(currentLatitude, currentLongitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 21f))

    }

    private fun dodowanieMarker(googleMap: GoogleMap?) {
        val sydney = LatLng(-33.852, 151.211)
        googleMap!!.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )


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
        val locationManager: LocationManager =
            context?.getSystemService(LOCATION_SERVICE) as LocationManager
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
            location = locationManager.getLastKnownLocation(providers[i])
            if (location != null)
                break
        }
        Log.d("komunikat", "getLastKnownLocation 5")
        val gps = DoubleArray(2)
        if (location != null) {
            gps[0] = location.latitude
            gps[1] = location.longitude
            aktualnaLokalizacja = LatLng(gps[0], gps[1])
            Log.d("komunikat", gps[0].toString())
            Log.d("komunikat", gps[1].toString())
        }
        return
    }

    //    private fun wyswietlDostepneMiejscaParkingowe() {
//        myRef.get().addOnSuccessListener {
//            val latitude = it.child("latitude")
//            val longitude = it.child("longitude")
////            mMap?.addMarker(MarkerOptions().position(latitude, longitude).title(showroomAddresses[i]))
//            Log.d("bazadanych", "Got value $latitude")
//        }.addOnFailureListener {
//            Log.d("bazadanych", "Error getting data", it)
//        }
//    }
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
