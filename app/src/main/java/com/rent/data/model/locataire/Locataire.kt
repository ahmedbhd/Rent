package com.rent.data.model.locataire

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Locataire")
@JsonClass(generateAdapter = true)
data class Locataire(
    @PrimaryKey(autoGenerate = true)
    val idLocataire: Long = 0,
    var cin: String = "",
    var fullName: String = "",
    var numTel: String = ""
) : Parcelable
