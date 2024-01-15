package com.example.ihealthdroid

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.service.controls.ControlsProviderService
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ihealthdroid.adapter.CustomEmergencyAdapter
import com.example.ihealthdroid.objectModel.EmergencyModel
import com.example.ihealthdroid.objectModel.ProfileModel
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class EmergencyActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomEmergencyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPreferences.getString("selectedLanguage", "en_us")

        val locale = selectedLanguage?.let { Locale(it) }
        if (locale != null) {
            Locale.setDefault(locale)
        }

        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        val context = createConfigurationContext(configuration)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        setContent {
            IHealthDroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val updatedContext = context.createConfigurationContext(configuration)
                    setContentView(R.layout.emergency_layout)

                    val backToMenuBtn = findViewById<ImageButton>(R.id.back_to_menu)
                    backToMenuBtn.setOnClickListener {
                        val intent = Intent(this@EmergencyActivity, MainActivity::class.java)
                        startActivity(intent)
                    }

                    var emergencyLocale = ""

                    Log.d(TAG, "$selectedLanguage")
                    emergencyLocale = if (selectedLanguage == "en_US") {
                        "emergency"
                    } else {
                        "emergency-vi"
                    }

                    recyclerView = findViewById(R.id.list_emergency)
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    adapter = CustomEmergencyAdapter()
                    recyclerView.adapter = adapter

                    adapter.setOnItemClickListener { emergency ->
                        showEmergencyDetail(emergency)
                    }

                    getEmergencyFromFirestore(emergencyLocale)
                }
            }
        }
    }


    private fun showEmergencyDetail(emergency: EmergencyModel) {
        val callPhone = emergency.phone
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$callPhone")
        startActivity(intent)
    }

    private fun getEmergencyFromFirestore(emergencyLocale: String) {

        val db = FirebaseFirestore.getInstance()
        val emergencyCollection = db.collection(emergencyLocale)

        emergencyCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                val emergencyList = mutableListOf<EmergencyModel>()

                for (document in querySnapshot.documents) {
                    val emergency = document.toObject(EmergencyModel::class.java)
                    if (emergency != null) {
                        Log.d(TAG,"$emergency")
                        emergencyList.add(emergency)
                    }
                }

                Log.d(ControlsProviderService.TAG, "Retrieved ${emergencyList.size} profiles from Firestore")

                adapter.submitList(emergencyList)
            }
            .addOnFailureListener { exception ->
                Log.e(ControlsProviderService.TAG, "Error retrieving profiles from Firestore", exception)
            }
    }
}