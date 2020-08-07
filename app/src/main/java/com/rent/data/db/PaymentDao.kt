package com.rent.data.db

import androidx.room.*
import com.rent.data.model.payment.Payment

@Dao
interface PaymentDao {

    @Query("SELECT * from payment")
    suspend fun getPayments(): List<Payment>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePayment(payments: Payment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPayment(payment: Payment)

    @Delete
    suspend fun deletePayment(payment: Payment)

    @Query("SELECT * from payment where idPayment=:id LIMIT 1")
    suspend fun getPaymentById(id: Long): Payment

    @Query("SELECT * from payment where idPayment = (SELECT MAX (idPayment) from payment) LIMIT 1")
    suspend fun getLastPayment(): Payment

    @Query("SELECT * from payment where rentalId=:id")
    suspend fun getPaymentByRentalId(id: Long): List<Payment>

    @Query("DELETE from payment")
    suspend fun deleteAllPayments()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllPayments(vararg payments: Payment)
}