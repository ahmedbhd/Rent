package com.rent.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.rent.data.model.locataire.Locataire
import com.rent.data.model.payment.Payment
import com.rent.data.model.rental.Rental
import java.util.*

@Database(entities = [Locataire::class, Rental::class, Payment::class], version = DB_VERSION)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    abstract fun rentalDao(): RentalDao
    abstract fun locataireDao(): LocataireDao
    abstract fun paymentDao(): PaymentDao

}

const val DB_VERSION = 1

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}