package com.rent.global.helper

import com.rent.data.model.relations.RentalWithLocataire


sealed class Navigation {

    object Back : Navigation()

    data class RentalDetailActivityNavigation(val rentalAndLocataire: RentalWithLocataire) : Navigation()
}