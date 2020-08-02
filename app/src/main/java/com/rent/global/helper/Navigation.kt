package com.rent.global.helper

import com.rent.data.model.rental.Rental


sealed class Navigation {

    object Back : Navigation()

    data class RentalDetailActivityNavigation(val rental: Rental) : Navigation()
}