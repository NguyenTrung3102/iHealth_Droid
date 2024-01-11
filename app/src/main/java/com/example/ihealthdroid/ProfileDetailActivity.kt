package com.example.ihealthdroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme
import java.util.Locale

class ProfileDetailActivity : ComponentActivity() {
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
                    setContentView(R.layout.profile_detail_layout)

                    val backToProfileBtn = findViewById<ImageButton>(R.id.back_to_profile)
                    backToProfileBtn.setOnClickListener {
                        val intent = Intent(this@ProfileDetailActivity, MedicalProfileActivity::class.java)
                        startActivity(intent)
                    }

                    val userNameView = findViewById<TextView>(R.id.edit_tv_name)
                    val userDOBView = findViewById<TextView>(R.id.edit_tv_date)
                    val userSexView = findViewById<TextView>(R.id.edit_tv_sex)
                    val userPhoneView = findViewById<TextView>(R.id.edit_tv_phone)
                    val userCIDView = findViewById<TextView>(R.id.edit_tv_citizen)
                    val userEthnicView = findViewById<TextView>(R.id.edit_tv_ethnic)
                    val userProvinceView = findViewById<TextView>(R.id.edit_tv_province)
                    val userDistrictView = findViewById<TextView>(R.id.edit_tv_distric)

                    val profile = intent.getSerializableExtra("profile") as? ProfileModel
                    if (profile != null) {
                        userNameView.text = profile.name
                        userDOBView.text = profile.dob
                        userSexView.text = profile.sex
                        userPhoneView.text = profile.phone
                        userCIDView.text = profile.citizenID
                        userEthnicView.text = profile.ethnic
                        userProvinceView.text = profile.province
                        userDistrictView.text = profile.district
                    }
                }
            }
        }
    }
}