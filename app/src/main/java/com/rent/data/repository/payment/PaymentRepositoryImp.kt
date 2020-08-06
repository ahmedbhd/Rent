package com.rent.data.repository.payment

import com.rent.base.BaseRepository
import com.rent.data.db.Database
import com.rent.data.model.payment.Payment
import com.rent.data.model.relations.LocataireWithPayment
import com.rent.global.helper.SharedPreferences
import javax.inject.Inject

class PaymentRepositoryImp
@Inject constructor(
    sharedPreferences: SharedPreferences,
    database: Database
) : BaseRepository(sharedPreferences, database), PaymentRepository {

    override suspend fun getPayments(): ArrayList<LocataireWithPayment> {
        val payments = database.paymentDao().getPayments()
        val rentals = database.rentalDao().getRentals()
        return ArrayList<LocataireWithPayment>().apply {
            payments.forEach {
                val locataire = rentals.first { row -> row.idRental == it.rentalId }
                add(
                    LocataireWithPayment(
                        database.locataireDao().getLocataireById(locataire.locataireOwnerId),
                        it
                    )
                )
            }
        }
    }

    override suspend fun addPayment(payment: Payment): Payment {
        database.paymentDao().addPayment(payment)
        return database.paymentDao().getLastPayment()
    }

    override suspend fun getPaymentById(id: Long) = database.paymentDao().getPaymentById(id)

    override suspend fun deletePayment(payment: Payment) =
        database.paymentDao().deletePayment(payment)

    override suspend fun updatePayment(payment: Payment) =
        database.paymentDao().updatePayment(payment)

    override suspend fun getPaymentByRentalId(id: Long): ArrayList<LocataireWithPayment> {
        val payments = database.paymentDao().getPaymentByRentalId(id)
        val locataire = database.locataireDao()
            .getLocataireById(database.rentalDao().getRentalById(id).locataireOwnerId)
        return ArrayList<LocataireWithPayment>().apply {
            payments.forEach {
                add(
                    LocataireWithPayment(
                        locataire,
                        it
                    )
                )
            }
        }
    }
}