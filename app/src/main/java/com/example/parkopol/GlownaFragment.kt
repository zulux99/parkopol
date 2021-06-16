package com.example.parkopol

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.system.Os.bind
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.parkopol.databinding.ActivityMainBinding.bind
import com.example.parkopol.databinding.ActivitySecondBinding
import com.example.parkopol.databinding.FragmentGlownaBinding
import com.example.parkopol.databinding.FragmentKontoBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class GlownaFragment : Fragment() {
    private lateinit var glownaBinding: FragmentGlownaBinding
    private lateinit var secondBinding: ActivitySecondBinding
    var listaLokalizacji = ArrayList<DodajParkingFragment.MiejsceParkingowe>()
    private var listaZaparkowan = ArrayList<Zaparkowanie>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var czas=0;
        listaZaparkowan= tablicaZaparkowanie(listaZaparkowan)
        listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji);
        Log.d("glowna", "lista: $listaZaparkowan")
        val myView = inflater.inflate(R.layout.fragment_glowna, container, false)
        val zakonczParkowanieButton = myView.findViewById(R.id.secondZakonczZaparkowanie) as Button
        glownaBinding = FragmentGlownaBinding.bind(myView)
        secondBinding = ActivitySecondBinding.inflate(layoutInflater)
        val secondCzasZaparkowaniaTextView = myView.findViewById(R.id.secondCzasZaparkowania) as TextView
        val secondZaparkowanieOpisTextView = myView.findViewById(R.id.secondZaparkowanieOpis) as TextView
        val glownaZaparkujButton = myView.findViewById(R.id.glownaZaparkuj) as Button

        val textView9 = myView.findViewById(R.id.textView9) as TextView
        val idMP="";
        var startZaparkowanie=""



        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                var tempczas=listaZaparkowan.findLast { it.koniecZaparkowania == "0" }
                var x = "Brak informacji"
                if(tempczas!=null){

                    var opis= listaLokalizacji.findLast { it.idMParkingowego== tempczas.idMiejsceParkingowe}
                    if (opis!=null){
                        secondZaparkowanieOpisTextView.setText(opis.opis)
                        secondZaparkowanieOpisTextView.visibility = View.VISIBLE
                        secondZaparkowanieOpisTextView.visibility = View.VISIBLE
                        glownaBinding.glownaLogo.visibility = View.GONE
                        glownaZaparkujButton.visibility=View.GONE
                    }else{
                        secondZaparkowanieOpisTextView.setText("Brak informacji")
                    }

                }else{
                    secondZaparkowanieOpisTextView.setText("Brak informacji")
                }
                val formatter  =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN)

                if (tempczas!=null){
                    val a = formatter.parse(SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        Locale.GERMAN).format(Date()))
                    val b = formatter.parse(tempczas.startZaparkowania)
                    val tem =a!!.getTime() - b!!.getTime()
                    x=roznicaCzas(tem)
                    secondCzasZaparkowaniaTextView.visibility = View.VISIBLE
                    textView9.visibility = View.VISIBLE
                    zakonczParkowanieButton.visibility = View.VISIBLE
                }


                secondCzasZaparkowaniaTextView.setText(x)

                handler.postDelayed(this, 1000)
            }
        }, 0)
        zakonczParkowanieButton.setOnClickListener()
        {
            var temp = listaZaparkowan.findLast { it.koniecZaparkowania == "0" }
            if (temp!=null ) {

                val test =  listaLokalizacji.findLast { it.idMParkingowego == temp.idMiejsceParkingowe}
                var cena = 0.0
                var  start = "0"
                if (test!=null){
                    cena= test.cena!!

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
                Log.d("glowna", "lista: $listaZaparkowan")
                val intent = Intent(activity, SecondActivity::class.java)
                startActivity(intent)
            }else{
                listaZaparkowan.clear()
                listaZaparkowan = tablicaZaparkowanie(listaZaparkowan)
                Log.d("glowna", "brak")
                Toast.makeText(context, "nie zaparkowałeś", Toast.LENGTH_SHORT).show()
            }
        }
        glownaZaparkujButton.setOnClickListener()
        {
            listaZaparkowan = tablicaZaparkowanie(listaZaparkowan)

            Log.d("komunkat", "rozmiar"+ listaZaparkowan.size.toString()+" id:"+listaZaparkowan.findLast {it.koniecZaparkowania == "0"  }?.idZap)
            if (!(listaZaparkowan.any { it.koniecZaparkowania == "0" })){

                if (activity != null) {
                    activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MapaFragment()).commit()
                    val navigationView = secondBinding.navView
                    navigationView.setCheckedItem(R.id.nav_mapa)// TODO: nie zaznacza mapy
                }
                listaLokalizacji = tablicaMiejscaParkingowebaza(listaLokalizacji)
            } else {
                Toast.makeText(context, "Zaparkowałeś już", Toast.LENGTH_SHORT).show()
            }
        }
        return myView
    }

}
