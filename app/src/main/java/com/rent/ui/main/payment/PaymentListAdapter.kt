package com.rent.ui.main.payment

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rent.data.model.relations.LocataireWithPayment
import com.rent.global.listener.PaymentItemClickListener
import com.rent.global.listener.PaymentItemSwipeListener


class PaymentListAdapter : RecyclerView.Adapter<PaymentViewHolder>() {

    private var payments: ArrayList<LocataireWithPayment> = ArrayList()

    private var paymentItemClickListener: PaymentItemClickListener? = null
    private var paymentItemSwipeListener: PaymentItemSwipeListener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PaymentViewHolder {
        return PaymentViewHolder.create(viewGroup, paymentItemClickListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: PaymentViewHolder, position: Int) {
        viewHolder.bind(payments[position])
    }

    fun setData(list: ArrayList<LocataireWithPayment>) {
        payments = list
        notifyDataSetChanged()
    }

    fun setClickListener(listener: PaymentItemClickListener) {
        paymentItemClickListener = listener
    }

    fun setSwipeListener(listener: PaymentItemSwipeListener) {
        paymentItemSwipeListener = listener
    }

    fun removeItem(position: Int) {
        paymentItemSwipeListener?.onPaymentItemSwiped(payments[position], position)
    }

    override fun getItemCount() = payments.size

}