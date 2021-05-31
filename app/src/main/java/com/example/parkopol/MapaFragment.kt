package com.example.parkopol

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MapaFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener {
    private var listaLokalizacji = ArrayList<KontoFragment.MiejsceParkingowe>()
    private var mapView: MapView? = null
    private val mMarkerArray = ArrayList<Marker>()
    private var aktualnaLokalizacja = LatLng(0.0, 0.0)
    private var builder = LatLngBounds.Builder()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji)
        // dodowanieMarker(googleMap)
        val view: View = inflater.inflate(R.layout.fragment_mapa, container, false)
        // Gets the MapView from the XML layout and creates it
        mapView = view.findViewById<View>(R.id.mapView) as MapView
        mapView!!.onCreate(savedInstanceState)
        // Set the map ready callback to receive the GoogleMap object
        mapView!!.getMapAsync(this)
//        getLastKnownLocation()
        return view
    }

    private var mindist = 0f
    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
        listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji)
        // listaLokalizacji.add(0, KontoFragment.MiejsceParkingowe(false,false,"wlascielkasd",LatLng(-33.1,151.2),1.8))
        for ((licznik, i) in listaLokalizacji.withIndex()) {

            val marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(i.lokalizacja)
                    .title("Wolne$licznik")
            )

            if (marker != null) {
                marker.setTag(i.idMParkingowego)
                mMarkerArray.add(marker)
            }
            builder.include(marker!!.position)
            googleMap.setOnMarkerClickListener(this)
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
        googleMap?.setOnMapClickListener(this)
        googleMap?.isMyLocationEnabled = true
        googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        val bounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, 0)
        googleMap?.animateCamera(cu)
        if (googleMap != null) {
            pokazWolne(googleMap)
        }
    }
    private var najblizszyMarker: Marker? = null
    private fun pokazWolne(mMap: GoogleMap) {
        val buttonMapaZlokalizuj = view?.findViewById(R.id.mapa_zlokalizuj) as ImageButton
        buttonMapaZlokalizuj.setOnClickListener {
            var licznik = 0
            for (i in mMarkerArray)
            {
                val distance = FloatArray(1)
                Location.distanceBetween(
                    getLastKnownLocation().latitude,
                    getLastKnownLocation().longitude, i.position.latitude,
                    i.position.longitude, distance
                )
                if (licznik == 0) {
                    mindist = distance[0]
                    najblizszyMarker = i
                }
                else if (mindist > distance[0]) {
                    mindist = distance[0]
                    najblizszyMarker = i
                }
                licznik++
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(najblizszyMarker!!.position, 19F))
        }
    }


    override fun onMarkerClick(marker: Marker): Boolean {
        val btnZaparkuj = view?.findViewById(R.id.zaparkuj) as Button
        btnZaparkuj.setOnClickListener{
        Log.d("komunikat_ID", marker.getTag().toString())
        zmianaStanu(marker.getTag().toString(),true)
        Zaparkowanie(FirebaseAuth.getInstance().currentUser!!.uid,marker!!.getTag().toString(), SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.GERMAN).format(Date()),"0" , 0.0 ).dodawanieDobazy()
        val intent = Intent(activity, SecondActivity::class.java)
        startActivity(intent)
        }

        btnZaparkuj.visibility = View.VISIBLE
        return false
    }

    override fun onMapClick(p0: LatLng) {
        val btnZaparkuj = view?.findViewById(R.id.zaparkuj) as Button
        btnZaparkuj.visibility = View.GONE
    }

    private fun getLastKnownLocation(): LatLng {
        val locationManager: LocationManager =
            context?.getSystemService(LOCATION_SERVICE) as LocationManager
        val providers: List<String> = locationManager.getProviders(true)
        var location: Location? = null
        for (i in providers.size - 1 downTo 0) {
            if (ActivityCompat.checkSelfPermission(
                    activity!!.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity!!.applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return LatLng(0.0, 0.0)
            }
            location = locationManager.getLastKnownLocation(providers[i])
            if (location != null)
                break
        }
        val gps = DoubleArray(2)
        if (location != null) {
            gps[0] = location.latitude
            gps[1] = location.longitude
            aktualnaLokalizacja = LatLng(gps[0], gps[1])
            Log.d("komunikat", gps[0].toString())
            Log.d("komunikat", gps[1].toString())
        }
        return aktualnaLokalizacja
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
