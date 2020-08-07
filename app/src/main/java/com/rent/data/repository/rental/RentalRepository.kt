package com.rent.data.repository.rental

import androidx.annotation.WorkerThread
import com.rent.data.model.rental.Rental

interface RentalRepository {
    @WorkerThread
    suspend fun getRentals(): List<Rental>

    @WorkerThread
    suspend fun addRental(rental: Rental): Rental

    @WorkerThread
    suspend fun getRentalById(id: Long): Rental

    @WorkerThread
    suspend fun deleteRental(rental: Rental)

    @WorkerThread
    suspend fun updateRental(rental: Rental)

    @WorkerThread
    suspend fun synchronise(rentals: List<Rental>)
}