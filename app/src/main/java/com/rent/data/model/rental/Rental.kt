package com.rent.data.model.rental

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rent.data.model.locataire.Locataire
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Rental")
@JsonClass(generateAdapter = true)
data class Rental(
    @PrimaryKey
    val idRental: Int = 0,
    var dateDebut: String = "",
    var dateFin: String = "",
    var color: String = "",
    @Embedded
    var locataire: Locataire = Locataire()
) : Parcelable
