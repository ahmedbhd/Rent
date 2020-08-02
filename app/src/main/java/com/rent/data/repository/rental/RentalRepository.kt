package com.rent.data.repository.rental

import androidx.annotation.WorkerThread
import com.rent.data.model.rental.Rental

interface RentalRepository {
    @WorkerThread
    suspend fun selectRentals(): List<Rental>

    @WorkerThread
    suspend fun addRental(rental: Rental): Rental

    @WorkerThread
    suspend fun selectRentalById(id: Int): Rental

    @WorkerThread
    suspend fun deleteRental(rental: Rental)

    @WorkerThread
    suspend fun updateRental(rental: Rental)
}