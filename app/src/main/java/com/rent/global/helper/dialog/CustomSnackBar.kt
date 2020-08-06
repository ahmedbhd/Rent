package com.rent.global.helper.dialog


class CustomSnackBar private constructor(
    val message: String,
    val actionBlock: (() -> Unit)? = null
) {

    companion object {
        fun build(
            message: String,
            actionBlock: (() -> Unit)? = null
        ): CustomSnackBar {
            return CustomSnackBar(
                message,
                actionBlock
            )
        }
    }
}