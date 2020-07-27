package com.rent.global.helper.dialog

import android.content.Context
import androidx.annotation.StringRes

class SimpleDialog
private constructor(
    val title: String? = null,
    val message: String,
    val ok: String,
    val okActionBlock: (() -> Unit)? = null,
    val dismissActionBlock: (() -> Unit)? = null

) {

    companion object {
        fun build(
            title: String? = null,
            message: String,
            ok: String,
            actionBlock: (() -> Unit)? = null,
            dismissActionBlock: (() -> Unit)? = null
        ): SimpleDialog {
            return SimpleDialog(title, message, ok, actionBlock, dismissActionBlock)
        }

        fun build(
            context: Context,
            @StringRes titleId: Int? = null,
            @StringRes messageId: Int,
            @StringRes okId: Int,
            actionBlock: (() -> Unit)? = null,
            dismissActionBlock: (() -> Unit)? = null
        ): SimpleDialog {
            val title = titleId?.let { context.getString(it) }
            return SimpleDialog(
                title,
                context.getString(messageId),
                context.getString(okId),
                actionBlock,
                dismissActionBlock
            )
        }
    }
}