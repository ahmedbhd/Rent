package com.rent.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rent.R
import com.rent.data.LocationServices
import com.rent.data.Model
import kotlinx.android.synthetic.main.list_rowcell.view.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Sanjeev k Saroj on 28/2/17.
 */

class CustomListAdapter(var list: MutableList<Model.payment>)  : RecyclerView.Adapter<CustomListAdapter.MainViewHolder>() {
    private var removedPosition: Int = 0
    private var removedItem: Model.payment? = null
    class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val amount: TextView = v.findViewById(R.id.tv_name)
        val type: TextView = v.findViewById(R.id.description)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_rowcell, viewGroup, false)
        return MainViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: MainViewHolder, position: Int) {
        viewHolder.amount.text = list[position].amount
        viewHolder.type.text = list[position].type
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


}