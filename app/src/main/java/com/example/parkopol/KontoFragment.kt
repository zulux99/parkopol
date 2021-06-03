package com.example.parkopol

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AlertDialogLayout
import androidx.fragment.app.Fragment
import com.example.parkopol.databinding.FragmentKontoBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


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
            val dialogClickListener =
                DialogInterface.OnClickListener { _, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            deleteUser()
                            Firebase.auth.signOut()
                            Toast.makeText(context, "Twoje konto zostało usunięte", Toast.LENGTH_SHORT).show()
//            TODO: po dwukrotnym usunięciu i zalogowaniu się crashuje
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {
                        }
                    }
                }
            val builder = AlertDialog.Builder(context!!)
            builder.setMessage("Czy jesteś pewny że chcesz usunąć konto?").setPositiveButton("Tak", dialogClickListener)
                .setNegativeButton("Anuluj", dialogClickListener).show()
        }
        kontoBinding.dodajSamochod.setOnClickListener {
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


data class samochod(
    var idWlasciciela: String = "",
    var nrRejestracyjny: String = "",
    var nazwasamochod: String = "",
    var idSamochod: String? = "",
) {
    fun dodawanieDobazy() {
        val database =
            FirebaseDatabase.getInstance("https://aplikacja-parkin-1620413734452-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("Zaparkowanie")

        val id = myRef.push().key // tu generuje następne id tabeli miejsce parkingowe
        myRef.child(id.toString()).setValue(
            samochod(
                FirebaseAuth.getInstance().currentUser!!.uid,
                this.nrRejestracyjny,
                this.nazwasamochod,
                null
            )
        )
    }
}

fun tablicaSamochody(listaSamochody: ArrayList<samochod> = ArrayList()): ArrayList<samochod> {

    Log.d("tablicaSamochody", "1")
    val database =
        FirebaseDatabase.getInstance("https://aplikacja-parkin-1620413734452-default-rtdb.europe-west1.firebasedatabase.app/")
    database.getReference("samochod/").get()
        .addOnSuccessListener {
            Log.i("tablicaSamochody", "wartosci ${it.value}")
            if (it.exists()) {
                for (spotLatLng: DataSnapshot in it.children) {
                    if (FirebaseAuth.getInstance().currentUser!!.uid == spotLatLng.child("idOsobyParkujacej").value.toString()) {
                        listaSamochody.add(
                            samochod(
                                spotLatLng.child("idWlasciciela").value.toString(),
                                spotLatLng.child("nrRejestracyjny").value.toString(),
                                spotLatLng.child("nazwasamochod").value.toString(),
                                spotLatLng.key.toString()

                            )
                        )
                        Log.d("tablicaSamochody", "${spotLatLng.key}")
                    }
                }
            }

        }.addOnFailureListener {
            Log.e("tablicaSamochody", "bląd", it)
        }

    return listaSamochody
}