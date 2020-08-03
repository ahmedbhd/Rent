package com.rent.ui.shared.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.rent.R
import com.rent.databinding.DialogCustomCallBinding
import com.rent.global.listener.DialogCustomCallListener

class CustomCallDialog(
    context: Context,
    private var stringTel: String,
    private val dialogCustomCallListener: DialogCustomCallListener,
    private val dismissActionBlock: (() -> Unit)? = null
) : AlertDialog(context, R.style.CustomSimpleDialog) {

    private val binding =
        DataBindingUtil.inflate<DialogCustomCallBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_custom_call,
            null,
            false
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setOnDismissListener {
            dismissActionBlock?.invoke()
            dismiss()
        }

        val addTel1 = binding.callnumberCall

        val myLayout = binding.listcalls

        if (stringTel != "") {
            val tab = stringTel.split(",")
            addTel1.text = tab[0]
            addTel1.setOnClickListener {
                dialogCustomCallListener.onCallClicked(tab[0])
            }
            for (i in 1 until tab.size) {
                val myTextView = TextView(context) // Pass it an Activity or Context
                myTextView.text = tab[i]

                myTextView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.phonesettings,
                    0,
                    0,
                    0
                )
                myTextView.compoundDrawablePadding = 5
                myTextView.gravity = Gravity.CENTER
                myTextView.setPadding(5, 5, 5, 5)
                myTextView.textSize = 20f
                val parms = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ) // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                parms.gravity = Gravity.CENTER
                parms.setMargins(5, 5, 5, 5)
                myTextView.layoutParams = parms
                myLayout.addView(myTextView)
                myTextView.setOnClickListener {
                    dialogCustomCallListener.onCallClicked(tab[i])
                }
            }

        } else {
            addTel1.visibility = View.GONE
        }
    }
}