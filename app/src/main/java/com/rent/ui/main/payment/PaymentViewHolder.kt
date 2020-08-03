package com.rent.ui.main.payment

import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rent.data.model.payment.Payment
import com.rent.data.model.rental.Rental
import com.rent.databinding.PaymentListItemBinding
import com.rent.global.listener.PaymentItemClickListener
import com.rent.ui.shared.view.ViewDialog


class PaymentViewHolder private constructor(
    private val binding: PaymentListItemBinding,
    private val paymentItemClickListener: PaymentItemClickListener?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(payment:Payment) {
        binding.textmontant.text =
            payment.amount.toString() + " ( " + payment.type + " )"
        binding.description.text = payment.paymentDate
        binding.tvName.text = payment.rental.locataire.fullName

        binding.root.setOnClickListener {
            paymentItemClickListener?.onPaymentItemClicked(payment)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            paymentItemClickListener: PaymentItemClickListener?
        ): PaymentViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PaymentListItemBinding.inflate(inflater, parent, false)
            return PaymentViewHolder(
                binding,
                paymentItemClickListener
            )
        }
    }
}