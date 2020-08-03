package com.rent.global.listener

import com.rent.data.model.payment.Payment

interface PaymentDialogListener {
    fun onSavePaymentButtonClicked(payment: Payment)
}