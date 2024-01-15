package com.example.ihealthdroid

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class MainActivity : ComponentActivity() {
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
                    setContentView(R.layout.main_menu_layout)

                    val mainMenuUserInfoField = findViewById<TextView>(R.id.main_menu_user_email)

                    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

                    val db = Firebase.firestore
                    if (currentUserUid != null) {
                        db.collection("accounts")
                            .document(currentUserUid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val accountName = document.getString("displayName")
                                    mainMenuUserInfoField.text = accountName
                                } else {
                                    Log.d(ControlsProviderService.TAG, "No account found")
                                }
                            }
                            .addOnFailureListener {
                                Log.d(ControlsProviderService.TAG, "Error retrieving profiles from Firestore")
                            }
                    }

                    val medicalProfileBtn = findViewById<ImageButton>(R.id.btn_profile)
                    medicalProfileBtn.setOnClickListener {
                        val intent = Intent(this@MainActivity, MedicalProfileActivity::class.java)
                        startActivity(intent)
                    }

                    val pickAppointmentBtn = findViewById<ImageButton>(R.id.btn_schedual)
                    pickAppointmentBtn.setOnClickListener {
                        val intent = Intent(this@MainActivity, PickAppointmentActivity::class.java)
                        startActivity(intent)
                    }

                    val emergencyBtn = findViewById<ImageButton>(R.id.btn_phone)
                    emergencyBtn.setOnClickListener {
                        val intent = Intent(this@MainActivity, EmergencyActivity::class.java)
                        startActivity(intent)
                    }

                    val healthWikiBtn = findViewById<ImageButton>(R.id.btn_search)
                    healthWikiBtn.setOnClickListener {
                        convertWeb()
                    }

                    val signOutBtn = findViewById<Button>(R.id.sign_out_btn)
                    signOutBtn.setOnClickListener {
                        val intent = Intent(this@MainActivity, AccountSignInActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }

    private fun convertWeb (){
        val url = "https://suckhoedoisong.vn"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}