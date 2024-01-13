package com.example.ihealthdroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Locale

class AccountCreatingActivity : ComponentActivity() {

    var isSpinnerInitialized: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lateinit var auth: FirebaseAuth
        auth = Firebase.auth

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
                    setContentView(R.layout.signup_layout)

                    val userNameField = findViewById<EditText>(R.id.username_input)
                    val passwordInputField = findViewById<EditText>(R.id.password_input)
                    val passwordConfirmField = findViewById<EditText>(R.id.password_confirm)
                    val accRoleSpinner: Spinner = findViewById(R.id.account_role_spinner)
                    val adapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.role_array, android.R.layout.simple_spinner_item
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    accRoleSpinner.adapter = adapter

                    var accRole = ""
                    accRoleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            if (isSpinnerInitialized) {
                                accRole = when (position) {
                                    0 -> "doctor"
                                    1 -> "patient"
                                    else -> "" // Return null for "Select Language" option
                                }
                            } else {
                                isSpinnerInitialized = true
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // Do nothing
                        }
                    }

                    val createAccountBtn = findViewById<Button>(R.id.create_account_btn)
                    createAccountBtn.setOnClickListener {
                        val email = userNameField.text.toString()
                        val pwdInput = passwordInputField.text.toString()
                        val pwdConfirm = passwordConfirmField.text.toString()

                        if (email.isNullOrEmpty() || pwdInput.isNullOrEmpty() || pwdConfirm.isNullOrEmpty() || accRole.isNullOrEmpty()) {
                            Toast.makeText(
                                baseContext,
                                context.getString(R.string.toast_empty_field),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (pwdInput == pwdConfirm) {
                                auth.createUserWithEmailAndPassword(email, pwdInput)
                                    .addOnCompleteListener(this) { task ->
                                        if (task.isSuccessful) {
                                            val user = auth.currentUser
                                            val uid = user?.uid

                                            val db = FirebaseFirestore.getInstance()
                                            val usersCollection = db.collection("accounts")
                                            val userDocument = usersCollection.document(uid!!)

                                            val userData = hashMapOf(
                                                "email" to email,
                                                "role" to accRole
                                            )

                                            userDocument.set(userData)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        baseContext,
                                                        context.getString(R.string.toast_auth_succeeded),
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    val intent = Intent(
                                                        this@AccountCreatingActivity,
                                                        AccountSignInActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                }
                                                .addOnFailureListener { exception ->
                                                    Toast.makeText(
                                                        baseContext,
                                                        context.getString(R.string.toast_auth_failed),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        } else {
                                            Toast.makeText(
                                                baseContext,
                                                context.getString(R.string.toast_auth_failed),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    context.getString(R.string.toast_pwd_confirm_mismatch),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    val signUpCancelBtn = findViewById<Button>(R.id.sign_up_cncl_btn)
                    signUpCancelBtn.setOnClickListener {
                        val intent = Intent(this@AccountCreatingActivity, AccountSignInActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}