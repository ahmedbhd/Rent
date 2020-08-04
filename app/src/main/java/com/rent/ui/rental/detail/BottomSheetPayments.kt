package com.rent.ui.rental.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rent.R
import com.rent.data.model.payment.Payment
import com.rent.databinding.BottomSheetPaymentsBinding
import com.rent.ui.main.payment.PaymentListAdapter

class BottomSheetPayments(
    context: Context,
    private val paymentListAdapter: PaymentListAdapter,
    private val payments: MutableLiveData<ArrayList<Payment>>
) : BottomSheetDialog(context) {
    private val binding: BottomSheetPaymentsBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.bottom_sheet_payments,
            null,
            true
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setCancelable(false)
        binding.executePendingBindings()
        setContentView(binding.root)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}