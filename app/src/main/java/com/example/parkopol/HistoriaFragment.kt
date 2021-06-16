package com.example.parkopol
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoriaFragment: Fragment() {
     var listaZaparkowan = ArrayList<Zaparkowanie>()
    private var listaLokalizacji = ArrayList<DodajParkingFragment.MiejsceParkingowe>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("komunikatHis", "Start")
        listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji)
        val myView = inflater.inflate(R.layout.fragment_historia, container, false)
        val listaParkowan: ListView = myView.findViewById(R.id.lista_parkowan)
        val tablicaParkowan = ArrayList<String>()
        val formatter  =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN)

        val database =
            FirebaseDatabase.getInstance(BuildConfig.BAZADANYCHLINK)
        database.getReference("Zaparkowanie/").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    for (spotLatLng: DataSnapshot in it.children) {
                        if (FirebaseAuth.getInstance().currentUser!!.uid == spotLatLng.child("idOsobyParkujacej").value.toString()&&spotLatLng.child("koniecZaparkowania").value.toString()!="0") {
                        Log.d("histora","zacz")
                            val startZaparkowania = formatter.parse(spotLatLng.child("startZaparkowania").value.toString())
                            val koniecZaparkowania = formatter.parse(spotLatLng.child("koniecZaparkowania").value.toString())
                            val tem =koniecZaparkowania!!.getTime() - startZaparkowania!!.getTime()
                            var opis = "Brak Opisu"
                            val idMPar =spotLatLng.child("idMiejsceParkingowe").value.toString()
                            val test =  listaLokalizacji.findLast { it.idMParkingowego == idMPar }
                            if (test!=null){
                                opis=test.opis.toString()
                            }
                            tablicaParkowan.add(roznicaCzas(tem)+" "+spotLatLng.child("koszt").value.toString()+" zł "+opis)

                        }
                    }
                    var arrayAdapter = ArrayAdapter(context!!, R.layout.custom_textview, tablicaParkowan)
                    listaParkowan.adapter = arrayAdapter
                }

            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }


        var arrayAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, tablicaParkowan)
        listaParkowan.adapter = arrayAdapter
        Log.e("komunikatHis", "rozmiar:"+listaZaparkowan.size.toString())
        return myView
    }
}

fun roznicaCzas(czas : Long): String {
    var wyswietl: String=""
    if (czas/1000/60<60){
        wyswietl=(czas/1000/60).toString()+"Min"
        return wyswietl

    }else{
        if (czas/1000/60%60<10) {
            wyswietl =
                ((czas / 1000 / 60 / 60)).toString() + "H 0" + ((czas / 1000 / 60) % 60).toString() + "Min"
        }else{
            wyswietl =
                ((czas / 1000 / 60 / 60)).toString() + "H " + ((czas / 1000 / 60) % 60).toString() + "Min"
        }
    }
    return wyswietl
}