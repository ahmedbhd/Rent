package com.rent.ui.shared.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.rent.R
import com.rent.databinding.DialogCustomChooseBinding
import com.rent.global.utils.setClickWithDebounce

class CustomChooseDialog : AlertDialog {

    private val binding =
        DataBindingUtil.inflate<DialogCustomChooseBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_custom_choose,
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
        cancel: String,
        okActionBlock: (() -> Unit)? = null,
        cancelActionBlock: (() -> Unit)? = null,
        dismissActionBlock: (() -> Unit)? = null
    ) : super(context, R.style.CustomSimpleDialog) {
        setCancelable(false)
        binding.title = title
        binding.message = message
        binding.okBtnText = ok
        binding.cancelBtnText = cancel

        binding.imgChooseCustomDialogClose.setClickWithDebounce {
            dismiss()
        }
        binding.btnChooseCustomDialogOk.setClickWithDebounce {
            okActionBlock?.invoke()
            dismiss()
        }
        binding.btnChooseCustomDialogCancel.setClickWithDebounce {
            cancelActionBlock?.invoke()
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