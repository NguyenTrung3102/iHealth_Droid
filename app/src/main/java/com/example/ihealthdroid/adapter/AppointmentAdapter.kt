package com.example.ihealthdroid.adapter

import android.content.Context
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ihealthdroid.objectModel.AppointmentModel
import com.example.ihealthdroid.R
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentAdapter : ListAdapter<AppointmentModel, AppointmentAdapter.AppointmentViewHolder>(
    DiffCallback()
) {
    private val db = FirebaseFirestore.getInstance()
    private var departmentArray = mutableListOf<String>()
    private var onItemClickListener: ((AppointmentModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (AppointmentModel) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = getItem(position)
        holder.bind(appointment)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(appointment)
        }
    }

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameAppointment: TextView = itemView.findViewById(R.id.tv_show_name)
        private val userDepartmentAppointment: TextView = itemView.findViewById(R.id.tv_show_department)
        private val userDateAppointment: TextView = itemView.findViewById(R.id.tv_show_date)
        private val userTimeAppointment: TextView = itemView.findViewById(R.id.tv_show_time)

        fun bind(appointment: AppointmentModel) {
            userNameAppointment.text = appointment.appUserName
            userDateAppointment.text = appointment.appSelectedDate

            fetchDepartmentArray(itemView.context, appointment) // Pass the appointment object as a parameter

            if (appointment.appSelectedTime == "0") {
                userTimeAppointment.text = itemView.context.getString(R.string.pick_a_time_option_1)
            } else {
                userTimeAppointment.text = itemView.context.getString(R.string.pick_a_time_option_2)
            }
        }

        private fun fetchDepartmentArray(context: Context, appointment: AppointmentModel) {
            val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val selectedLanguage = sharedPreferences.getString("selectedLanguage", "en_us")

            var departmentNameLocale = ""

            Log.d(TAG, "$selectedLanguage")
            departmentNameLocale = if (selectedLanguage == "en_US") {
                "department"
            } else {
                "department-vi"
            }

            val docref = db.collection("appointment").document(departmentNameLocale)
            docref.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    departmentArray = documentSnapshot.get(departmentNameLocale) as ArrayList<String>
                    userDepartmentAppointment.text = updateDepartmentText(appointment.appSelectedDepartment) // Update the department text here
                }
            }.addOnFailureListener { exception ->
                // Handle Error
                Log.d("TAG", "get failed with ", exception)
            }
        }
    }
    private fun updateDepartmentText(position: String): String {
        var text = ""
        for (departmentID in 0 until departmentArray.size) {
            if (departmentID.toString() == position) {
                text = departmentArray[departmentID]
            }
        }
        return text
    }

    private class DiffCallback : DiffUtil.ItemCallback<AppointmentModel>() {
        override fun areItemsTheSame(oldItem: AppointmentModel, newItem: AppointmentModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AppointmentModel, newItem: AppointmentModel): Boolean {
            return oldItem == newItem
        }
    }
}