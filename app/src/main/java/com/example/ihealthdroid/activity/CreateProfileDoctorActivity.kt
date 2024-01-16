package com.example.ihealthdroid.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ihealthdroid.R
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class CreateProfileDoctorActivity : ComponentActivity() {

    var isSpinnerInitialized = true
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
                    setContentView(R.layout.profile_detail_edit_doc_layout)

                    val db = Firebase.firestore

                    val backToDocProfileBtn = findViewById<ImageButton>(R.id.back_to_doc_profile)
                    backToDocProfileBtn.setOnClickListener {
                        val intent = Intent(this@CreateProfileDoctorActivity, ProfileDetailDoctorActivity::class.java)
                        startActivity(intent)
                    }

                    val doctorNameView = findViewById<EditText>(R.id.edit_doc_name)
                    val doctorSexBtnGrp = findViewById<RadioGroup>(R.id.radio_btn_doc_sex_grp)

                    val doctorDepartmentSpinner: Spinner = findViewById(R.id.doc_department_spinner)
                    val departmentData = Firebase.firestore

                    var departmentNameLocale = ""

                    Log.d(ControlsProviderService.TAG, "$selectedLanguage")
                    departmentNameLocale = if (selectedLanguage == "en_US") {
                        "department"
                    } else {
                        "department-vi"
                    }

                    val docref = departmentData.collection("appointment").document(departmentNameLocale)
                    docref.get().addOnSuccessListener { documentSnapshot ->

                        if(documentSnapshot.exists()) {

                            //Get arrayList
                            val departmentList = documentSnapshot.get(departmentNameLocale) as ArrayList<String>

                            //Create adapter and add data
                            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departmentList)
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            doctorDepartmentSpinner.adapter = adapter
                        }
                    }.addOnFailureListener { exception ->
                        //Handle Error
                        Log.d("TAG", "get failed with ", exception)
                    }

                    var selectedDepartment = ""
                    doctorDepartmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            if (isSpinnerInitialized) {
                                selectedDepartment = position.toString()
                            } else {
                                isSpinnerInitialized = true
                            }
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }
                    }

                    val saveChangeDocProfile = findViewById<Button>(R.id.profile_doc_detail_save)
                    saveChangeDocProfile.setOnClickListener {
                        val loggedInAcc = FirebaseAuth.getInstance().currentUser
                        val doctorUID: String = loggedInAcc?.uid ?: ""

                        val doctorName = doctorNameView.text.toString()

                        val sexId = doctorSexBtnGrp.indexOfChild(findViewById(doctorSexBtnGrp.checkedRadioButtonId))
                        var doctorSex = sexId.toString()
                        doctorSex = if (doctorSex == "0") {
                            "male"
                        } else {
                            "female"
                        }

                        if (doctorName.isNullOrEmpty() ||
                            doctorSexBtnGrp.checkedRadioButtonId == -1
                        )
                        {
                            Toast.makeText(
                                this@CreateProfileDoctorActivity,
                                R.string.toast_empty_field,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {

                            val doctorData = hashMapOf(
                                "doctorName" to doctorName,
                                "doctorSex" to doctorSex,
                                "doctorDepartment" to selectedDepartment
                            )

                            db.collection("profile-doc").document(doctorUID)
                                .set(doctorData)
                                .addOnSuccessListener {
                                    Log.d(ControlsProviderService.TAG, "Doctor profiles created")
                                    Toast.makeText(
                                        this@CreateProfileDoctorActivity,
                                        R.string.toast_doctor_profile_created,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener {
                                    Log.d(ControlsProviderService.TAG, "Error when creating doctor profiles")
                                }
                        }
                    }
                }
            }
        }
    }
}