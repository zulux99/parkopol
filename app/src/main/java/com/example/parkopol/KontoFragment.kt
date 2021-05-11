package com.example.parkopol
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.parkopol.databinding.FragmentKontoBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class KontoFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        var myView = inflater.inflate(R.layout.fragment_konto, container, false)
        var btn_test = myView.findViewById(R.id.konto_usun) as Button
        btn_test.setOnClickListener {
            Log.d("komunikat", "1")
        }
        return myView
    }

    private fun deleteUser() {
        Log.d("komunikat", "odp")
        // [START delete_user]
//        val user = Firebase.auth.currentUser!!
//
//        user.delete()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Log.d("komunikat", "User account deleted.")
//                }
//            }
        // [END delete_user]
    }


}