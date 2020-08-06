package com.rent.global.listener

import com.rent.data.model.relations.RentalWithLocataire

interface RentalItemClickListener {
    fun onRentalItemClicked(rentalAndLocataire: RentalWithLocataire)
}