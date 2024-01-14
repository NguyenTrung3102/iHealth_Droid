package com.example.ihealthdroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
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

class ProfileDetailDoctorActivity: ComponentActivity() {

    private var departmentArray = mutableListOf<String>()
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
                        setContentView(R.layout.profile_detail_doctor_layout)

                        val backToMainDocBtn = findViewById<ImageButton>(R.id.back_to_main_profile)
                        backToMainDocBtn.setOnClickListener {
                            val intent = Intent(this@ProfileDetailDoctorActivity, MainDoctorActivity::class.java)
                            startActivity(intent)
                        }

                        val editDocProfileBtn = findViewById<ImageButton>(R.id.profile_edit_btn_doc)
                        editDocProfileBtn.setOnClickListener {
                            val intent = Intent(this@ProfileDetailDoctorActivity, CreateProfileDoctorActivity::class.java)
                            startActivity(intent)
                        }

                        val doctorNameView = findViewById<TextView>(R.id.edit_doc_name)
                        val doctorSexView = findViewById<TextView>(R.id.edit_doc_sex)
                        val doctorDepartmentView = findViewById<TextView>(R.id.edit_doc_department)

                        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

                        val db = Firebase.firestore
                        if (currentUserUid != null) {
                            db.collection("profile-doc")
                                .document(currentUserUid)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        doctorNameView.text = document["doctorName"].toString()

                                        val docSex = document["doctorSex"].toString()
                                        if (docSex == "male") {
                                            doctorSexView.text =
                                                resources.getText(R.string.male_btn_grp)
                                        } else if (docSex == "female") {
                                            doctorSexView.text =
                                                resources.getText(R.string.female_btn_grp)
                                        } else {
                                            doctorSexView.text == ""
                                        }
                                    } else {
                                        Log.d(ControlsProviderService.TAG, "No info found")
                                    }

                                    var departmentNameLocale = ""
                                    doctorDepartmentView.text = ""
                                    val departmentData = Firebase.firestore

                                    Log.d(ControlsProviderService.TAG, "$selectedLanguage")
                                    departmentNameLocale = if (selectedLanguage == "en_US") {
                                        "department"
                                    } else {
                                        "department-vi"
                                    }

                                    val docref = departmentData.collection("appointment")
                                        .document(departmentNameLocale)
                                    docref.get().addOnSuccessListener { documentSnapshot ->

                                        if (documentSnapshot.exists()) {

                                            //Get arrayList
                                            departmentArray =
                                                documentSnapshot.get(departmentNameLocale) as ArrayList<String>
                                            for (departmentID in 0 until departmentArray.size) {
                                                if (departmentID.toString() == document["doctorDepartment"].toString()) {
                                                    doctorDepartmentView.text =
                                                        departmentArray[departmentID]
                                                }
                                            }
                                        }
                                    }.addOnFailureListener { exception ->
                                        //Handle Error
                                        Log.d("TAG", "get failed with ", exception)
                                    }
                                }
                                .addOnFailureListener {
                                    Log.d(
                                        ControlsProviderService.TAG,
                                        "Error retrieving info from Firestore"
                                    )
                                }
                    }
                }
            }
        }
    }
}