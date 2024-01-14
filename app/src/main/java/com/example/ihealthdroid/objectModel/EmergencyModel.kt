package com.example.ihealthdroid.objectModel

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class EmergencyModel(
    val district: String = "",
    val name: String = "",
    val phone: String = "",
) : Parcelable, Serializable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(district)
        parcel.writeString(name)
        parcel.writeString(phone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EmergencyModel> {
        override fun createFromParcel(parcel: Parcel): EmergencyModel {
            return EmergencyModel(parcel)
        }

        override fun newArray(size: Int): Array<EmergencyModel?> {
            return arrayOfNulls(size)
        }
    }
}