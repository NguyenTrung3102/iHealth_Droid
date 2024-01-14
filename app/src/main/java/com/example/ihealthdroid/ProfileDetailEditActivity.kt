package com.example.ihealthdroid

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
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
import java.util.Calendar
import java.util.Locale

class ProfileDetailEditActivity : ComponentActivity() {

    var isSpinnerInitialized = true
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
                    setContentView(R.layout.profile_detail_edit_layout)

                    val db = Firebase.firestore

                    val backToDocProfileBtn = findViewById<ImageButton>(R.id.back_to_doc_profile)
                    backToDocProfileBtn.setOnClickListener {
                        val intent = Intent(this@ProfileDetailEditActivity, MedicalProfileActivity::class.java)
                        startActivity(intent)
                    }

                    val userPhone:String = intent.getStringExtra("userPhone").toString()

                    val userNameView = findViewById<TextView>(R.id.edit_tv_name)
                    val userDOBView = findViewById<TextView>(R.id.edit_tv_date)
                    val userSexView = findViewById<RadioGroup>(R.id.btn_grp_sex)
                    val userPhoneView = findViewById<TextView>(R.id.edit_tv_phone)
                    val userCIDView = findViewById<TextView>(R.id.edit_tv_citizen)
                    val userEthnicView = findViewById<TextView>(R.id.edit_tv_ethnic)
                    val userProvinceView = findViewById<TextView>(R.id.edit_tv_province)
                    val userDistrictView = findViewById<TextView>(R.id.edit_tv_district)

                    // Get the currently logged-in user's UID
                    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

                    // Query the Firestore collection to retrieve the document with the specified ID and createdBy field
                    val docRef = db.collection("profiles").document("$userPhone")
                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                userNameView.text = document["name"].toString()
                                userDOBView.text = document["dob"].toString()
                                userPhoneView.text = document["phone"].toString()
                                userCIDView.text = document["citizenID"].toString()
                                userEthnicView.text = document["ethnic"].toString()
                                userProvinceView.text = document["province"].toString()
                                userDistrictView.text = document["district"].toString()
                            } else {
                                Log.d(TAG, "get document failed")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "get failed with ", exception)
                        }

                    userDOBView.setOnClickListener {
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
                                val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                                userDOBView.setText(dat)
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

                    val saveProfileDetailBtn = findViewById<Button>(R.id.save_profile_detail_btn)
                    saveProfileDetailBtn.setOnClickListener {
                        val userNameNew = userNameView.text.toString()
                        val userDOBNew = userDOBView.text.toString()

                        val sexId = userSexView.indexOfChild(findViewById(userSexView.checkedRadioButtonId))
                        var userSexNew = sexId.toString()
                        userSexNew = if (userSexNew == "0") {
                            "male"
                        } else {
                            "female"
                        }
                        val userCIDNew = userCIDView.text.toString()
                        val userEthnicNew = userEthnicView.text.toString()
                        val userProvinceNew = userProvinceView.text.toString()
                        val userDistrictNew = userDistrictView.text.toString()

                        if ( userNameNew.isNullOrEmpty() ||
                            userDOBNew.isNullOrEmpty() ||
                            userSexView.checkedRadioButtonId == -1 ||
                            userCIDNew.isNullOrEmpty() ||
                            userEthnicNew.isNullOrEmpty() ||
                            userProvinceNew.isNullOrEmpty() ||
                            userDistrictNew.isNullOrEmpty()
                        )
                        {
                            Toast.makeText(
                                this@ProfileDetailEditActivity,
                                R.string.toast_empty_field,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val newProfileDta = hashMapOf(
                                "name" to userNameNew,
                                "dob" to userDOBNew,
                                "sex" to userSexNew,
                                "phone" to userPhone,
                                "citizenID" to userCIDNew,
                                "ethnic" to userEthnicNew,
                                "province" to userProvinceNew,
                                "district" to userDistrictNew,
                                "createdBy" to currentUserUid
                            )

                            val docRef = db.collection("profiles").document("$userPhone")
                            docRef.set(newProfileDta)
                                .addOnSuccessListener { document ->
                                    Log.d(TAG, "Saved successfully")
                                    Toast.makeText(
                                        this@ProfileDetailEditActivity,
                                        R.string.toast_doctor_profile_created,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent = Intent(this@ProfileDetailEditActivity, MedicalProfileActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                        }
                    }
                }
            }
        }
    }
}