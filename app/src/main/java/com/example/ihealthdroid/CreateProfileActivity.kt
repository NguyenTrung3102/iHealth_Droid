package com.example.ihealthdroid

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Locale

class CreateProfileActivity : ComponentActivity() {
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
                    setContentView(R.layout.create_profile_layout)

                    val db = Firebase.firestore

                    val backToProfileBtn = findViewById<ImageButton>(R.id.back_to_profile)
                    backToProfileBtn.setOnClickListener {
                        val intent = Intent(this@CreateProfileActivity, MedicalProfileActivity::class.java)
                        startActivity(intent)
                    }

                    val nameEditField = findViewById<EditText>(R.id.edit_tv_name)
                    val dobEditField = findViewById<EditText>(R.id.edit_tv_date)
                    val sexBtnGr = findViewById<RadioGroup>(R.id.btn_grp_sex)
                    val phoneEditField = findViewById<EditText>(R.id.edit_tv_phone)
                    val cIDEditField = findViewById<EditText>(R.id.edit_tv_citizen)
                    val ethnicEditField = findViewById<EditText>(R.id.edit_tv_ethnic)
                    val provinceEditField = findViewById<EditText>(R.id.edit_tv_province)
                    val districtEditField = findViewById<EditText>(R.id.edit_tv_distric)

                    dobEditField.setOnClickListener {
                        // on below line we are getting
                        // the instance of our calendar.
                        val c = Calendar.getInstance()

                        // on below line we are getting
                        // our day, month and year.
                        val year = c.get(Calendar.YEAR)
                        val month = c.get(Calendar.MONTH)
                        val day = c.get(Calendar.DAY_OF_MONTH)

                        // on below line we are creating a
                        // variable for date picker dialog.
                        val datePickerDialog = DatePickerDialog(
                            // on below line we are passing context.
                            this,
                            { view, year, monthOfYear, dayOfMonth ->
                                // on below line we are setting
                                // date to our edit text.
                                val dat = (dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                                dobEditField.setText(dat)
                            },
                            // on below line we are passing year, month
                            // and day for the selected date in our date picker.
                            year,
                            month,
                            day
                        )
                        // at last we are calling show
                        // to display our date picker dialog.
                        datePickerDialog.show()
                    }

                    val createProfileBtn = findViewById<Button>(R.id.btn_create_profile)
                    createProfileBtn.setOnClickListener {
                        val userName = nameEditField.text.toString()
                        val userDOB = dobEditField.text.toString()

                        val sexId = sexBtnGr.indexOfChild(findViewById(sexBtnGr.checkedRadioButtonId))
                        var userSex = sexId.toString()
                        if (userSex == "0") {
                            userSex = "male"
                        } else {
                            userSex = "female"
                        }

                        val userPhone = phoneEditField.text.toString()
                        val userCID = cIDEditField.text.toString()
                        val userEthnic = ethnicEditField.text.toString()
                        val userProvince = provinceEditField.text.toString()
                        val userDistrict = districtEditField.text.toString()

                        if (userName.isNullOrEmpty() ||
                            userDOB.isNullOrEmpty() ||
                            sexBtnGr.checkedRadioButtonId == -1 ||
                            userPhone.isNullOrEmpty() ||
                            userCID.isNullOrEmpty() ||
                            userEthnic.isNullOrEmpty() ||
                            userProvince.isNullOrEmpty() ||
                            userDistrict.isNullOrEmpty())

                        {
                            Toast.makeText(
                                this@CreateProfileActivity,
                                R.string.toast_empty_field,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val docref = db.collection("profiles").document("$userPhone")

                            docref.get().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    if(document != null) {
                                        if (document.exists()) {
                                            Log.d("TAG", "Document already exists.")

                                            Toast.makeText(
                                                this@CreateProfileActivity,
                                                R.string.toast_phone_number_exist,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Log.d("TAG", "Document doesn't exist.")
                                        }
                                    }
                                } else {
                                    Log.d("TAG", "Error: ", task.exception)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}