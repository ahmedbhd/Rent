package com.rent.global.helper.dialog

import com.rent.global.listener.DialogCustomCallListener

class CallDialog private constructor(
    val stringTel: String,
    val dialogCustomCallListener: DialogCustomCallListener,
    val dismissActionBlock: (() -> Unit)? = null
) {

    companion object {
        fun build(
            stringTel: String,
            dialogCustomCallListener: DialogCustomCallListener,
            dismissActionBlock: (() -> Unit)? = null
        ): CallDialog {
            return CallDialog(
                stringTel,
                dialogCustomCallListener,
                dismissActionBlock
            )
        }
    }
}