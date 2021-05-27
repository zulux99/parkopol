package com.example.parkopol

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
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
    private var mapView: MapView? = null
    private var aktualnaLokalizacja = LatLng(0.0, 0.0)
    private var latlng: LatLng? = null
    private var builder = LatLngBounds.Builder()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji)
        // dodowanieMarker(googleMap)
        val view: View = inflater.inflate(R.layout.fragment_mapa, container, false)
        val buttonMapaZlokalizuj = view.findViewById(R.id.mapa_zlokalizuj) as ImageButton
        // Gets the MapView from the XML layout and creates it
        mapView = view.findViewById<View>(R.id.mapView) as MapView
        mapView!!.onCreate(savedInstanceState)
        // Set the map ready callback to receive the GoogleMap object
        mapView!!.getMapAsync(this)
//        getLastKnownLocation()
        buttonMapaZlokalizuj.setOnClickListener {
            Log.d("komunikat", "Przycisk")
        }
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
            val address = listaLokalizacji[0]
            latlng = LatLng(address.lokalizacja!!.latitude, address.lokalizacja!!.longitude)
            builder.include(marker!!.position)
        }
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
        googleMap?.animateCamera(cu)
    }
    private fun dodowanieMarker(googleMap: GoogleMap?) {
        val sydney = LatLng(-33.852, 151.211)
        googleMap!!.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )
    }
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
    override fun onResume() {
        mapView?.onResume()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        super.onDestroy()
        mapView?.onDestroy()
    }
}
