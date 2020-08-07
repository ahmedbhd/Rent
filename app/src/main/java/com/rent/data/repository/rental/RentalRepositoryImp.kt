package com.rent.data.repository.rental

import com.rent.base.BaseRepository
import com.rent.data.db.Database
import com.rent.data.model.rental.Rental
import com.rent.global.helper.SharedPreferences
import javax.inject.Inject


class RentalRepositoryImp
@Inject constructor(
    sharedPreferences: SharedPreferences,
    database: Database
) : BaseRepository(sharedPreferences, database), RentalRepository {

    override suspend fun getRentals() = database.rentalDao().getRentals()

    override suspend fun addRental(rental: Rental): Rental {
        database.rentalDao().addRental(rental)
        return database.rentalDao().getLastRental()
    }

    override suspend fun getRentalById(id: Long) = database.rentalDao().getRentalById(id)

    override suspend fun deleteRental(rental: Rental) {
        database.rentalDao().deleteRental(rental)
    }

    override suspend fun updateRental(rental: Rental) = database.rentalDao().updateRental(rental)

    override suspend fun synchronise(rentals: List<Rental>) {
        database.rentalDao().deleteAllRentals()
        database.rentalDao().addAllRentals(*rentals.toTypedArray())
    }
}