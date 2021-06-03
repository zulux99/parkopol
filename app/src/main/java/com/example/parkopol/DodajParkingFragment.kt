package com.example.parkopol

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DodajParkingFragment : Fragment() {
    private var listaLokalizacji = ArrayList<MiejsceParkingowe>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myView = inflater.inflate(R.layout.fragment_dodaj_parking, container, false)
        listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji)
        val kontoLokalicacja = myView.findViewById(R.id.kontoLokalicacja) as EditText
        val niepelnosprawni = myView.findViewById(R.id.kontoCheckBoxNiePS) as CheckBox
        val inputOpis = myView.findViewById(R.id.kontoOpis) as EditText
        val inputCena = myView.findViewById(R.id.kontoCena) as EditText
        val nowyParkingbutton = myView.findViewById(R.id.nowyParking) as Button

        nowyParkingbutton.setOnClickListener {
        var ok = true
        val lokalicajca = LatLng(0.0,0.0)
            if (kontoLokalicacja.text.toString().length==0){
                ok=false
                Toast.makeText(context, "lokalizacja jest puste", Toast.LENGTH_SHORT).show()
            }else{
                val partkontoLokalicacja= kontoLokalicacja.text.toString().split(",")
                if (partkontoLokalicacja.size==1){
                    Toast.makeText(context, "lokalizacja jest wpisana w nie prawidłowy sposób", Toast.LENGTH_SHORT).show()
                    ok=false
                }
            }
            if (inputOpis.text.toString().length==0){
                ok=false
                Toast.makeText(context, "opis jest puste", Toast.LENGTH_SHORT).show()

            }


            if (ok ) {
                val opis=inputOpis.text.toString()
                var cena  =0.0
                if (inputCena.text.toString().length!=0){
                    cena =inputCena.text.toString().toDouble()
                }

                 //Log.e("tablica", "lok:"+lokalicajca.toString()+" nie:"+niepelnosprawni.toString()+"opis:"+inputOpis.toString()+"opis:"+inputCena.toString())
                val partkontoLokalicacja= kontoLokalicacja.text.toString().split(",")
                val lokalicajca = LatLng(
                    partkontoLokalicacja[0].toDouble(),
                    partkontoLokalicacja[1].toDouble()
                )
                nowyParking(
                    lokalicajca,
                    niepelnosprawni.isChecked,
                    opis,
                    cena
                )
            }

          // Log.d("t",inputCena.text.toString().toDouble().toString())
        }
        return myView
    }

    data class MiejsceParkingowe(
        var stan: Boolean? = false,
        var mNiPelSprawnych: Boolean? = false,
        var idwlasciciela: String? = "",
        var lokalizacja: LatLng? = LatLng(0.0, 0.0),
        var cena: Double? = 0.0,
        var opis: String? = "",
        var idMParkingowego: String? = "",
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
        lokalicacja: LatLng,
        niepelnosprawni: Boolean,
        inputOpis: String,
        inputCena: Double,
    ) {
       // Log.e("tablica", "lok:"+kontoLokalicacja.toString()+" nie:"+niepelnosprawni.toString()+"opis:"+inputOpis.toString()+"opis:"+inputCena.toString())


        val database =
            FirebaseDatabase.getInstance("https://aplikacja-parkin-1620413734452-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("MiejsceParkingowe")
    //        val partkontoLokalicacja= kontoLokalicacja.split(",")
    //        if (partkontoLokalicacja[1]==null){
    //            return
    //        }
    //        val lokalicajca = LatLng(partkontoLokalicacja[0].toDouble(), partkontoLokalicacja[1].toDouble())
        Log.e("tablica", "lokalicajcaja"+lokalicacja.toString())
        for (i in listaLokalizacji) {
            Log.d("tablica", "tablica0: ${i.idwlasciciela} ")
            if (lokalicacja == i.lokalizacja) {
                Log.e("tablica", "nie dodawanie")
                Toast.makeText(context, "Taka Lokalizacja już istnieje", Toast.LENGTH_SHORT).show()
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
                lokalicacja,
                inputCena,
                inputOpis
            )
        )

        Toast.makeText(context, "Dodano pomyslnie", Toast.LENGTH_SHORT).show()
        listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji)
    }

}