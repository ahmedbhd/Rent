package com.rent.global.helper.dialog


class PhoneDialog private constructor(
    val stringTel: String = "",
    val dismissActionBlock: (() -> Unit)? = null
) {

    companion object {
        fun build(
            stringTel: String = "",
            dismissActionBlock: (() -> Unit)? = null
        ): PhoneDialog {
            return PhoneDialog(
                stringTel,
                dismissActionBlock
            )
        }
    }
}