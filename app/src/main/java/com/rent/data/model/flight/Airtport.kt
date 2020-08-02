package com.rent.data.model.flight

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Airport(val city: String, val code: String) : Parcelable