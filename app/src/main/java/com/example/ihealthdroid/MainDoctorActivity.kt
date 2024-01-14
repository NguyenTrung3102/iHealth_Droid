package com.example.ihealthdroid

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.service.controls.ControlsProviderService
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
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ihealthdroid.adapter.AppointmentAdapter
import com.example.ihealthdroid.objectModel.AppointmentModel
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Locale

class MainDoctorActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter
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
                    setContentView(R.layout.main_menu_layout_doctor)

                    val mainMenuUserInfoField = findViewById<TextView>(R.id.main_menu_user_email)
                    var selectDepartment = ""

                    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

                    val db = Firebase.firestore
                    if (currentUserUid != null) {
                        db.collection("accounts")
                            .document(currentUserUid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val accountName = document.getString("displayName")
                                    mainMenuUserInfoField.text = accountName
                                } else {
                                    Log.d(ControlsProviderService.TAG, "No account found")
                                }
                            }
                            .addOnFailureListener {
                                Log.d(ControlsProviderService.TAG, "Error retrieving profiles from Firestore")
                            }
                    }

                    val db2 = Firebase.firestore
                    if (currentUserUid != null) {
                        db2.collection("profile-doc")
                            .document(currentUserUid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    selectDepartment = document.getString("doctorDepartment").toString()
                                } else {
                                    Log.d(ControlsProviderService.TAG, "No account found")
                                }
                            }
                            .addOnFailureListener {
                                Log.d(ControlsProviderService.TAG, "Error retrieving profiles from Firestore")
                            }
                    }

                    val checkDocInfo = findViewById<Button>(R.id.btn_information)
                    checkDocInfo.setOnClickListener {
                        val intent = Intent(this@MainDoctorActivity, ProfileDetailDoctorActivity::class.java)
                        startActivity(intent)
                    }

                    val signOutBtn = findViewById<Button>(R.id.sign_out_btn)
                    signOutBtn.setOnClickListener {
                        val intent = Intent(this@MainDoctorActivity, AccountSignInActivity::class.java)
                        startActivity(intent)
                    }

                    val appSearchDayEditField = findViewById<EditText>(R.id.edit_tv_date)
                    appSearchDayEditField.setOnClickListener() {
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
                                appSearchDayEditField.setText(dat)
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



                    val btnSearchDay = findViewById<Button>(R.id.btn_select_date)
                    btnSearchDay.setOnClickListener{

                        val selectDay = appSearchDayEditField.text.toString()

                        recyclerView = findViewById(R.id.list_appointment)
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        adapter = AppointmentAdapter()
                        recyclerView.adapter = adapter

                        adapter.setOnItemClickListener { profile ->
                            showAppointmentDetail(profile)
                        }

                        getAppointmentFromFirestore(selectDay, selectDepartment)
                    }

                }
            }
        }
    }

    private fun showAppointmentDetail(appointment: AppointmentModel) {
        val intent = Intent(this@MainDoctorActivity, AppointmentDetailDoctorActivity::class.java)
        intent.putExtra("appointment", appointment as Parcelable)
        startActivity(intent)
    }

    private fun getAppointmentFromFirestore(selectDay: String, selectDepartment: String) {
        val db = FirebaseFirestore.getInstance()
        val appointmentCollection = db.collection("appointments")

        appointmentCollection
            .whereEqualTo("appSelectedDate", selectDay)
            .whereEqualTo("appSelectedDepartment", selectDepartment)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val appointmentList = mutableListOf<AppointmentModel>()

                for (documentSnapshot in querySnapshot.documents) {
                    val document = documentSnapshot.toObject(AppointmentModel::class.java)
                    if (document != null) {
                        appointmentList.add(document)
                    }
                }

                Log.d(ControlsProviderService.TAG, "Retrieved ${appointmentList.size} appointments from Firestore")
                adapter.submitList(appointmentList)
            }
            .addOnFailureListener { exception ->
                Log.e(ControlsProviderService.TAG, "Error retrieving appointments from Firestore", exception)
            }
    }
}