package com.example.parkopol

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import com.example.parkopol.databinding.ActivitySecondBinding
import com.example.parkopol.databinding.FragmentKontoBinding
import com.example.parkopol.databinding.NavHeaderBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class KontoFragment : Fragment() {
    private lateinit var kontoBinding: FragmentKontoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myView = inflater.inflate(R.layout.fragment_konto, container, false)
        kontoBinding = FragmentKontoBinding.bind(myView)
        val usunKontoButton = myView.findViewById(R.id.konto_usun) as Button
        usunKontoButton.setOnClickListener {
            Log.d("komunikat", "1")
            deleteUser()
            Firebase.auth.signOut()
//            TODO: po dwukrotnym usunięciu i zalogowaniu się crashuje
        }
        kontoBinding.dodajSamochod.setOnClickListener {
            Toast.makeText(context, "Teścik", Toast.LENGTH_SHORT).show()
            if (kontoBinding.zapiszSamochod.visibility == View.GONE) {
                kontoBinding.samochodNazwa.visibility = View.VISIBLE
                kontoBinding.samochodNrRejestracyjny.visibility = View.VISIBLE
                kontoBinding.zapiszSamochod.visibility = View.VISIBLE
            } else {
                kontoBinding.samochodNazwa.visibility = View.GONE
                kontoBinding.samochodNrRejestracyjny.visibility = View.GONE
                kontoBinding.zapiszSamochod.visibility = View.GONE
            }
        }
        val signInAccount: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(context)
        if (signInAccount != null) {
            kontoBinding.imieNazwisko.text = signInAccount.displayName
            kontoBinding.adresEmail.text = signInAccount.email
            Log.d("komunikat", "google avatar url: " + signInAccount.photoUrl.toString())
            Picasso.get().load(signInAccount.photoUrl.toString()).into(kontoBinding.navAvatar)
        }
        return myView
    }

    private fun deleteUser() {
        FirebaseAuth.getInstance().currentUser!!.delete().addOnSuccessListener {
            Log.d("komunikat", "Usunięto")
            val intent = Intent(SecondActivity(), MainActivity::class.java)
            startActivity(intent)
        }.addOnFailureListener {
            Log.d("komunikat", "Nie udało się usunąć")
        }
    }
}