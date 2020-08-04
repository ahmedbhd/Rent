package com.rent.ui.shared.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.rent.R
import com.rent.data.model.payment.Payment
import com.rent.databinding.DialogCustomPaymentBinding
import com.rent.global.listener.PaymentDialogListener
import com.rent.global.utils.hideKeyboard
import java.util.*


class CustomPaymentDialog(
    context: Context,
    private var payment: Payment,
    private var paymentDialogListener: PaymentDialogListener,
    private var dismissActionBlock: (() -> Unit)? = null
) : Dialog(context, R.style.CustomSimpleDialog) {
    private val binding =
        DataBindingUtil.inflate<DialogCustomPaymentBinding>(
            LayoutInflater.from(context),
            R.layout.dialog_custom_payment,
            null,
            false
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)

        var typePayment = ""
        setOnDismissListener {
            dismissActionBlock?.invoke()
            dismiss()
        }

        binding.addAmount.setText(payment.amount.toString(), TextView.BufferType.EDITABLE)

        val c = Calendar.getInstance(Locale.FRANCE)
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMinute = c.get(Calendar.MINUTE)
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        binding.addTime.text = "$mHour:$mMinute"
        binding.addDate.text = mYear.toString() + "-" + (mMonth + 1) + "-" + (mDay)



        binding.imageTime.setOnClickListener {

            val timePickerDialog = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int
                    ->
                    binding.addTime.text = "$hourOfDay:$minute"
                },
                mHour, mMinute, true
            )
            timePickerDialog.show()
        }

        binding.imageDate.setOnClickListener {

            val datePickerDialog = DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth
                    ->
                    binding.addDate.text =
                        "${year.toString()}  -  ${(monthOfYear + 1)} -  $dayOfMonth"
                },
                mYear, // Initial year selection
                mMonth, // Initial month selection
                mDay // Inital day selection
            )
            datePickerDialog.show()
        }

        val users = arrayOf("Avance", "Paiment")
        val spinner: Spinner = binding.typesSpinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(context, R.layout.drop_down_list_types, users)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                println("hhhhhhhhh" + parent.getItemAtPosition(pos))
                typePayment = parent.getItemAtPosition(pos).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        val spinnerAdap = spinner.adapter as ArrayAdapter<String>

        if (payment.type == "payment") {
            val spinnerPosition = spinnerAdap.getPosition("Paiement")

            spinner.setSelection(spinnerPosition)
        }
        binding.txtclose.setOnClickListener {
            binding.root.hideKeyboard()
            dismiss()
        }
        binding.savePayment.setOnClickListener {
            paymentDialogListener.onSavePaymentButtonClicked(
                Payment(
                    payment.idPayment,
                    binding.addDate.text.toString() + " " + binding.addTime.text.toString() + ":00",
                    Integer.parseInt(binding.addAmount.text.toString()),
                    typePayment,
                    payment.rental
                )
            )
            binding.root.hideKeyboard()
            dismiss()
        }
    }
}