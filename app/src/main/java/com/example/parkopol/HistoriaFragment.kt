package com.example.parkopol
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class HistoriaFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myView = inflater.inflate(R.layout.fragment_historia, container, false)
        val listaParkowan: ListView = myView.findViewById(R.id.lista_parkowan)
        val tablicaParkowan = ArrayList<String>()
        tablicaParkowan.add("Testowe 1 \n testowe halo")
        tablicaParkowan.add("Testowe 2")
        tablicaParkowan.add("Testowe 3")
        tablicaParkowan.add("Testowe 4")
        tablicaParkowan.add("Testowe 5")
        tablicaParkowan.add("Testowe 6")
        tablicaParkowan.add("Testowe 7")
        tablicaParkowan.add("Testowe 8")
        tablicaParkowan.add("Testowe 9")
        tablicaParkowan.add("Testowe 1")
        tablicaParkowan.add("Testowe 2")
        tablicaParkowan.add("Testowe 3")
        tablicaParkowan.add("Testowe 4")
        tablicaParkowan.add("Testowe 5")
        tablicaParkowan.add("Testowe 6")
        tablicaParkowan.add("Testowe 7")
        tablicaParkowan.add("Testowe 8")
        tablicaParkowan.add("Testowe 9")
        tablicaParkowan.add("Testowe 1")
        tablicaParkowan.add("Testowe 2")
        tablicaParkowan.add("Testowe 3")
        tablicaParkowan.add("Testowe 4")
        tablicaParkowan.add("Testowe 5")
        tablicaParkowan.add("Testowe 6")
        tablicaParkowan.add("Testowe 7")
        tablicaParkowan.add("Testowe 8")
        tablicaParkowan.add("Testowe 9")
        tablicaParkowan.add("Testowe 1")
        tablicaParkowan.add("Testowe 2")
        tablicaParkowan.add("Testowe 3")
        tablicaParkowan.add("Testowe 4")
        tablicaParkowan.add("Testowe 5")
        tablicaParkowan.add("Testowe 6")
        tablicaParkowan.add("Testowe 7")
        tablicaParkowan.add("Testowe 8")
        tablicaParkowan.add("Testowe 9")
        var arrayAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, tablicaParkowan)
        listaParkowan.adapter = arrayAdapter
        return myView
    }
}
