package com.rent.data.model.flight

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Flight(
    val idRental: Long,
    val time: LocalDateTime,
    val departure: Airport,
    val destination: Airport,
    val color: Int
) : Parcelable