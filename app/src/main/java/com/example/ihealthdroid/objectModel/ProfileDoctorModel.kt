package com.example.ihealthdroid.objectModel

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class ProfileDoctorModel(
    val doctorName: String = "",
    val doctorSex: String = "",
    val doctorDepartment: String = "",
) : Parcelable, Serializable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(doctorName)
        parcel.writeString(doctorSex)
        parcel.writeString(doctorDepartment)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProfileDoctorModel> {
        override fun createFromParcel(parcel: Parcel): ProfileDoctorModel {
            return ProfileDoctorModel(parcel)
        }

        override fun newArray(size: Int): Array<ProfileDoctorModel?> {
            return arrayOfNulls(size)
        }
    }
}