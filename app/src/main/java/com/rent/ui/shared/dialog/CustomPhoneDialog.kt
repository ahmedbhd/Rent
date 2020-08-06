package com.rent.ui.shared.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.rent.R
import com.rent.databinding.DialogCustomPhoneBinding
import com.rent.global.listener.PhoneDialogListener
import com.rent.global.utils.hideKeyboard


class CustomPhoneDialog(
    context: Context,
    private var stringTel: String,
    private var phoneDialogListener: PhoneDialogListener,
    private var dismissActionBlock: (() -> Unit)? = null
) : Dialog(context, R.style.CustomSimpleDialog) {
    private val binding =
        DataBindingUtil.inflate<DialogCustomPhoneBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_custom_phone,
            null,
            false
        )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)

        val txt = binding.txtcloseTel
        val imgAdd = binding.addTelimg
        val addTel1 = binding.addtel

        val btn = binding.saveTel
        val myLayout = binding.listnumbers


        val telArray = ArrayList<EditText>()

        if (stringTel != "") {
            val tab = stringTel.split(",")
            addTel1.setText(tab[0])
            for (i in 1 until tab.size) {
                val myEditText = EditText(context) // Pass it an Activity or Context
                myEditText.setTextColor(Color.BLACK)
                myEditText.hint = context.getString(R.string.global_phone)
                myEditText.setHintTextColor(ContextCompat.getColor(context, R.color.blue_grey_700))
                myEditText.inputType = InputType.TYPE_CLASS_PHONE
                myEditText.setText(tab[i])
                myEditText.layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ) // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                myLayout.addView(myEditText)
                telArray.add(myEditText)
            }

        }

        imgAdd.setOnClickListener {
            binding.rootView.hideKeyboard()
            val myEditText = EditText(context) // Pass it an Activity or Context
            myEditText.setTextColor(Color.BLACK)
            myEditText.hint = context.getString(R.string.global_phone)
            myEditText.setHintTextColor(ContextCompat.getColor(context, R.color.blue_grey_700))
            myEditText.inputType = InputType.TYPE_CLASS_PHONE
            myEditText.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ) // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
            myLayout.addView(myEditText)
            telArray.add(myEditText)
        }

        txt.setOnClickListener {
            binding.rootView.hideKeyboard()
            dismiss()
        }

        btn.setOnClickListener {
            binding.rootView.hideKeyboard()
            stringTel = addTel1.text.toString()
            telArray.forEach {
                stringTel = stringTel + "," + it.text.toString()
            }
            println(stringTel)
            phoneDialogListener.onSaveClicked(stringTel)
            dismiss()
        }

        setOnDismissListener {
            dismissActionBlock?.invoke()
            dismiss()
        }
    }
}