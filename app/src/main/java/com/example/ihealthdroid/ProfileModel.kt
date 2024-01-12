package com.example.ihealthdroid

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class ProfileModel(
    val name: String = "",
    val dob: String = "",
    val sex: String = "",
    val phone: String = "",
    val citizenID: String = "",
    val ethnic: String = "",
    val province: String = "",
    val district: String = ""
) : Parcelable, Serializable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(dob)
        parcel.writeString(sex)
        parcel.writeString(phone)
        parcel.writeString(citizenID)
        parcel.writeString(ethnic)
        parcel.writeString(province)
        parcel.writeString(district)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProfileModel> {
        override fun createFromParcel(parcel: Parcel): ProfileModel {
            return ProfileModel(parcel)
        }

        override fun newArray(size: Int): Array<ProfileModel?> {
            return arrayOfNulls(size)
        }
    }
}