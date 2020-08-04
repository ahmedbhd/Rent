package com.rent.global.helper.dialog

import androidx.lifecycle.MutableLiveData
import com.rent.data.model.payment.Payment

class PaymentsBottomSheet private constructor(
    val payments: MutableLiveData<ArrayList<Payment>>,
    val dismissActionBlock: (() -> Unit)? = null
) {

    companion object {
        fun build(
            payments: MutableLiveData<ArrayList<Payment>>,
            dismissActionBlock: (() -> Unit)? = null
        ): PaymentsBottomSheet {
            return PaymentsBottomSheet(
                payments,
                dismissActionBlock
            )
        }
    }
}