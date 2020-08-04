package com.rent.data.model.payment

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rent.data.model.rental.Rental
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Payment")
@JsonClass(generateAdapter = true)
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val idPayment: Int,
    val paymentDate: String,
    val amount: Int,
    val type: String,
    @Embedded
    val rental: Rental = Rental()
) : Parcelable
