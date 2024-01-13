package com.example.ihealthdroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme
import java.util.Locale

class SettingsActivity : ComponentActivity() {

    var isSpinnerInitialized: Boolean = false

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
                    setContentView(R.layout.settings_layout)

                    val backToMenuBtn = findViewById<ImageButton>(R.id.back_to_menu)
                    backToMenuBtn.setOnClickListener {
                        finish()
                    }

                    val languageSpinner: Spinner = findViewById(R.id.language_switch)
                    val adapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.languages_array, android.R.layout.simple_spinner_item
                    )

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    languageSpinner.adapter = adapter
                    languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (isSpinnerInitialized) {
                                val selectedLanguage = position
                                val languageCode = when (selectedLanguage) {
                                    1 -> "en_US"
                                    2 -> "vi"
                                    else -> null // Return null for "Select Language" option
                                }
                                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("selectedLanguage", languageCode)
                                editor.apply()
                                restartApp()
                            } else {
                                isSpinnerInitialized = true
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // Do nothing
                        }
                    }
                }
            }
        }
    }

    private fun restartApp() {
        val packageManager = packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

}