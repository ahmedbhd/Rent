package com.rent.ui.main.rental

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rent.data.model.relations.RentalWithLocataire
import com.rent.global.listener.RentalItemClickListener
import com.rent.global.listener.RentalItemSwipeListener


class RentalListAdapter : RecyclerView.Adapter<RentalViewHolder>() {

    private var rentals: MutableList<RentalWithLocataire> = ArrayList()

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

    fun setData(list: ArrayList<RentalWithLocataire>) {
        rentals = list
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

    }


}