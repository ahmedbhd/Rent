package com.rent.data.db

import androidx.room.*
import com.rent.data.model.rental.Rental

@Dao
interface RentalDao {

    @Query("SELECT * from rental")
    suspend fun getRentals(): List<Rental>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRental(rental: Rental)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRental(rental: Rental)

    @Delete
    suspend fun deleteRental(rental: Rental)

    @Query("SELECT * from rental where idRental=:id LIMIT 1")
    suspend fun getRentalById(id: Int): Rental

    @Query("SELECT * from rental where idRental = (SELECT MAX (idRental) from rental) LIMIT 1")
    suspend fun getLastRental(): Rental
}