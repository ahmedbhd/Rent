package com.rent.data.model.rental

import android.os.Parcelable
import androidx.room.*
import com.rent.data.model.locataire.Locataire
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(
    tableName = "Rental", foreignKeys = (arrayOf(
        ForeignKey(
            entity = Locataire::class,
            onDelete = ForeignKey.CASCADE,
            parentColumns = arrayOf("idLocataire"),
            childColumns = arrayOf("locataireOwnerId")
        )
    ))
)
@JsonClass(generateAdapter = true)
data class Rental(
    @PrimaryKey(autoGenerate = true)
    val idRental: Long = 0,
    var dateDebut: Date = Date(),
    var dateFin: Date = Date(),
    var color: String = "",
    var locataireOwnerId: Long = 0
) : Parcelable
