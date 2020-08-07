package com.rent.data.model.payment

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.rent.data.model.rental.Rental
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "Payment", foreignKeys = (arrayOf(
    ForeignKey(
        entity = Rental::class,
        onDelete = ForeignKey.CASCADE,
        parentColumns = arrayOf("idRental"),
        childColumns = arrayOf("rentalId")
    )
)))
@JsonClass(generateAdapter = true)
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val idPayment: Long = 0,
    val paymentDate: Date = Date(),
    val amount: Int = 0,
    val type: String = "",
    val rentalId: Long = 0
) : Parcelable
