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
import com.google.firebase.firestore.FirebaseFirestore
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme
import java.util.Locale

class AppointmentDetailActivity : ComponentActivity() {

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
                    setContentView(R.layout.appointment_detail_layout)

                    val backToAppListBtn = findViewById<ImageButton>(R.id.back_to_appointment_list)
                    backToAppListBtn.setOnClickListener {
                        val intent = Intent(this@AppointmentDetailActivity, AppointmentList::class.java)
                        startActivity(intent)
                    }

                    val db = FirebaseFirestore.getInstance()

                    val userAppNameView = findViewById<TextView>(R.id.edit_tv_app_name)
                    val userAppPhoneView = findViewById<TextView>(R.id.edit_tv_app_phone)
                    val userAppProvinceView = findViewById<TextView>(R.id.edit_tv_accommodation)
                    val userAppSelectedDepartmentView = findViewById<TextView>(R.id.edit_tv_app_department)
                    val userAppDateView = findViewById<TextView>(R.id.edit_tv_appointment_date)
                    val userAppTimeView = findViewById<TextView>(R.id.edit_tv_appointment_time)
                    val userAppSymptomsView = findViewById<TextView>(R.id.edit_tv_app_symptoms)
                    val userAppStatusView = findViewById<TextView>(R.id.edit_tv_appointment_status)


                    val appointment = intent.getSerializableExtra("appointment") as? AppointmentModel
                    if (appointment != null) {
                        userAppNameView.text = appointment.appUserName
                        userAppPhoneView.text = appointment.appUserPhone
                        userAppProvinceView.text = appointment.appUserProvince

                        var departmentNameLocale = ""

                        Log.d(ControlsProviderService.TAG, "$selectedLanguage")
                        departmentNameLocale = if (selectedLanguage == "en_US") {
                            "department"
                        } else {
                            "department-vi"
                        }

                        val docref = db.collection("appointment").document(departmentNameLocale)
                        docref.get().addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                departmentArray = documentSnapshot.get(departmentNameLocale) as ArrayList<String>

                                var text = ""
                                for (departmentID in 0 until departmentArray.size) {
                                    if (departmentID.toString() == appointment.appSelectedDepartment) {
                                        text = departmentArray[departmentID]
                                    }
                                }
                                userAppSelectedDepartmentView.text = text
                            }
                        }.addOnFailureListener { exception ->
                            // Handle Error
                            Log.d("TAG", "get failed with ", exception)
                        }

                        userAppDateView.text = appointment.appSelectedDate

                        if (appointment.appSelectedTime == "0") {
                            userAppTimeView.text = resources.getString(R.string.pick_a_time_option_1)
                        } else {
                            userAppTimeView.text = resources.getString(R.string.pick_a_time_option_2)
                        }

                        userAppSymptomsView.text = appointment.appSymptomsInfo

                        if (appointment.appStatus == "pending") {
                            userAppStatusView.text = resources.getString(R.string.app_status_pending)
                        } else {
                            userAppStatusView.text = resources.getString(R.string.app_status_accepted)
                        }
                    }
                }
            }
        }
    }
}