package com.example.parkopol

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast


class GlownaFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myView = inflater.inflate(R.layout.fragment_glowna, container, false)
        val zakonczParkowanieButton = myView.findViewById(R.id.zakoncz_parkowanie) as Button
        zakonczParkowanieButton.setOnClickListener()
        {
            Toast.makeText(context, "Zakończono parkowanie", Toast.LENGTH_SHORT).show()
            zakonczParkowanieButton.visibility = View.GONE
        }
        return myView
    }

}