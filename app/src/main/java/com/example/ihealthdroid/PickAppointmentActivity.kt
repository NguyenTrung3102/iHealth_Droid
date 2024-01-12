package com.example.ihealthdroid

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class PickAppointmentActivity : ComponentActivity() {

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
                    setContentView(R.layout.pick_appointment_layout)

                    val backToMenuBtn = findViewById<ImageButton>(R.id.back_to_menu)
                    backToMenuBtn.setOnClickListener {
                        finish()
                    }

                    val btnSelect = findViewById<Button>(R.id.btn_select)
                    val selectEditField = findViewById<EditText>(R.id.edit_tv_select)

                    btnSelect.setOnClickListener{
                        val phone = selectEditField.text.toString()

                        val db = Firebase.firestore
                        val docref = db.collection("profiles").document("$phone")

                        docref.get().addOnSuccessListener { documentSnapshot ->

                            if(documentSnapshot.exists()) {

                                //Get data
                                val name = documentSnapshot.getString("name")
                                val birthDate = documentSnapshot.getString("dob")
                                val phoneNum = documentSnapshot.getString("phone")
                                val province = documentSnapshot.getString("province")

                                // Clear previous text to prevent the same button is pressed accidentally


                                //Set data for TextView
                                val showName = findViewById<TextView>(R.id.tv_show_name)
                                showName.text = name

                                val showBirthDay = findViewById<TextView>(R.id.tv_show_date)
                                showBirthDay.text = this.getString(R.string.date_of_birth)
                                showBirthDay.text = String.format("%s %s", showBirthDay.text, birthDate)

                                val showPhone = findViewById<TextView>(R.id.tv_show_phone)
                                showPhone.text = this.getString(R.string.phone_num)
                                showPhone.text = String.format("%s %s", showPhone.text, phoneNum)

                                val showProvince = findViewById<TextView>(R.id.tv_show_province)
                                showProvince.text = this.getString(R.string.province)
                                showProvince.text = String.format("%s %s", showProvince.text, province)


                            } else {
                                //Handle the case where the document does not exist
                                Log.d("TAG", "Profile not found ")
                            }

                        }.addOnFailureListener { exception ->
                            //Handle Error
                            Log.d("TAG", "get failed with ", exception)
                        }

                    }
                }
            }
        }
    }
}