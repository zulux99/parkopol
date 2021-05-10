package com.example.parkopol

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.parkopol.databinding.ActivitySecondBinding
import com.example.parkopol.databinding.NavHeaderBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class SecondActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var binding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        val headerBinding = NavHeaderBinding.bind(binding.navView.getHeaderView(0))
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
            Log.d("komunikat", "google avatar url: " + signInAccount.photoUrl.toString())
            Picasso.get().load(signInAccount.photoUrl.toString()).into(headerBinding.navAvatar)
        }
        val navigationView = binding.navView
        navigationView.bringToFront()
        navigationView.setNavigationItemSelectedListener(this)
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
            R.id.nav_logout -> {
                Toast.makeText(this, "Wylogowano", Toast.LENGTH_LONG).show()
                Firebase.auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.nav_konto -> {

                return true
            }
            R.id.nav_historia -> {

                return true
            }
            R.id.nav_oplac -> {

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}

