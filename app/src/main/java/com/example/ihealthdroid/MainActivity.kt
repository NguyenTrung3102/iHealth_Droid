package com.example.ihealthdroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import java.util.Locale

class MainActivity : ComponentActivity() {
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
                    setContentView(R.layout.main_menu_layout)

                    val languageCode = intent.getStringExtra("LANGUAGE_CODE")

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
                        val intent = Intent(this@MainActivity, HealthWikiActivity::class.java)
                        startActivity(intent)
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

}