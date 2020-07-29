package com.rent.global.helper.dialog


class PhoneDialog private constructor(
    val dismissActionBlock: (() -> Unit)? = null
) {

    companion object {
        fun build(
            dismissActionBlock: (() -> Unit)? = null
        ): PhoneDialog {
            return PhoneDialog(
                dismissActionBlock
            )
        }
    }
}