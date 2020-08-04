package com.rent.ui.main.payment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import java.util.*
import android.widget.ArrayAdapter
import com.rent.data.PaymentServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import android.app.Activity
import com.rent.data.model.payment.Payment
import com.rent.data.model.rental.Rental
import com.rent.global.listener.PaymentItemClickListener
import com.rent.global.listener.PaymentItemSwipeListener
import com.rent.ui.shared.view.ViewDialog
import com.rent.tools.PhoneGrantings
import kotlin.collections.ArrayList


/**
 * Created by Sanjeev k Saroj on 28/2/17.
 */

class PaymentListAdapter : RecyclerView.Adapter<PaymentViewHolder>() {

    private var payments: ArrayList<Payment> = ArrayList()

    private var paymentItemClickListener: PaymentItemClickListener? = null
    private var paymentItemSwipeListener: PaymentItemSwipeListener? = null

    private var removedPosition: Int = 0
    private var removedItem: Payment? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PaymentViewHolder {
        return PaymentViewHolder.create(viewGroup, paymentItemClickListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: PaymentViewHolder, position: Int) {
        viewHolder.bind(payments[position])
    }

    fun setData(list: ArrayList<Payment>){
        payments = list
        notifyDataSetChanged()
    }

    fun setClickListener(listener: PaymentItemClickListener){
        paymentItemClickListener = listener
    }

    fun setSwipeListener(listener: PaymentItemSwipeListener){
        paymentItemSwipeListener = listener
    }

    fun removeItem(position: Int) {
        paymentItemSwipeListener?.onPaymentItemSwiped(position)
    }

    override fun getItemCount() = payments.size

}