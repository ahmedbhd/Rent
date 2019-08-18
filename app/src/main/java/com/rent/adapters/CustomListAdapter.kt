package com.rent.adapters

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
import com.rent.adapters.util.LocaleHelper
import com.rent.data.Model
import java.util.*
import android.widget.ArrayAdapter
import com.rent.R


/**
 * Created by Sanjeev k Saroj on 28/2/17.
 */

class CustomListAdapter(var list: MutableList<Model.payment>, var myContext: Context)
    : RecyclerView.Adapter<CustomListAdapter.MainViewHolder>(){


    private var removedPosition: Int = 0
    private var removedItem: Model.payment? = null
    lateinit var myDialog: Dialog

     private var mYear: Int = 0
     private var mMonth: Int = 0
     private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private var typePaiement:String = "Avance"

    class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val amount: TextView = v.findViewById(com.rent.R.id.tv_name)
        val type: TextView = v.findViewById(com.rent.R.id.description)


    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(com.rent.R.layout.list_rowcell, viewGroup, false)
        return MainViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: MainViewHolder, position: Int) {
        viewHolder.amount.text = list[position].amount
        viewHolder.type.text = list[position].type
        myDialog =  Dialog(myContext)
        viewHolder.itemView.setOnClickListener {
            println("item clicked")
            ShowPopup(viewHolder.itemView,list[position])
        }
    }

    fun removeItem(position: Int, viewHolder: RecyclerView.ViewHolder) {
        removedItem = list[position]
        removedPosition = position

        list.removeAt(position)
        notifyItemRemoved(position)

        Snackbar.make(viewHolder.itemView, "Payment Supprimé", Snackbar.LENGTH_LONG).setAction("UNDO") {
            list.add(removedPosition, removedItem!!)
            notifyItemInserted(removedPosition)

        }.show()
    }

    override fun getItemCount() = list.size


    @SuppressLint("SetTextI18n")
    fun ShowPopup(v :View, payment: Model.payment) {
        LocaleHelper.setLocale(myContext, "fr")
        myDialog.setCanceledOnTouchOutside(false)

        myDialog.show()

        myDialog.setContentView(com.rent.R.layout.custompopup2  )

        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txt: TextView = myDialog.findViewById(R.id.txtclose) as TextView
        val btn: Button = myDialog.findViewById(R.id.btnfollow) as Button

        val amount: EditText = myDialog.findViewById(R.id.add_amount) as EditText
        val dateBtn: ImageView = myDialog.findViewById(R.id.imageDate) as ImageView
        val timeBtn: ImageView = myDialog.findViewById(R.id.imageTime) as ImageView
        val dateText: TextView = myDialog.findViewById(R.id.add_date) as TextView

        val time = myDialog.findViewById(R.id.add_time) as TextView

        amount.setText(payment.amount, TextView.BufferType.EDITABLE)

        val c = Calendar.getInstance(Locale.FRANCE)
        mHour = c.get(Calendar.HOUR_OF_DAY)
        mMinute = c.get(Calendar.MINUTE)
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)

        time.text = "$mHour:$mMinute"
        dateText.text = mYear.toString()+ "-"+mMonth+ 1+ "-" + (mDay )



        timeBtn.setOnClickListener {

            val timePickerDialog = TimePickerDialog(
                myContext,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay:Int, minute: Int
                    -> time.text = "$hourOfDay:$minute"
                },
                mHour, mMinute, true
            )
            timePickerDialog.show()
        }

        dateBtn.setOnClickListener {

            val datePickerDialog = DatePickerDialog(
                myContext,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth
                    -> dateText.text =year.toString()+"-"+ dayOfMonth+ "-" + (monthOfYear + 1)  },
                mYear, // Initial year selection
                mMonth, // Initial month selection
                mDay // Inital day selection
            )
            datePickerDialog.show()
        }

        val users = arrayOf("Avance", "Reste")
        val spinner: Spinner = myDialog.findViewById(com.rent.R.id.types_spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        val adapter = ArrayAdapter(myContext,R.layout.drop_down_list_types , users)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                println("hhhhhhhhh"+parent.getItemAtPosition(pos))
                typePaiement = parent.getItemAtPosition(pos).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        val spinnerAdap = spinner.adapter as ArrayAdapter<String>

        if (payment.type == "payment") {
            val spinnerPosition = spinnerAdap.getPosition("Reste")

            spinner.setSelection(spinnerPosition)
        }
        txt.setOnClickListener { myDialog.dismiss() }
        btn.setOnClickListener {
            println(amount.text.toString())

//            addPayment(Integer.parseInt(amount.text.toString()),date,type.text.toString())
            myDialog.dismiss()
        }

    }

}