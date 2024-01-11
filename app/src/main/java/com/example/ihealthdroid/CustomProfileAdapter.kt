package com.example.ihealthdroid

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ihealthdroid.ui.theme.IHealthDroidTheme

class CustomProfileAdapter : ListAdapter<ProfileModel, CustomProfileAdapter.ProfileViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = getItem(position)
        holder.bind(profile)
    }

    inner class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameView: TextView = itemView.findViewById(R.id.tv_show_name)
        private val userPhoneView: TextView = itemView.findViewById(R.id.tv_show_date)

        fun bind(profile: ProfileModel) {
            userNameView.text = profile.name
            userPhoneView.text = profile.phone
        }

        val btnDetail: Button = itemView.findViewById(R.id.btn_detail)
        init {
            btnDetail.setOnClickListener {
                val intent = Intent(itemView.context, ProfileDetailActivity::class.java)
                itemView.context.startActivity(intent)
            }
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