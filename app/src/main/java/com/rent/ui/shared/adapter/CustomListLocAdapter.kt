package com.rent.ui.shared.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.rent.ui.rental.detail.RentalDetailActivity
import com.rent.ui.shared.adapter.util.ViewDialog
import com.rent.data.model.rental.Rental
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Sanjeev k Saroj on 28/2/17.
 */

class CustomListLocAdapter(
    var list: MutableList<Rental>,
    var myContext: Context,
    var activity: Activity
) : RecyclerView.Adapter<CustomListLocAdapter.MainViewHolder>(), Filterable {


    private lateinit var viewDialog: ViewDialog
    var locationFilteredList: MutableList<Rental> = ArrayList()
    private lateinit var myDialog: Dialog


    class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val cin = v.findViewById(com.rent.R.id.loc_cin) as TextView
        val dateStart = v.findViewById(com.rent.R.id.loc_start) as TextView
        val dateEnd = v.findViewById(com.rent.R.id.loc_end) as TextView
        val timeEnd = v.findViewById(com.rent.R.id.etime) as TextView
        val timeStart = v.findViewById(com.rent.R.id.stime) as TextView

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(com.rent.R.layout.listview_item, viewGroup, false)
        return MainViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: MainViewHolder, position: Int) {
        val p = list[position]

        // Populate the data into the template view using the data object
        viewHolder.cin.text = p.locataire.fullName
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        var date = format.parse(p.dateDebut)
        var mDay = DateFormat.format("dd", date)
        var mMonth = DateFormat.format("MM", date)
        var mYear = DateFormat.format("yyyy", date)
        var mHour = DateFormat.format("hh", date)
        var mMinute = DateFormat.format("mm", date)

        viewHolder.dateStart.text = "$mYear-$mMonth-$mDay"
        viewHolder.timeStart.text = "$mHour:$mMinute"

        date = format.parse(p.dateFin)
        mDay = DateFormat.format("dd", date)
        mMonth = DateFormat.format("MM", date)
        mYear = DateFormat.format("yyyy", date)
        mHour = DateFormat.format("hh", date)
        mMinute = DateFormat.format("mm", date)

        viewHolder.dateEnd.text = "$mYear-$mMonth-$mDay"
        viewHolder.timeEnd.text = "$mHour:$mMinute"


        viewDialog = ViewDialog(activity)
        myDialog = Dialog(myContext)

        viewHolder.itemView.setOnClickListener {
            println("item clicked $position")

            val intent = Intent(myContext, RentalDetailActivity().javaClass)
            val res: Rental? = list[position]
            println("res ${res.toString()}")
            intent.putExtra("myObject2", Gson().toJson(res))
            activity.startActivity(intent)
        }
    }


    override fun getItemCount() = list.size


    fun ShowPopupTel(position: Int, viewHolder: RecyclerView.ViewHolder) {
        val stringTel = list[position].locataire.numTel


        myDialog.show()

        myDialog.setContentView(com.rent.R.layout.custompopupcall)

        val addTel1: TextView = myDialog.findViewById(com.rent.R.id.callnumberCall) as TextView

        val myLayout: LinearLayout = myDialog.findViewById(com.rent.R.id.listcalls) as LinearLayout




        if (stringTel != "") {
            val tab = stringTel.split(",")
            addTel1.text = tab[0]
            addTel1.setOnClickListener {
                phoneCall(tab[0])
            }
            for (i in 1 until tab.size) {
                val myTextView = TextView(myContext) // Pass it an Activity or Context
                myTextView.text = tab[i]

                myTextView.setCompoundDrawablesWithIntrinsicBounds(
                    com.rent.R.drawable.phonesettings,
                    0,
                    0,
                    0
                )
                myTextView.compoundDrawablePadding = 5
                myTextView.gravity = Gravity.CENTER
                myTextView.setPadding(5, 5, 5, 5)
                myTextView.textSize = 20f
                val parms = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ) // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                parms.gravity = Gravity.CENTER
                parms.setMargins(5, 5, 5, 5)
                myTextView.layoutParams = parms
                myLayout.addView(myTextView)
                myTextView.setOnClickListener {
                    phoneCall(tab[i])
                }
            }

        } else {
            addTel1.visibility = View.GONE
        }


    }

    private fun phoneCall(tel: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel"))
        activity.startActivity(intent)
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    locationFilteredList = list
                } else {
                    val filteredList: MutableList<Rental> = ArrayList()
                    for (row in list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.locataire.fullName.toLowerCase(Locale.getDefault()).contains(
                                charString.toLowerCase(
                                    Locale.getDefault()
                                )
                            ) || row.locataire.numTel.contains(
                                charSequence
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }

                    locationFilteredList = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = locationFilteredList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                locationFilteredList = filterResults.values as MutableList<Rental>

                // refresh the list with filtered data
                notifyDataSetChanged()
            }
        }
    }
}