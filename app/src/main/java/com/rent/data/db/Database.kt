package com.rent.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rent.data.model.locataire.Locataire
import com.rent.data.model.payment.Payment
import com.rent.data.model.rental.Rental

@Database(entities = [Locataire::class, Rental::class, Payment::class], version = DB_VERSION)
abstract class Database : RoomDatabase() {

    abstract fun rentalDao(): RentalDao
    abstract fun locataireDao(): LocataireDao
    abstract fun paymentDao(): PaymentDao

}

const val DB_VERSION = 1
