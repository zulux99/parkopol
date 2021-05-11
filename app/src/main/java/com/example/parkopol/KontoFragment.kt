package com.example.parkopol
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.parkopol.databinding.FragmentKontoBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class KontoFragment: Fragment() {
    private lateinit var binding: FragmentKontoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = FragmentKontoBinding.inflate(layoutInflater)
        binding.kontoUsun.setOnClickListener{
            deleteUser()
            Log.d("komunikat", "1")
        }
        return inflater.inflate(R.layout.fragment_konto, container, false)
    }

    private fun deleteUser() {
        Log.d("komunikat", "odp")
        // [START delete_user]
        val user = Firebase.auth.currentUser!!

        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("komunikat", "User account deleted.")
                }
            }
        // [END delete_user]
    }


}