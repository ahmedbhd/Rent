package com.rent.global.helper.dialog

import com.rent.data.model.payment.Payment
import com.rent.global.listener.PaymentDialogListener

class PaymentDialog private constructor(
    val payment: Payment,
    val paymentDialogListener: PaymentDialogListener,
    val dismissActionBlock: (() -> Unit)? = null
) {

    companion object {
        fun build(
            payment: Payment,
            paymentDialogListener: PaymentDialogListener,
            dismissActionBlock: (() -> Unit)? = null
        ): PaymentDialog {
            return PaymentDialog(
                payment,
                paymentDialogListener,
                dismissActionBlock
            )
        }
    }
}