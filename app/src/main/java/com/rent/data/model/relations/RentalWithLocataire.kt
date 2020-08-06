package com.rent.data.model.relations

import android.os.Parcelable
import com.rent.data.model.locataire.Locataire
import com.rent.data.model.rental.Rental
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class RentalWithLocataire (
    val rental: Rental,
    val locataire: Locataire
): Parcelable