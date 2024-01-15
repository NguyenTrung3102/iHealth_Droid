package com.example.ihealthdroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.ihealthdroid.objectModel.AppointmentModel
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class AccountSignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth: FirebaseAuth = Firebase.auth

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
                    setContentView(R.layout.signin_layout)

                    val settingBtn = findViewById<ImageButton>(R.id.setting_btn)
                    settingBtn.setOnClickListener {
                        val intent = Intent(this@AccountSignInActivity, SettingsActivity::class.java)
                        startActivity(intent)
                    }

                    val userNameField = findViewById<EditText>(R.id.username_input)
                    val passwordInputField = findViewById<EditText>(R.id.password_input)

                    val signInBtn = findViewById<Button>(R.id.sign_in_btn)
                    signInBtn.setOnClickListener {
                        val email = userNameField.text.toString()
                        val pwdInput = passwordInputField.text.toString()

                        if (email.isNullOrEmpty() || pwdInput.isNullOrEmpty()) {
                            Toast.makeText(
                                baseContext,
                                context.getString(R.string.toast_empty_field),
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else {
                            auth.signInWithEmailAndPassword(email, pwdInput)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        //Log.d(TAG, "signInWithEmail:success")
                                        auth.currentUser

                                        Toast.makeText(
                                            baseContext,
                                            context.getString(R.string.toast_sign_in_succeeded),
                                            Toast.LENGTH_SHORT,
                                        ).show()

                                        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

                                        val db = Firebase.firestore
                                        if (currentUserUid != null) {
                                            db.collection("accounts")
                                                .document(currentUserUid)
                                                .get()
                                                .addOnSuccessListener { document ->
                                                    if (document.exists()) {
                                                        val accountRole = document.getString("role")

                                                        // Check if the createdBy field matches the currently logged-in user's UID
                                                        if (accountRole == "doctor") {
                                                            val intent = Intent(this@AccountSignInActivity, MainDoctorActivity::class.java)
                                                            startActivity(intent)
                                                        } else {
                                                            val intent = Intent(this@AccountSignInActivity, MainActivity::class.java)
                                                            startActivity(intent)
                                                        }
                                                    } else {
                                                        Log.d(ControlsProviderService.TAG, "No account found")
                                                    }
                                                }
                                                .addOnFailureListener {
                                                    Log.d(ControlsProviderService.TAG, "Error retrieving profiles from Firestore")
                                                }
                                        }

                                        //updateUI(user)
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        //Log.w(TAG, "signInWithEmail:failure", task.exception)
                                        Toast.makeText(
                                            baseContext,
                                            context.getString(R.string.toast_sign_in_failed),
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                        //updateUI(null)
                                    }
                                }
                        }
                    }

                    val signUpBtn = findViewById<Button>(R.id.sign_up_btn)
                    signUpBtn.setOnClickListener {
                        val intent = Intent(this@AccountSignInActivity, AccountCreatingActivity::class.java)
                        startActivity(intent)


                    }
                }
            }
        }
    }
}