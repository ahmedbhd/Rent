package com.rent.data.repository.payment

import androidx.annotation.WorkerThread
import com.rent.data.model.payment.Payment

interface PaymentRepository {

    @WorkerThread
    suspend fun getPayments(): List<Payment>

    @WorkerThread
    suspend fun addPayment(payment: Payment): Payment

    @WorkerThread
    suspend fun getPaymentById(id: Int): Payment

    @WorkerThread
    suspend fun deletePayment(payment: Payment)

    @WorkerThread
    suspend fun updatePayment(payment: Payment)

    @WorkerThread
    suspend fun getPaymentByRentalId(id: Int): List<Payment>
}