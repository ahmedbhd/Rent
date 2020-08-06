package com.rent.global.listener

import com.rent.data.model.relations.LocataireWithPayment


interface PaymentItemSwipeListener {
    fun onPaymentItemSwiped(locataireWithPayment: LocataireWithPayment, position: Int)
}