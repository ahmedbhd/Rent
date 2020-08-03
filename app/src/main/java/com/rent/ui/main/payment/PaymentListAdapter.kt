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

//
//    private fun addPayment(pay: Payment) {
////        viewDialog.showDialog()
//
//        disposable =
//            paymentServices.addPayment(pay)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    { result ->
////                        viewDialog.hideDialog()
//
//                        Toast.makeText(myContext, "Payment Ajouté", Toast.LENGTH_LONG).show()
//
//                    },
//                    { error ->
////                        viewDialog.hideDialog()
//
//                        Toast.makeText(myContext, "Opération échouée!", Toast.LENGTH_LONG).show()
//                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
//                    }
//                )
//    }
//
//
//    private fun updatePayment(id: Int, amount: Int, date: String, type: String, loc: Rental) {
//        val newPayment = Payment(id, date, amount, type, loc)
//        println(newPayment)
//        viewDialog.showDialog()
//        disposable =
//            paymentServices.updatePayment(newPayment)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    { result ->
//                        viewDialog.hideDialog()
//
//                        Toast.makeText(myContext, "Paiement Modifié", Toast.LENGTH_LONG).show()
//
//                    },
//                    { error ->
//                        viewDialog.hideDialog()
//
//                        Toast.makeText(myContext, "Opération échouée!", Toast.LENGTH_LONG).show()
//                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
//                    }
//                )
//    }
//
//
//    private fun deletePayment(pay: Payment) {
//        viewDialog.showDialog()
//
//        disposable =
//            paymentServices.deletePayment(pay.idPayment)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    { result ->
//                        viewDialog.hideDialog()
//
//                        Toast.makeText(myContext, "Paiement Supprimé", Toast.LENGTH_LONG).show()
//
//                    },
//                    { error ->
//                        viewDialog.hideDialog()
//
//                        Toast.makeText(myContext, "Opération échouée!", Toast.LENGTH_LONG).show()
//                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
//                    }
//                )
//    }
}