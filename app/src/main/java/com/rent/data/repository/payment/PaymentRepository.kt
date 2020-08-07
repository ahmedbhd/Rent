package com.rent.data.repository.payment

import androidx.annotation.WorkerThread
import com.rent.data.model.payment.Payment
import com.rent.data.model.relations.LocataireWithPayment

interface PaymentRepository {

    @WorkerThread
    suspend fun getPayments(): ArrayList<LocataireWithPayment>

    @WorkerThread
    suspend fun addPayment(payment: Payment): Payment

    @WorkerThread
    suspend fun getPaymentById(id: Long): Payment

    @WorkerThread
    suspend fun deletePayment(payment: Payment)

    @WorkerThread
    suspend fun updatePayment(payment: Payment)

    @WorkerThread
    suspend fun getPaymentByRentalId(id: Long): ArrayList<LocataireWithPayment>

    @WorkerThread
    suspend fun synchronise(payment: List<Payment>)
}