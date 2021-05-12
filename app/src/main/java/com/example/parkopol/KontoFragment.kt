package com.example.parkopol

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
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
    data class miejsceParkingowe(val stan: Boolean? = false,val mNiPelSprawnych: Boolean = false, val idwlasciciela: String, val szerokosc: String, val wysokosc: String,val cena: Double? = null){

    }

    private  fun nowyParking(){
        val database = FirebaseDatabase.getInstance("https://aplikacja-parkin-1620413734452-default-rtdb.europe-west1.firebasedatabase.app/")
      //  val myRef = database.getReference("MiejsceParkingowe")

        //myRef.setValue(miejscePar)
        database.getReference("MiejsceParkingowe").child("1").setValue(miejsceParkingowe(false,false,FirebaseAuth.getInstance().currentUser.uid,"51.7919374642711","16.869667087783448" ))

        database.getReference("MiejsceParkingowe").child("2").setValue(miejsceParkingowe(true,false,FirebaseAuth.getInstance().currentUser.uid,"51.7919374642000","16.869667087783000",1.2))
        Log.d("komunikat", "nowyParking")

    }
}