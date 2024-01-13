package com.example.ihealthdroid

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
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

                    //Spiner
                    val departmentSpinner: Spinner = findViewById(R.id.spn_department)
                    val departmentData = Firebase.firestore
                    val docref = departmentData.collection("appointment").document("department")
                    docref.get().addOnSuccessListener { documentSnapshot ->

                        if(documentSnapshot.exists()) {

                            //Get arrayList
                            val departmentList = documentSnapshot.get("department") as ArrayList<String>

                            //Create adapter and add data
                            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departmentList)
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            departmentSpinner.adapter = adapter
                        }
                    }.addOnFailureListener { exception ->
                        //Handle Error
                        Log.d("TAG", "get failed with ", exception)
                    }

                    departmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            // Lấy item được chọn tại vị trí position
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
                                val dat = (dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
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

                    val timeBtnGr = findViewById<RadioGroup>(R.id.btn_group_date)
                    val symptomsEditField = findViewById<EditText>(R.id.edit_tv_symptoms)
                    //Make An Appointment
                    val makeAnAppointment = findViewById<Button>(R.id.btn_create_appoint)
                    /*makeAnAppointment.setOnClickListener{

                        val userDepartment = departmentSpinner.text.toString()
                        val userDOB = dobEditField.text.toString()

                        val sexId = sexBtnGr.indexOfChild(findViewById(sexBtnGr.checkedRadioButtonId))
                        var userSex = sexId.toString()
                        userSex = if (userSex == "0") {
                            "male"
                        } else {
                            "female"
                        }
                    }*/
                }
            }
        }
    }
}