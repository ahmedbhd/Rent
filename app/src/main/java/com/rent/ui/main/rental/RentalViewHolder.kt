package com.rent.ui.main.rental

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rent.data.model.rental.Rental
import com.rent.databinding.RentalListItemBinding
import com.rent.global.listener.RentalItemClickListener
import java.text.SimpleDateFormat
import java.util.*

class RentalViewHolder(
    private val binding: RentalListItemBinding,
    private val rentalItemClickListener: RentalItemClickListener?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(rental: Rental) {
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