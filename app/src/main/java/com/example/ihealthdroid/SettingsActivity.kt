package com.example.ihealthdroid

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
import setLocale

class SettingsActivity : ComponentActivity() {

    private val languages = arrayOf("Select Language" ,"Tiếng Việt", "English")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IHealthDroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    setContentView(R.layout.settings_layout)

                    val backToMenuBtn = findViewById<ImageButton>(R.id.back_to_menu)
                    backToMenuBtn.setOnClickListener {
                        finish()
                    }

                    val languageSpinner = findViewById<Spinner>(R.id.language_switch)
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    languageSpinner.adapter = adapter
                    languageSpinner.setSelection(0)

                    languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedLanguage = languages[position].toString()
                            if (selectedLanguage == "Tiếng Việt") {
                                setLocale(this@SettingsActivity,"vi")
                                finish()
                                startActivity(intent)
                            } else if (selectedLanguage == "English") {
                                setLocale(this@SettingsActivity,"en")
                                finish()
                                startActivity(intent)
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            //Do nothing
                        }
                    }
                }
            }
        }
    }
}