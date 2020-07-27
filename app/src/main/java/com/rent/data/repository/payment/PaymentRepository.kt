package com.rent.data.repository.payment

import androidx.annotation.WorkerThread
import com.rent.data.model.payment.Payment

interface PaymentRepository {

    @WorkerThread
    suspend fun selectPayments(): List<Payment>

    @WorkerThread
    suspend fun ajouterPayment(payment: Payment)

    @WorkerThread
    suspend fun selectPaymentById(id: Int): Payment

    @WorkerThread
    suspend fun deletePayment(payment: Payment)

    @WorkerThread
    suspend fun updatePayment(payment: Payment)

}