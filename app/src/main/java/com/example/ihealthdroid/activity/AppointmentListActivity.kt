package com.example.ihealthdroid.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.service.controls.ControlsProviderService
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ihealthdroid.R
import com.example.ihealthdroid.adapter.AppointmentAdapter
import com.example.ihealthdroid.objectModel.AppointmentModel
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class AppointmentListActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentAdapter
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
                    setContentView(R.layout.list_appointment_layout)

                    val backToAppointBtn = findViewById<ImageButton>(R.id.back_to_appointment)
                    backToAppointBtn.setOnClickListener {
                        val intent = Intent(this@AppointmentListActivity, PickAppointmentActivity::class.java)
                        startActivity(intent)
                    }

                    /*
                    val appSearchDayEditField = findViewById<EditText>(R.id.edit_app_day_search)
                    appSearchDayEditField.setOnClickListener {
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
                     */

                    val appPhoneSearchField = findViewById<EditText>(R.id.edit_app_phone_search)

                    recyclerView = findViewById(R.id.appointment_list)
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    adapter = AppointmentAdapter()
                    recyclerView.adapter = adapter

                    val searchAppointmentBtn = findViewById<Button>(R.id.app_search_btn)
                    searchAppointmentBtn.setOnClickListener {

                        val appSearchPhone = appPhoneSearchField.text.toString()

                        //Log.d(TAG, "$appSearchDate/$appSearchTime/$appSearchPhone")

                        if (appSearchPhone.isNullOrEmpty()) {
                            Toast.makeText(
                                this@AppointmentListActivity,
                                R.string.toast_empty_field,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            adapter.setOnItemClickListener { appointment ->
                                showAppointmentDetail(appointment)
                            }
                            getAppointmentFromFirestore(appSearchPhone)
                        }
                    }
                }
            }
        }
    }
    private fun showAppointmentDetail(appointment: AppointmentModel) {
        val intent = Intent(this@AppointmentListActivity, AppointmentDetailActivity::class.java)
        intent.putExtra("appointment", appointment as Parcelable)
        startActivity(intent)
    }

    private fun getAppointmentFromFirestore(appSearchPhone: String) {

        val db = FirebaseFirestore.getInstance()
        val appointmentCollection = db.collection("appointments")

        appointmentCollection
            .whereEqualTo("appUserPhone", appSearchPhone)
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