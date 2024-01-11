package com.example.ihealthdroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CustomProfileAdapter : ListAdapter<ProfileModel, CustomProfileAdapter.ProfileViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medical_profile_recycle_addon, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = getItem(position)
        holder.bind(profile)
    }

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameView: TextView = itemView.findViewById(R.id.userNameView)
        private val userPhoneView: TextView = itemView.findViewById(R.id.userPhoneView)


        fun bind(profile: ProfileModel) {
            userNameView.text = profile.name
            userPhoneView.text = profile.phone
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ProfileModel>() {
        override fun areItemsTheSame(oldItem: ProfileModel, newItem: ProfileModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ProfileModel, newItem: ProfileModel): Boolean {
            return oldItem == newItem
        }
    }
}