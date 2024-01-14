package com.example.ihealthdroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ihealthdroid.objectModel.ProfileModel
import com.example.ihealthdroid.R

class CustomProfileAdapter : ListAdapter<ProfileModel, CustomProfileAdapter.ProfileViewHolder>(
    DiffCallback()
) {

    private var onItemClickListener: ((ProfileModel) -> Unit)? = null

    fun setOnItemClickListener(listener: (ProfileModel) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = getItem(position)
        holder.bind(profile)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(profile)
        }
    }

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameView: TextView = itemView.findViewById(R.id.tv_show_name)
        private val userPhoneView: TextView = itemView.findViewById(R.id.tv_show_phone)
        private val userDOBView: TextView = itemView.findViewById(R.id.tv_show_date)
        private val userProvinceView: TextView = itemView.findViewById(R.id.tv_show_province)

        fun bind(profile: ProfileModel) {
            userNameView.text = profile.name
            userPhoneView.text = profile.phone
            userDOBView.text = profile.dob
            userProvinceView.text = profile.province
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