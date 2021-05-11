package com.example.parkopol

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class MapaFragment: Fragment(), OnMapReadyCallback {
    private var mapView: MapView? = null
    var showroom_addresses = arrayOfNulls<String>(5)
    var addressList: List<Address>? = null
    var latlng: LatLng? = null
    var builder = LatLngBounds.Builder()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("komunikat", "startMapaFragment")
        val view: View = inflater.inflate(R.layout.fragment_mapa, container, false)

        // Gets the MapView from the XML layout and creates it

        // Gets the MapView from the XML layout and creates it
        mapView = view.findViewById<View>(R.id.mapView) as MapView
        mapView!!.onCreate(savedInstanceState)

        // Set the map ready callback to receive the GoogleMap object

        // Set the map ready callback to receive the GoogleMap object
        mapView!!.getMapAsync(this)

        return view
//        val mapFragment = fragmentManager?.findFragmentById(R.id.mapView) as? SupportMapFragment
//        mapFragment?.getMapAsync(this)
//
//        return inflater.inflate(R.layout.fragment_mapa, container, false)
    }
    override fun onMapReady(googleMap: GoogleMap?) {
        try {
            MapsInitializer.initialize(SecondActivity())
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
//        Log.d("komunikat", "onMapReady")
//        googleMap?.apply {
//            val sydney = LatLng(-33.852, 151.211)
//            addMarker(
//                MarkerOptions()
//                    .position(sydney)
//                    .title("Marker in Sydney")
//            )
//            moveCamera(CameraUpdateFactory.newLatLng(sydney))
//        }
        val mGoogleMap = googleMap

        showroom_addresses[0] = "Krzywiń"
        showroom_addresses[1] = "Rozstępniewo"
        showroom_addresses[2] = "Osieczna"
        showroom_addresses[3] = "Poznań"
        showroom_addresses[4] = "Widziszewo"
        val geocoder = Geocoder(getActivity())
        for (i in 0 until 5) {
            try {
                addressList = geocoder.getFromLocationName(showroom_addresses[i], 1);
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val address = addressList?.get(0)
            latlng = LatLng(address!!.getLatitude(), address.getLongitude());
            val marker = mGoogleMap?.addMarker(MarkerOptions().position(latlng).title(showroom_addresses[i]));
            builder.include(marker!!.getPosition())
        }

        val bounds = builder.build()
        val padding = 0
        val cu = CameraUpdateFactory.newLatLngBounds(bounds,padding)
        //CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new MarkerOptions().position(latlng).title(showroom_addresses[3]).getPosition(),7F);
        mGoogleMap?.animateCamera(cu)
    }
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
