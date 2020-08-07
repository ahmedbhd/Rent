package com.rent.ui.main.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rent.R
import com.rent.data.model.relations.LocataireWithPayment
import com.rent.databinding.PaymentListItemBinding
import com.rent.global.listener.PaymentItemClickListener
import com.rent.global.utils.formatDate


class PaymentViewHolder private constructor(
    private val binding: PaymentListItemBinding,
    private val paymentItemClickListener: PaymentItemClickListener?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(locataireWithPayment: LocataireWithPayment) {
        binding.textmontant.text = binding.root.context.getString(
            R.string.payment_dialog_amount_text,
            locataireWithPayment.payment.amount,
            locataireWithPayment.payment.type
        )
        binding.description.text = formatDate.format(locataireWithPayment.payment.paymentDate)
        binding.tvName.text = locataireWithPayment.locataire.fullName

        binding.root.setOnClickListener {
            paymentItemClickListener?.onPaymentItemClicked(locataireWithPayment.payment)
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