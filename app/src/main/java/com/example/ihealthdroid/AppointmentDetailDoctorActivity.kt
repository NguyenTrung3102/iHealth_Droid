package com.example.ihealthdroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ihealthdroid.objectModel.AppointmentModel
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class AppointmentDetailDoctorActivity : ComponentActivity() {

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
                    setContentView(R.layout.appointment_detail_doctor_layout)

                    val backToAppListBtn = findViewById<ImageButton>(R.id.back_to_appointment_list)
                    backToAppListBtn.setOnClickListener {
                        val intent = Intent(
                            this@AppointmentDetailDoctorActivity,
                            MainDoctorActivity::class.java
                        )
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


                    val appointment =
                        intent.getSerializableExtra("appointment") as? AppointmentModel
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
                                departmentArray =
                                    documentSnapshot.get(departmentNameLocale) as ArrayList<String>

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
                            userAppTimeView.text =
                                resources.getString(R.string.pick_a_time_option_1)
                        } else {
                            userAppTimeView.text =
                                resources.getString(R.string.pick_a_time_option_2)
                        }

                        userAppSymptomsView.text = appointment.appSymptomsInfo

                        when (appointment.appStatus) {
                            "pending" -> {
                                userAppStatusView.text =
                                    resources.getString(R.string.app_status_pending)
                            }

                            "accepted" -> {
                                userAppStatusView.text =
                                    resources.getText(R.string.app_status_accepted)
                            }

                            else -> {
                                userAppStatusView.text =
                                    resources.getText(R.string.app_status_rejected)
                            }
                        }
                    }

                    val appDepartment = appointment?.appSelectedDepartment
                    val appTime = appointment?.appSelectedTime
                    val loggedInAcc = FirebaseAuth.getInstance().currentUser
                    var doctorUID: String = loggedInAcc?.uid ?: ""

                    var doctorName = ""
                    db.collection("profile-doc").document("$doctorUID")
                        .get()
                        .addOnSuccessListener { document ->
                            Log.d(ControlsProviderService.TAG, "Doctor profiles created")
                            doctorName = document["doctorName"].toString()
                        }
                        .addOnFailureListener { exception ->
                            Log.d(
                                ControlsProviderService.TAG,
                                "Error when creating doctor profiles"
                            )
                        }

                    val acceptBtn = findViewById<Button>(R.id.btn_accept)
                    acceptBtn.setOnClickListener {
                        val appData = hashMapOf(
                            "appUserName" to userAppNameView.text.toString(),
                            "appUserPhone" to userAppPhoneView.text.toString(),
                            "appUserProvince" to userAppProvinceView.text.toString(),
                            "appSelectedDepartment" to appDepartment,
                            "appSelectedDate" to userAppDateView.text.toString(),
                            "appSelectedTime" to appTime,
                            "appSymptomsInfo" to userAppSymptomsView.text.toString(),
                            "appStatus" to "accepted", /* pushing status to database,
                                                 when a doctor/operator accept the appointment,
                                                 it'll be overwrite to Accepted */
                            "appDoctor" to doctorName
                        )
                        userAppStatusView.text = resources.getText(R.string.app_status_accepted)

                        db.collection("appointments")
                            .document("${userAppDateView.text}" + "_" + "$appTime" + "_" + "${userAppPhoneView.text}")
                            .set(appData)
                            .addOnSuccessListener {
                                Log.d(ControlsProviderService.TAG, "Appointment Accpeted")
                                Toast.makeText(
                                    this@AppointmentDetailDoctorActivity,
                                    R.string.toast_app_accepted,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { exception ->
                                Log.d(
                                    ControlsProviderService.TAG,
                                    "Error when creating doctor profiles"
                                )
                        }
                    }

                    val rejectBtn = findViewById<Button>(R.id.btn_reject)
                    rejectBtn.setOnClickListener {
                        val appData = hashMapOf(
                            "appUserName" to userAppNameView.text.toString(),
                            "appUserPhone" to userAppPhoneView.text.toString(),
                            "appUserProvince" to userAppProvinceView.text.toString(),
                            "appSelectedDepartment" to appDepartment,
                            "appSelectedDate" to userAppDateView.text.toString(),
                            "appSelectedTime" to appTime,
                            "appSymptomsInfo" to userAppSymptomsView.text.toString(),
                            "appStatus" to "rejected", /* pushing status to database,
                                                 when a doctor/operator accept the appointment,
                                                 it'll be overwrite to Accepted */
                            "appDoctor" to ""
                        )
                        userAppStatusView.text = resources.getText(R.string.app_status_rejected)

                        db.collection("appointments")
                            .document("${userAppDateView.text}" + "_" + "$appTime" + "_" + "${userAppPhoneView.text}")
                            .set(appData)
                            .addOnSuccessListener {
                                Log.d(ControlsProviderService.TAG, "Appointment Rejected")
                                Toast.makeText(
                                    this@AppointmentDetailDoctorActivity,
                                    R.string.toast_app_rejected,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { exception ->
                                Log.d(
                                    ControlsProviderService.TAG,
                                    "Error when creating doctor profiles"
                                )
                            }
                    }
                }
            }
        }
    }
}