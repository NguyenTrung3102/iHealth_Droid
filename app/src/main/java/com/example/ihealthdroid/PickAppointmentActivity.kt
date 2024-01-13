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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Locale

class PickAppointmentActivity : ComponentActivity() {

    var isSpinnerInitialized = false
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
                        val intent = Intent(this@PickAppointmentActivity, MainActivity::class.java)
                        startActivity(intent)
                    }

                    //Get information
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
                                showBirthDay.text = birthDate

                                val showPhone = findViewById<TextView>(R.id.tv_show_phone)
                                showPhone.text = phoneNum

                                val showProvince = findViewById<TextView>(R.id.tv_show_province)
                                showProvince.text = province


                            } else {
                                //Handle the case where the document does not exist
                                Log.d("TAG", "Profile not found ")

                                Toast.makeText(
                                    this@PickAppointmentActivity,
                                    "Profile not found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }.addOnFailureListener { exception ->
                            //Handle Error
                            Log.d("TAG", "get failed with ", exception)
                        }
                    }

                    //Spiner
                    val departmentSpinner: Spinner = findViewById(R.id.spn_department)
                    val departmentData = Firebase.firestore

                    var departmentNameLocale = ""

                    Log.d(TAG, "$selectedLanguage")
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
                            departmentSpinner.adapter = adapter
                        }
                    }.addOnFailureListener { exception ->
                        //Handle Error
                        Log.d("TAG", "get failed with ", exception)
                    }

                    var selectedDepartment = ""
                    departmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

                    //Appointment Date
                    val appointmentDayEditField = findViewById<EditText>(R.id.edit_tv_date)
                    appointmentDayEditField.setOnClickListener {
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
                                appointmentDayEditField.setText(dat)
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

                    val symptomsEditField = findViewById<EditText>(R.id.edit_tv_symptoms)

                    //Make An Appointment
                    val makeAnAppointment = findViewById<Button>(R.id.btn_create_appoint)
                    makeAnAppointment.setOnClickListener{

                        //get user info from 4 textview
                        val appUserNameField = findViewById<TextView>(R.id.tv_show_name)
                        val appUserDOBField = findViewById<TextView>(R.id.tv_show_date)
                        val appUserPhoneField = findViewById<TextView>(R.id.tv_show_phone)
                        val appUserProvinceField = findViewById<TextView>(R.id.tv_show_province)

                        val appUserName = appUserNameField.text.toString()
                        val appUserDOB = appUserDOBField.text.toString()
                        val appUserPhone = appUserPhoneField.text.toString()
                        val appUserProvince = appUserProvinceField.text.toString()

                        //get selected department index position in spinner
                        val appSelectedDepartment = selectedDepartment

                        //get selected date for appointment
                        val appSelectedDate = appointmentDayEditField.text.toString()

                        //get selected time for appointment
                        val timeBtnGr = findViewById<RadioGroup>(R.id.btn_group_date)
                        val timeId = timeBtnGr.indexOfChild(findViewById(timeBtnGr.checkedRadioButtonId))
                        var selectedTime = timeId.toString()
                        val appSelectedTime = selectedTime

                        //get user symptoms info from edit text
                        val appSymptomsInfo = symptomsEditField.text.toString()

                        val db = Firebase.firestore //initialize Firestore pushing

                        if (appUserName.isNullOrEmpty() ||
                            appUserDOB.isNullOrEmpty() ||
                            appUserPhone.isNullOrEmpty() ||
                            appUserProvince.isNullOrEmpty() ||
                            appSelectedDepartment.isNullOrEmpty() ||
                            appSelectedDate.isNullOrEmpty() ||
                            timeBtnGr.checkedRadioButtonId == -1 ||
                            appSymptomsInfo.isNullOrEmpty())

                        {
                            Toast.makeText(
                                this@PickAppointmentActivity,
                                R.string.toast_empty_field,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val docref = db.collection("appointments").document("$appSelectedDate/$appSelectedTime/$appUserPhone")

                            docref.get().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    if(document != null) {
                                        if (document.exists()) {
                                            Log.d("TAG", "Document already exists.")

                                            Toast.makeText(
                                                this@PickAppointmentActivity,
                                                "Can't create appointment because there is other appointment at your selected time",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Log.d("TAG", "Document doesn't exist.")

                                            // Create a new user with info above
                                            val appStatus = "pending" //Initialize appointment Status, default is Pending
                                            val user = hashMapOf(
                                                "appUserName" to appUserName,
                                                "appUserDOB" to appUserDOB,
                                                "appUserPhone" to appUserPhone,
                                                "appUserProvince" to appUserProvince,
                                                "appSelectedDepartment" to appSelectedDepartment,
                                                "appSelectedDate" to appSelectedDate,
                                                "appSelectedTime" to appSelectedTime,
                                                "appSymptomsInfo" to appSymptomsInfo,
                                                "appStatus" to appStatus /* pushing status to database,
                                                 when a doctor/operator accept the appointment,
                                                 it'll be overwrite to Accepted */
                                            )

                                            // Add a new document with a generated ID
                                            db.collection("appointments").document("$appSelectedDate/$appSelectedTime/$appUserPhone")
                                                .set(user)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this@PickAppointmentActivity,
                                                        R.string.toast_profile_created,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w(ControlsProviderService.TAG, "Error adding document", e)
                                                }
                                        }
                                    }
                                } else {
                                    Log.d("TAG", "Error: ", task.exception)
                                }
                            }
                        }
                    }

                    val listButton = findViewById<ImageButton>(R.id.btn_list_appointment)
                    listButton.setOnClickListener {
                        val intent = Intent(this@PickAppointmentActivity, AppointmentList::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}