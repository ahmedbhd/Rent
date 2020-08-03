package com.rent.ui.main.rental

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rent.data.model.rental.Rental
import com.rent.global.listener.RentalItemClickListener
import com.rent.global.listener.RentalItemSwipeListener
import com.rent.ui.shared.view.ViewDialog
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Sanjeev k Saroj on 28/2/17.
 */

class RentalListAdapter : RecyclerView.Adapter<RentalViewHolder>() {

    private var rentals: MutableList<Rental> = ArrayList()

    var locationFilteredList: MutableList<Rental>? = null
    private var rentalItemClickListener: RentalItemClickListener? = null
    private var rentalItemSwipeListener: RentalItemSwipeListener? = null


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RentalViewHolder {
        return RentalViewHolder.create(viewGroup, rentalItemClickListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: RentalViewHolder, position: Int) {
        viewHolder.bind(rentals[position])
    }


    override fun getItemCount() = rentals.size

    fun setData(list :ArrayList<Rental>) {
        rentals = list
        if (locationFilteredList == null) locationFilteredList = list
        notifyDataSetChanged()
    }

    fun setListenerClick(listener: RentalItemClickListener) {
        rentalItemClickListener = listener
    }

    fun setListenerSwipe(listener: RentalItemSwipeListener) {
        rentalItemSwipeListener = listener
    }

    fun showPopupTel(position: Int) {
        val stringTel = rentals[position].locataire.numTel

        rentalItemSwipeListener?.onRentalItemSwiped(stringTel)
//
//        myDialog.show()
//
//        myDialog.setContentView(com.rent.R.layout.dialog_custom_call)
//
//        val addTel1: TextView = myDialog.findViewById(com.rent.R.id.callnumberCall) as TextView
//
//        val myLayout: LinearLayout = myDialog.findViewById(com.rent.R.id.listcalls) as LinearLayout
//
//
//
//
//        if (stringTel != "") {
//            val tab = stringTel.split(",")
//            addTel1.text = tab[0]
//            addTel1.setOnClickListener {
//                phoneCall(tab[0])
//            }
//            for (i in 1 until tab.size) {
//                val myTextView = TextView(activity) // Pass it an Activity or Context
//                myTextView.text = tab[i]
//
//                myTextView.setCompoundDrawablesWithIntrinsicBounds(
//                    com.rent.R.drawable.phonesettings,
//                    0,
//                    0,
//                    0
//                )
//                myTextView.compoundDrawablePadding = 5
//                myTextView.gravity = Gravity.CENTER
//                myTextView.setPadding(5, 5, 5, 5)
//                myTextView.textSize = 20f
//                val parms = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                ) // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                parms.gravity = Gravity.CENTER
//                parms.setMargins(5, 5, 5, 5)
//                myTextView.layoutParams = parms
//                myLayout.addView(myTextView)
//                myTextView.setOnClickListener {
//                    phoneCall(tab[i])
//                }
//            }
//
//        } else {
//            addTel1.visibility = View.GONE
//        }


    }




}