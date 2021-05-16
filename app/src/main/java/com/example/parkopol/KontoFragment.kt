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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class KontoFragment : Fragment() {
   private var listaLokalizacji = ArrayList<MiejsceParkingowe>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        tablicaMiejscaParkingowebaza()


        val myView = inflater.inflate(R.layout.fragment_konto, container, false)
        val button = myView.findViewById(R.id.konto_usun) as Button
        val inputLokalicacja1 = myView.findViewById(R.id.Lokalizacja_1) as EditText
        val inputLokalicacja2 = myView.findViewById(R.id.lokalicacja2) as EditText
        val niepelnosprawni= myView.findViewById(R.id.kontoCheckBoxNiePS) as CheckBox
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
                niepelnosprawni.isChecked
            )
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
        var idwlasciciela: String?="",
        var lokalizacja: LatLng?=LatLng(0.0,0.0),
        var cena: Double? = 0.0
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

    fun  tablicaMiejscaParkingowebaza(){

        val database =
            FirebaseDatabase.getInstance("https://aplikacja-parkin-1620413734452-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("MiejsceParkingowe")

        database.getReference("MiejsceParkingowe/").
        orderByChild("lokalizacja").
        addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (spotLatLng: DataSnapshot in dataSnapshot.children) {
                        listaLokalizacji.add(MiejsceParkingowe(
                            spotLatLng.child("stan").value.toString().toBoolean(),
                            spotLatLng.child("mNiPelSprawnych").value.toString().toBoolean(),
                            spotLatLng.child("idwlasciciela").value.toString(),
                            LatLng(spotLatLng.child("lokalizacja/latitude/").value.toString().toDouble(),
                                spotLatLng.child("lokalizacja/longitude/").value.toString().toDouble()),
                            spotLatLng.child("cena").value.toString().toDouble() ))
                       Log.d("bazaDoTablcy","${spotLatLng.key}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO "Not yet implemented"
            }
        })
    }










    private fun nowyParking(
        inputLokalicacja1: Double,
        inputLokalicacja2: Double,
        niepelnosprawni: Boolean
    ) {
        val database =
            FirebaseDatabase.getInstance("https://aplikacja-parkin-1620413734452-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("MiejsceParkingowe")


        var lokalicajca = LatLng(inputLokalicacja1, inputLokalicacja2)
            for (i in listaLokalizacji){

            Log.d("tablica", "tablica0: ${i.idwlasciciela} ")

                if(lokalicajca==i.lokalizacja){
                    Log.e("tablica", "nie dodawanie")
                    return ;
                }
        }

        Log.e("baza", "}}}}}}}}}}}}}}}}}}")
        //myRef.setValue(miejscePar)

               Log.e("baza", "dodaje")
                val id = myRef.push().key // tu generuje następne id tabeli miejsce parkingowe
                myRef.child(id.toString()).setValue(
                    MiejsceParkingowe(
                        false, niepelnosprawni, FirebaseAuth.getInstance().currentUser!!.uid,
                        lokalicajca,0.0
                    )
                )

        tablicaMiejscaParkingowebaza()

    }
}