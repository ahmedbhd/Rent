package com.rent.global.listener

import com.rent.data.model.rental.Rental

interface RentalItemClickListener {
    fun onRentalItemClicked(rental:Rental)
}