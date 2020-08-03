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
import java.util.*


class CustomPaymentDialog(
    context: Context,
    private var payment: Payment? = null,
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

        val txt: TextView = binding.txtclose
        val btn: Button = binding.btnfollow

        val amount: EditText = binding.addAmount
        val dateBtn: ImageView = binding.imageDate
        val timeBtn: ImageView = binding.imageTime
        val dateText: TextView = binding.addDate

        val time = binding.addTime

        amount.setText(payment?.amount.toString(), TextView.BufferType.EDITABLE)

        val c = Calendar.getInstance(Locale.FRANCE)
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMinute = c.get(Calendar.MINUTE)
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        time.text = "$mHour:$mMinute"
        dateText.text = mYear.toString() + "-" + (mMonth + 1) + "-" + (mDay)



        timeBtn.setOnClickListener {

            val timePickerDialog = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int
                    ->
                    time.text = "$hourOfDay:$minute"
                },
                mHour, mMinute, true
            )
            timePickerDialog.show()
        }

        dateBtn.setOnClickListener {

            val datePickerDialog = DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth
                    ->
                    dateText.text = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
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

        if (payment?.type == "payment") {
            val spinnerPosition = spinnerAdap.getPosition("Paiement")

            spinner.setSelection(spinnerPosition)
        }
        txt.setOnClickListener { dismiss() }
        btn.setOnClickListener {
            paymentDialogListener.onSavePaymentButtonClicked(
                Payment(
                    payment?.idPayment ?: 0,
                    dateText.text.toString() + " " + time.text.toString() + ":00",
                    Integer.parseInt(amount.text.toString()),
                    typePayment,
                    payment!!.rental
                )
            )
            dismiss()
        }
    }
}