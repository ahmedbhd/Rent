package com.rent.ui.main.payment

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rent.data.model.payment.Payment
import com.rent.global.listener.PaymentItemClickListener
import com.rent.global.listener.PaymentItemSwipeListener


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