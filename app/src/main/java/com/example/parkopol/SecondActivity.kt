package com.example.parkopol

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.parkopol.databinding.ActivitySecondBinding
import com.example.parkopol.databinding.NavHeaderBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.time.LocalDateTime


class SecondActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var listaLokalizacji = ArrayList<KontoFragment.MiejsceParkingowe>()
    private lateinit var drawer: DrawerLayout
    private lateinit var binding: ActivitySecondBinding

    //    private lateinit var fragmentKontoBinding: FragmentKontoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji)
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        val headerBinding = NavHeaderBinding.bind(binding.navView.getHeaderView(0))
//        fragmentKontoBinding = FragmentKontoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val signInAccount: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        drawer = binding.drawerLayout
        val toogle = ActionBarDrawerToggle(
            this, drawer, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toogle)
        toogle.syncState()
        if (signInAccount != null) {
            headerBinding.imieNazwisko.text = signInAccount.displayName
            headerBinding.adresEmail.text = signInAccount.email
            Picasso.get().load(signInAccount.photoUrl!!.toString()).into(headerBinding.navAvatar)
        }
//        fragmentKontoBinding.kontoUsun.setOnClickListener {
//            Log.d("komunikat", "listener działa")
//            deleteUser()
//        }
        val navigationView = binding.navView
        navigationView.bringToFront()
        navigationView.setNavigationItemSelectedListener(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    SecondActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                )
            }
        }
//        KTÓRY FRAGMENT MA SIĘ WYŚWIETLAĆ NA STARCIE
//        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, KontoFragment()).commit()
//        navigationView.setCheckedItem(R.id.nav_konto)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(this, "Przyznano uprawnienia", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Nie przyznano uprawnień", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_mapa -> {
                listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MapaFragment()).commit()
                drawer.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_logout -> {
                Firebase.auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Wylogowano", Toast.LENGTH_LONG).show()
                drawer.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_konto -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, KontoFragment()).commit()
                drawer.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_historia -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HistoriaFragment()).commit()
                drawer.closeDrawer(GravityCompat.START)
                return true
            }
            R.id.nav_oplac -> {

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}

fun tablicaMiejscaParkingowebaza(listaLokalizacji: ArrayList<KontoFragment.MiejsceParkingowe> = ArrayList()): ArrayList<KontoFragment.MiejsceParkingowe> {

    val database =
        FirebaseDatabase.getInstance("https://aplikacja-parkin-1620413734452-default-rtdb.europe-west1.firebasedatabase.app/")
    database.getReference("MiejsceParkingowe/").orderByChild("lokalizacja")
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (spotLatLng: DataSnapshot in dataSnapshot.children) {
                        listaLokalizacji.add(
                            KontoFragment.MiejsceParkingowe(
                                spotLatLng.child("stan").value.toString().toBoolean(),
                                spotLatLng.child("mNiPelSprawnych").value.toString().toBoolean(),
                                spotLatLng.child("idwlasciciela").value.toString(),
                                LatLng(
                                    spotLatLng.child("lokalizacja/latitude/").value.toString()
                                        .toDouble(),
                                    spotLatLng.child("lokalizacja/longitude/").value.toString()
                                        .toDouble()
                                ),
                                spotLatLng.child("cena").value.toString().toDouble()
                            )
                        )
                        Log.d("bazaDoTablcy", "${spotLatLng.key}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO "Not yet implemented"
            }
        })
    return listaLokalizacji
}

// Przyład dodawania
//var test =Zaparkowanie("testyidOsp","idmiejscaparkingopwego", LocalDateTime.now(),
//            LocalDateTime.now().plusHours(2),22.4);
//    test.dodawanieDobazy()
data class Zaparkowanie(
    var idOsobyParkujacej: String = "",
    var idMiejsceParkingowe: String = "",
    var startZaparkowania: LocalDateTime = LocalDateTime.now(),
    var koniecZaparkowania: LocalDateTime = LocalDateTime.now().plusHours(12),
    var koszt: Double = 0.0
) {
    fun dodawanieDobazy() {
        val database =
            FirebaseDatabase.getInstance("https://aplikacja-parkin-1620413734452-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("Zaparkowanie")

        val id = myRef.push().key // tu generuje następne id tabeli miejsce parkingowe
        myRef.child(id.toString()).setValue(
            Zaparkowanie(
                FirebaseAuth.getInstance().currentUser!!.uid,
                this.idMiejsceParkingowe,
                this.startZaparkowania,
                this.koniecZaparkowania,
                this.koszt
            )
        )
    }
}
