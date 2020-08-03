package com.rent.ui.shared.adapter

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
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
import com.rent.ui.shared.view.ViewDialog
import com.rent.tools.PhoneGrantings


/**
 * Created by Sanjeev k Saroj on 28/2/17.
 */

class PaymentListAdapter(
    var list: MutableList<Payment>,
    var myContext: Context,
    var activity: Activity
) : RecyclerView.Adapter<PaymentListAdapter.MainViewHolder>() {


    private lateinit var viewDialog: ViewDialog
    private var removedPosition: Int = 0
    private var removedItem: Payment? = null
    lateinit var myDialog: Dialog

    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private var typePaiement: String = "Avance"


    private val paymentServices by lazy {
        PaymentServices.create()
    }
    private var disposable: Disposable? = null


    class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val amount: TextView = v.findViewById(com.rent.R.id.textmontant)
        val type: TextView = v.findViewById(com.rent.R.id.description)
        val name: TextView = v.findViewById(com.rent.R.id.tv_name)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(com.rent.R.layout.list_rowcell, viewGroup, false)
        return MainViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: MainViewHolder, position: Int) {
        viewHolder.amount.text =
            list[position].amount.toString() + " ( " + list[position].type + " )"
        viewHolder.type.text = list[position].paymentDate
        viewHolder.name.text = list[position].rental.locataire.fullName

        myDialog = Dialog(myContext)
        viewDialog = ViewDialog(activity)

        viewHolder.itemView.setOnClickListener {
            println("item clicked $position")
            ShowPopup(viewHolder.itemView, list[position])
        }
    }

    fun removeItem(position: Int, viewHolder: RecyclerView.ViewHolder) {
        if (PhoneGrantings.isNetworkAvailable(activity)) {
            removedItem = list[position]
            removedPosition = position
            deletePayment(removedItem!!)
            list.removeAt(position)
            notifyItemRemoved(position)

            Snackbar.make(viewHolder.itemView, "Payment Supprimé", Snackbar.LENGTH_LONG)
                .setAction("ANNULER") {
                    addPayment(removedItem!!)
                    list.add(removedPosition, removedItem!!)
                    notifyItemInserted(removedPosition)

                }.show()
        } else
            Toast.makeText(myContext, "Internet Non Disponible", Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount() = list.size


    @SuppressLint("SetTextI18n")
    fun ShowPopup(v: View, payment: Payment) {
        myDialog.setCanceledOnTouchOutside(false)

        myDialog.show()

        myDialog.setContentView(com.rent.R.layout.custompopup2)

        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txt: TextView = myDialog.findViewById(com.rent.R.id.txtclose) as TextView
        val btn: Button = myDialog.findViewById(com.rent.R.id.btnfollow) as Button

        val amount: EditText = myDialog.findViewById(com.rent.R.id.add_amount) as EditText
        val dateBtn: ImageView = myDialog.findViewById(com.rent.R.id.imageDate) as ImageView
        val timeBtn: ImageView = myDialog.findViewById(com.rent.R.id.imageTime) as ImageView
        val dateText: TextView = myDialog.findViewById(com.rent.R.id.add_date) as TextView

        val time = myDialog.findViewById(com.rent.R.id.add_time) as TextView

        amount.setText(payment.amount.toString(), TextView.BufferType.EDITABLE)

        val c = Calendar.getInstance(Locale.FRANCE)
        mHour = c.get(Calendar.HOUR_OF_DAY)
        mMinute = c.get(Calendar.MINUTE)
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)

        time.text = "$mHour:$mMinute"
        dateText.text = mYear.toString() + "-" + (mMonth + 1) + "-" + (mDay)



        timeBtn.setOnClickListener {

            val timePickerDialog = TimePickerDialog(
                myContext,
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
                myContext,
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
        val spinner: Spinner = myDialog.findViewById(com.rent.R.id.types_spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(myContext, com.rent.R.layout.drop_down_list_types, users)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                println("hhhhhhhhh" + parent.getItemAtPosition(pos))
                typePaiement = parent.getItemAtPosition(pos).toString()
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
        txt.setOnClickListener { myDialog.dismiss() }
        btn.setOnClickListener {
            if (PhoneGrantings.isNetworkAvailable(activity)) {
                updatePayment(
                    payment.idPayment,
                    Integer.parseInt(amount.text.toString()),
                    dateText.text.toString() + " " + time.text.toString() + ":00",
                    typePaiement,
                    payment.rental
                )
                myDialog.dismiss()
            } else
                Toast.makeText(myContext, "Internet Non Disponible", Toast.LENGTH_SHORT).show()


        }

    }


    private fun addPayment(pay: Payment) {
//        viewDialog.showDialog()

        disposable =
            paymentServices.addPayment(pay)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
//                        viewDialog.hideDialog()

                        Toast.makeText(myContext, "Payment Ajouté", Toast.LENGTH_LONG).show()

                    },
                    { error ->
//                        viewDialog.hideDialog()

                        Toast.makeText(myContext, "Opération échouée!", Toast.LENGTH_LONG).show()
                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                    }
                )
    }


    private fun updatePayment(id: Int, amount: Int, date: String, type: String, loc: Rental) {
        val newPayment = Payment(id, date, amount, type, loc)
        println(newPayment)
        viewDialog.showDialog()
        disposable =
            paymentServices.updatePayment(newPayment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        viewDialog.hideDialog()

                        Toast.makeText(myContext, "Paiement Modifié", Toast.LENGTH_LONG).show()

                    },
                    { error ->
                        viewDialog.hideDialog()

                        Toast.makeText(myContext, "Opération échouée!", Toast.LENGTH_LONG).show()
                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                    }
                )
    }


    private fun deletePayment(pay: Payment) {
        viewDialog.showDialog()

        disposable =
            paymentServices.deletePayment(pay.idPayment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        viewDialog.hideDialog()

                        Toast.makeText(myContext, "Paiement Supprimé", Toast.LENGTH_LONG).show()

                    },
                    { error ->
                        viewDialog.hideDialog()

                        Toast.makeText(myContext, "Opération échouée!", Toast.LENGTH_LONG).show()
                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                    }
                )
    }
}