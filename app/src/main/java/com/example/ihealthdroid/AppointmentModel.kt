package com.example.ihealthdroid

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
    data class AppointmentModel(
        val appUserName: String = "",
        val appUserPhone: String = "",
        val appUserProvince: String = "",
        val appSelectedDepartment: String = "",
        val appSelectedDate: String = "",
        val appSelectedTime: String = "",
        val appSymptomsInfo: String = "",
        val appStatus: String = ""
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
            parcel.writeString(appUserName)
            parcel.writeString(appUserPhone)
            parcel.writeString(appUserProvince)
            parcel.writeString(appSelectedDepartment)
            parcel.writeString(appSelectedDate)
            parcel.writeString(appSelectedTime)
            parcel.writeString(appSymptomsInfo)
            parcel.writeString(appStatus)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<AppointmentModel> {
            override fun createFromParcel(parcel: Parcel): AppointmentModel {
                return AppointmentModel(parcel)
            }

            override fun newArray(size: Int): Array<AppointmentModel?> {
                return arrayOfNulls(size)
            }
        }
    }