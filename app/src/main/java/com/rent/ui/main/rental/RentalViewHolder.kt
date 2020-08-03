package com.rent.ui.main.rental

import android.app.Dialog
import android.content.Intent
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.rent.data.model.rental.Rental
import com.rent.databinding.ItemCalendarBinding
import com.rent.databinding.RentalListItemBinding
import com.rent.global.listener.CalendarItemClickListener
import com.rent.global.listener.RentalItemClickListener
import com.rent.ui.main.calendar.CalendarViewHolder
import com.rent.ui.rental.detail.RentalDetailActivity
import com.rent.ui.shared.view.ViewDialog
import java.text.SimpleDateFormat
import java.util.*

class RentalViewHolder(
    private val binding: RentalListItemBinding,
    private val rentalItemClickListener: RentalItemClickListener?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(rental: Rental) {
//        val cin = v.findViewById(com.rent.R.id.loc_cin) as TextView
//        val dateStart = v.findViewById(com.rent.R.id.loc_start) as TextView
//        val dateEnd = v.findViewById(com.rent.R.id.loc_end) as TextView
//        val timeEnd = v.findViewById(com.rent.R.id.etime) as TextView
//        val timeStart = v.findViewById(com.rent.R.id.stime) as TextView

        // Populate the data into the template view using the data object
        binding.locCin.text = rental.locataire.fullName
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        var date = format.parse(rental.dateDebut)
        var mDay = DateFormat.format("dd", date)
        var mMonth = DateFormat.format("MM", date)
        var mYear = DateFormat.format("yyyy", date)
        var mHour = DateFormat.format("hh", date)
        var mMinute = DateFormat.format("mm", date)

        binding.locStart.text = "$mYear-$mMonth-$mDay"
        binding.stime.text = "$mHour:$mMinute"

        date = format.parse(rental.dateFin)
        mDay = DateFormat.format("dd", date)
        mMonth = DateFormat.format("MM", date)
        mYear = DateFormat.format("yyyy", date)
        mHour = DateFormat.format("hh", date)
        mMinute = DateFormat.format("mm", date)

        binding.locEnd.text = "$mYear-$mMonth-$mDay"
        binding.etime.text = "$mHour:$mMinute"


//        viewDialog = ViewDialog(activity)
//        myDialog = Dialog(myContext)

        binding.root.setOnClickListener {
            rentalItemClickListener?.onRentalItemClicked(rental)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            rentalItemClickListener: RentalItemClickListener?
        ): RentalViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = RentalListItemBinding.inflate(inflater, parent, false)
            return RentalViewHolder(
                binding,
                rentalItemClickListener
            )
        }
    }
}