package com.example.parkopol

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast


class GlownaFragment : Fragment() {
    var listaLokalizacji = ArrayList<DodajParkingFragment.MiejsceParkingowe>()
    private var listaZaparkowan = ArrayList<Zaparkowanie>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        listaZaparkowan= tablicaZaparkowanie(listaZaparkowan)
        listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji);
        Log.d("glowna","lista: "+listaZaparkowan.toString())
        val myView = inflater.inflate(R.layout.fragment_glowna, container, false)
        val zakonczParkowanieButton = myView.findViewById(R.id.zakoncz_parkowanie) as Button
        zakonczParkowanieButton.setOnClickListener()
        {
            var temp = listaZaparkowan.findLast { it.koniecZaparkowania == "0" }
            if (temp!=null ) {

                val test =  listaLokalizacji.findLast { it.idMParkingowego == temp.idMiejsceParkingowe}
                var cena = 0.0
                var  start = "0"
                if (test!=null){
                    cena=test.cena

                }
                start=temp.startZaparkowania
                zmianaStanu(
                    temp.idMiejsceParkingowe,
                    false,
                    temp.idZap,
                    cena,
                    start
                )
                Toast.makeText(context, "Zakończono parkowanie", Toast.LENGTH_SHORT).show()
                listaZaparkowan.clear()
                listaZaparkowan = tablicaZaparkowanie(listaZaparkowan)
              //  listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji);
                Log.d("glowna", "lista: " + listaZaparkowan.toString())
                val intent = Intent(activity, SecondActivity::class.java)
                startActivity(intent)
            }else{
                listaZaparkowan.clear()
                listaZaparkowan = tablicaZaparkowanie(listaZaparkowan)
                Log.d("glowna", "brak")
                Toast.makeText(context, "nie zaparkowałeś", Toast.LENGTH_SHORT).show()
            }
        }
        return myView
    }

}
