package com.example.ihealthdroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class AppointmentAdapter : ListAdapter<AppointmentModel, AppointmentAdapter.AppointmentViewHolder>(DiffCallback()) {

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
            userDepartmentAppointment.text = appointment.appSelectedDepartment
            userDateAppointment.text = appointment.appSelectedDate
            userTimeAppointment.text = appointment.appSelectedTime
        }
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