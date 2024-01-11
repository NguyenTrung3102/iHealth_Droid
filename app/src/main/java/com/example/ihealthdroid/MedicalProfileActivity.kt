package com.example.ihealthdroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class MedicalProfileActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val selectedLanguage = sharedPreferences.getString("selectedLanguage", "en_us")

        val locale = Locale(selectedLanguage)
        Locale.setDefault(locale)

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
                    setContentView(R.layout.medical_profile_layout)

                    val db = Firebase.firestore

                    val backToMenuBtn = findViewById<ImageButton>(R.id.back_to_menu)
                    backToMenuBtn.setOnClickListener {
                        val intent = Intent(this@MedicalProfileActivity, MainActivity::class.java)
                        startActivity(intent)
                    }

                    val addProfileButton = findViewById<ImageButton>(R.id.add_new_btn)
                    addProfileButton.setOnClickListener {
                        val intent = Intent(this@MedicalProfileActivity, CreateProfileActivity::class.java)
                        startActivity(intent)
                    }

                    recyclerView = findViewById(R.id.profile_list)
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    adapter = CustomProfileAdapter()
                    recyclerView.adapter = adapter

                    getProfilesFromFirestore()
                }
            }
        }
    }
    private fun getProfilesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val profilesCollection = db.collection("profiles")

        profilesCollection.get().addOnSuccessListener { documents ->
            val profileList = mutableListOf<ProfileModel>()

            for (document in documents) {
                val profile = document.toObject(ProfileModel::class.java)
                profileList.add(profile)
            }

            Log.d(TAG, "Retrieved ${profileList.size} profiles from Firestore") // Add this logging statement

            adapter.submitList(profileList)
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error retrieving profiles from Firestore", exception) // Add this logging statement
        }
    }

    private fun showProfileDetail(profile: ProfileModel) {
        val intent = Intent(this, ProfileDetailActivity::class.java)
        intent.putExtra("profile", profile)
        startActivity(intent)
    }
}
