package com.rent.ui.shared.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.rent.R
import com.rent.databinding.DialogCustomSimpleBinding
import com.rent.global.utils.setClickWithDebounce

class CustomSimpleDialog : AlertDialog {

    private val binding =
        DataBindingUtil.inflate<DialogCustomSimpleBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_custom_simple,
            null,
            false
        )

    constructor(context: Context) : super(context)

    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener
    ) : super(context, cancelable, cancelListener)

    constructor(context: Context, @StyleRes themeResId: Int) : super(context, themeResId)

    constructor(
        context: Context,
        title: String? = null,
        message: String,
        ok: String,
        actionBlock: (() -> Unit)? = null,
        dismissActionBlock: (() -> Unit)? = null
    ) : super(context, R.style.CustomSimpleDialog) {
        setCancelable(false)
        binding.title = title
        binding.message = message
        binding.okBtnText = ok

        binding.imgSimpleCustomDialogClose.setClickWithDebounce {
            dismiss()
        }

        binding.btnSimpleCustomDialogOk.setClickWithDebounce {
            actionBlock?.invoke()
            dismiss()
        }
        setOnDismissListener {
            dismissActionBlock?.invoke()
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}