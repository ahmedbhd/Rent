package com.rent.data.model.relations

import android.os.Parcelable
import com.rent.data.model.locataire.Locataire
import com.rent.data.model.payment.Payment
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class LocataireWithPayment(
    val locataire: Locataire,
    val payment: Payment
) : Parcelable