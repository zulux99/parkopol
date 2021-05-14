package com.example.parkopol

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class KontoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val myView = inflater.inflate(R.layout.fragment_konto, container, false)
        val button = myView.findViewById(R.id.konto_usun) as Button
        button.setOnClickListener {
            Log.d("komunikat", "1")
            deleteUser()
            Firebase.auth.signOut()
//            TODO: po dwukrotnym usunięciu i zalogowaniu się crashuje
        }
        val nowyParkingbutton = myView.findViewById(R.id.nowyParking) as Button
        nowyParkingbutton.setOnClickListener {
            Log.d("komunikat", "nowyParking0")
            nowyParking()
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

    data class MiejsceParkingowe(
        val stan: Boolean? = false,
        val mNiPelSprawnych: Boolean = false,
        val idwlasciciela: String,
        val lokalizacja: LatLng,
        val cena: Double? = null
    ) {

        fun toMiejsceParkingowe(): Map<String, Any?> {
            return mapOf(
                "stan" to stan,
                "mNiPelSprawnych" to mNiPelSprawnych,
                "idwlasciciela" to idwlasciciela,
                "lokalizacja" to lokalizacja,
                "cena" to cena
            )

        }

    }

    private fun nowyParking() {
        val TAG = "baza"
        val database =
        FirebaseDatabase.getInstance("https://aplikacja-parkin-1620413734452-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("MiejsceParkingowe")

        //myRef.setValue(miejscePar)
        val id = myRef.push().key // tu generuje następne id tabeli miejsce parkingowe
        myRef.child(id.toString()).setValue(
            MiejsceParkingowe(
                false, false, FirebaseAuth.getInstance().currentUser!!.uid,
                LatLng(51.7919374642711, 16.869667087783448)
            )
        )
        //database.getReference("MiejsceParkingowe").child("2").setValue(MiejsceParkingowe(true,false,FirebaseAuth.getInstance().currentUser.uid,LatLng(51.7919374642000,16.869667087783000 ),1.2))
        Log.d("bazadanych", "nowyParking")

        myRef.get().addOnSuccessListener {
            val test = it.child("idwlasciciela")
            Log.d("bazadanych", "Got value ${test}  ++===++,${it.value}")
        }.addOnFailureListener {
            Log.d("bazadanych", "Error getting data", it)
        }


        val key = database.getReference("MiejsceParkingowe").push().key
        if (key == null) {
            Log.d("bazadanych", "Couldn't get push key for posts")

        } else {
            val test = MiejsceParkingowe(
                true, false, FirebaseAuth.getInstance().currentUser!!.uid,
                LatLng(51.7919374642000, 16.869667087783000)
            )
// myRef.child("1").setValue(null) usuwanie
            // myRef.setValue( test)
            // myRef.child("2").child("cena").setValue(20)
        }
    }
}