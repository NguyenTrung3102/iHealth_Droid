package com.example.ihealthdroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ihealthdroid.R
import com.example.ihealthdroid.objectModel.EmergencyModel

class CustomEmergencyAdapter : ListAdapter<EmergencyModel, CustomEmergencyAdapter.EmergencyViewHolder>(
    DiffCallback()
) {

    private var onItemClickListener: ((EmergencyModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (EmergencyModel) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmergencyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_emergency, parent, false)
        return EmergencyViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmergencyViewHolder, position: Int) {
        val emergency = getItem(position)
        holder.bind(emergency)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(emergency)
        }
    }

    inner class EmergencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val emergencyDistrictView: TextView = itemView.findViewById(R.id.tv_show_distric)
        private val emergencyNameView: TextView = itemView.findViewById(R.id.tv_show_name)
        private val emergencyPhoneView: TextView = itemView.findViewById(R.id.tv_show_phone)

        fun bind(emergency: EmergencyModel) {
            emergencyDistrictView.text = emergency.district
            emergencyNameView.text = emergency.name
            emergencyPhoneView.text = emergency.phone
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<EmergencyModel>() {
        override fun areItemsTheSame(oldItem: EmergencyModel, newItem: EmergencyModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: EmergencyModel, newItem: EmergencyModel): Boolean {
            return oldItem == newItem
        }
    }
}