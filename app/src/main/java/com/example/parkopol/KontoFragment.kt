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
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class KontoFragment : Fragment() {
    private var listaLokalizacji = ArrayList<MiejsceParkingowe>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji)
        val myView = inflater.inflate(R.layout.fragment_konto, container, false)
        val button = myView.findViewById(R.id.konto_usun) as Button
        val inputLokalicacja1 = myView.findViewById(R.id.Lokalizacja_1) as EditText
        val inputLokalicacja2 = myView.findViewById(R.id.lokalicacja2) as EditText
        val niepelnosprawni = myView.findViewById(R.id.kontoCheckBoxNiePS) as CheckBox
        val inputOpis = myView.findViewById(R.id.kontoOpis) as EditText
        val inputCena = myView.findViewById(R.id.kontoCena) as EditText
        button.setOnClickListener {
            Log.d("komunikat", "1")
            deleteUser()
            Firebase.auth.signOut()
//            TODO: po dwukrotnym usunięciu i zalogowaniu się crashuje
        }
        val nowyParkingbutton = myView.findViewById(R.id.nowyParking) as Button
        nowyParkingbutton.setOnClickListener {
            Log.d("komunikat", "nowyParking0")
            nowyParking(
                inputLokalicacja1.text.toString().toDouble(),
                inputLokalicacja2.text.toString().toDouble(),
                niepelnosprawni.isChecked,
                inputOpis.text.toString(),
                //  inputCena.text.toString(),
            )

//            Log.d("t",inputCena.text.toString().toDouble().toString())
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
        var stan: Boolean? = false,
        var mNiPelSprawnych: Boolean? = false,
        var idwlasciciela: String? = "",
        var lokalizacja: LatLng? = LatLng(0.0, 0.0),
        var cena: Double? = 0.0,
        var opis: String? = ""
    ) {
        fun toMiejsceParkingowe(): Map<String, Any?> {
            return mapOf(
                "stan" to stan,
                "mNiPelSprawnych" to mNiPelSprawnych,
                "idwlasciciela" to idwlasciciela,
                "lokalizacja" to lokalizacja,
                "cena" to cena,
                "opis" to opis
            )
        }
    }

    private fun nowyParking(
        inputLokalicacja1: Double,
        inputLokalicacja2: Double,
        niepelnosprawni: Boolean,
        inputOpis: String,
        //inputCena: String,
    ) {
        val database =
            FirebaseDatabase.getInstance("https://aplikacja-parkin-1620413734452-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("MiejsceParkingowe")
        val lokalicajca = LatLng(inputLokalicacja1, inputLokalicacja2)
        for (i in listaLokalizacji) {
            Log.d("tablica", "tablica0: ${i.idwlasciciela} ")
            if (lokalicajca == i.lokalizacja) {
                Log.e("tablica", "nie dodawanie")
                return
            }
        }
        Log.e("baza", "}}}}}}}}}}}}}}}}}}")
        //myRef.setValue(miejscePar)
        Log.e("baza", "dodaje")
        val id = myRef.push().key // tu generuje następne id tabeli miejsce parkingowe
        myRef.child(id.toString()).setValue(
            MiejsceParkingowe(
                false,
                niepelnosprawni,
                FirebaseAuth.getInstance().currentUser!!.uid,
                lokalicajca,
                0.1,
                inputOpis
            )
        )
        listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji)
    }
}