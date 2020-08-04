package com.rent.data.repository.payment

import com.rent.base.BaseRepository
import com.rent.data.db.Database
import com.rent.data.model.payment.Payment
import com.rent.global.helper.SharedPreferences
import javax.inject.Inject

class PaymentRepositoryImp
@Inject constructor(
    sharedPreferences: SharedPreferences,
    database: Database
) : BaseRepository(sharedPreferences, database), PaymentRepository {
    override suspend fun getPayments() = database.paymentDao().getPayments()

    override suspend fun addPayment(payment: Payment): Payment {
        database.paymentDao().addPayment(payment)
        return database.paymentDao().getLastPayment()
    }

    override suspend fun getPaymentById(id: Int) = database.paymentDao().getPaymentById(id)

    override suspend fun deletePayment(payment: Payment) =
        database.paymentDao().deletePayment(payment)

    override suspend fun updatePayment(payment: Payment) =
        database.paymentDao().updatePayment(payment)

    override suspend fun getPaymentByRentalId(id: Int) =
        database.paymentDao().getPaymentByRentalId(id)
}