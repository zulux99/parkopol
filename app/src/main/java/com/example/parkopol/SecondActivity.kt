package com.example.parkopol

import android.R
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.parkopol.databinding.ActivitySecondBinding
import com.example.parkopol.databinding.NavHeaderBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL


class SecondActivity : AppCompatActivity() {
    private lateinit var drawer: DrawerLayout
    private lateinit var binding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        val headerBinding = NavHeaderBinding.bind(binding.navView.getHeaderView(0))
        setContentView(binding.root)
        Toast.makeText(this, "Zalogowano", Toast.LENGTH_LONG).show()
        val signInAccount: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        drawer = binding.drawerLayout
        val toogle = ActionBarDrawerToggle(this, drawer, binding.toolbar,
            R.string.unknownName, R.string.unknownName)
        drawer.addDrawerListener(toogle)
        toogle.syncState()
        if(signInAccount != null){
            headerBinding.imieNazwisko.text = signInAccount.displayName
            headerBinding.adresEmail.text = signInAccount.email
        }


//
//        binding.wyloguj.setOnClickListener {
//            Firebase.auth.signOut()
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            Toast.makeText(this, "Wylogowano", Toast.LENGTH_LONG).show()
//        }
    }
    override fun onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        } else{
            super.onBackPressed()
        }
    }
}