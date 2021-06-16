package com.example.parkopol

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.widget.EditText
import android.widget.ListView
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
    private var listaSamochod = ArrayList<Samochod>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val tablicaSamochody = ArrayList<String>()
        listaSamochod= tablicaSamochody()

        val listaSamochody: ArrayList<Samochod> = ArrayList()
        val myView = inflater.inflate(R.layout.fragment_konto, container, false)
        val ListaSamochody: ListView = myView.findViewById(R.id.kontoListaSamochody)
        kontoBinding = FragmentKontoBinding.bind(myView)
        val usunKontoButton = myView.findViewById(R.id.konto_usun) as Button
        val zapisz_samochodButton = myView.findViewById(R.id.zapisz_samochod) as Button
        val samochod_nazwaInPut = myView.findViewById(R.id.samochod_nazwa) as EditText
        val samochod_nRejInPut = myView.findViewById(R.id.samochod_nr_rejestracyjny) as EditText
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
        zapisz_samochodButton.setOnClickListener{
            var ok = true;
            if (samochod_nRejInPut.text.toString().length==0){
                ok=false
                Toast.makeText(context, "Nr rejestracyjny jest pusty", Toast.LENGTH_SHORT).show()
            }
            if (samochod_nazwaInPut.text.toString().length==0){
                ok=false
                Toast.makeText(context, "Nazwa jest pusta", Toast.LENGTH_SHORT).show()
            }
            if(ok){
                val samNazwa= samochod_nazwaInPut.text.toString()
                val samRej= samochod_nRejInPut.text.toString()
                Samochod(FirebaseAuth.getInstance().currentUser!!.uid,samRej,samNazwa,"").dodawanieDobazy()

                tablicaSamochody.add("$samNazwa $samRej")
                        val arrayAdapter = ArrayAdapter(context!!, R.layout.custom_textview, tablicaSamochody)
                       ListaSamochody.adapter = arrayAdapter
                samochod_nRejInPut.setText("")
                samochod_nazwaInPut.setText("")
                Toast.makeText(context, "Samochód został Dodany", Toast.LENGTH_SHORT).show()
                kontoBinding.samochodNazwa.visibility = View.GONE
                kontoBinding.samochodNrRejestracyjny.visibility = View.GONE
                kontoBinding.zapiszSamochod.visibility = View.GONE
        }
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
        Log.d("tablicaSamochody", "1")
        val database =
            FirebaseDatabase.getInstance(BuildConfig.BAZADANYCHLINK)
        database.getReference("samochod/").get()
            .addOnSuccessListener {
                Log.i("tablicaSamochody", "wartosci ${it.value}")
                if (it.exists()) {
                    for (spotLatLng: DataSnapshot in it.children) {
                        if (FirebaseAuth.getInstance().currentUser!!.uid == spotLatLng.child("idWlasciciela").value.toString()) {

                            tablicaSamochody.add(
                                spotLatLng.child(
                                    "nazwasamochod"
                                ).value.toString() + " " +
                                        spotLatLng.child("nrRejestracyjny").value.toString()
                            )
                        }
                            Log.d("tablicaSamochody", "${spotLatLng.key}")

                    }
                    val arrayAdapter = ArrayAdapter(context!!, R.layout.custom_textview, tablicaSamochody)
                    ListaSamochody.adapter = arrayAdapter
                }

            }.addOnFailureListener {
                Log.e("tablicaSamochody", "bląd", it)
            }


//        tablicaSamochody.add("nazwasamochod | nrRejestracyjny")
//        val arrayAdapter = ArrayAdapter(context!!, R.layout.custom_textview, tablicaSamochody)
//        ListaSamochody.adapter = arrayAdapter
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










data class Samochod(
    var idWlasciciela: String="",
    var nrRejestracyjny: String="",
    var nazwasamochod: String="",
    var idSamochod: String? ="",
){
fun dodawanieDobazy(){
    val database =
        FirebaseDatabase.getInstance(BuildConfig.BAZADANYCHLINK)
    val myRef = database.getReference("samochod")

    val id = myRef.push().key // tu generuje następne id tabeli miejsce parkingowe
    myRef.child(id.toString()).setValue(
        Samochod(
            this.idWlasciciela,
            this.nrRejestracyjny,
            this.nazwasamochod,
            null
        )
    )
}
}

fun tablicaSamochody( ): ArrayList<Samochod> {
    val listaSamochody: ArrayList<Samochod> = ArrayList()
    Log.d("tablicaSamochody", "1")
    val database =
        FirebaseDatabase.getInstance(BuildConfig.BAZADANYCHLINK)
    database.getReference("samochod/").get()
        .addOnSuccessListener {
            Log.i("tablicaSamochody", "wartosci ${it.value}")
            if (it.exists()) {
                for (spotLatLng: DataSnapshot in it.children) {
                       listaSamochody.add(
                            Samochod(
                                spotLatLng.child("idWlasciciela").value.toString(),
                                spotLatLng.child("nrRejestracyjny").value.toString(),
                                spotLatLng.child("nazwasamochod").value.toString(),
                                spotLatLng.key.toString()

                            )
                        )
                        Log.d("tablicaSamochody", "${spotLatLng.key}")

                }
            }

        }.addOnFailureListener {
            Log.e("tablicaSamochody", "bląd", it)
        }

    return listaSamochody
}