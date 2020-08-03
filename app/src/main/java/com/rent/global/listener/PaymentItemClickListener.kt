package com.rent.global.listener

import com.rent.data.model.payment.Payment

interface PaymentItemClickListener {
    fun onPaymentItemClicked(payment:Payment)
}