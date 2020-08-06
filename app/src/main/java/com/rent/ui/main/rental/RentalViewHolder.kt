package com.rent.ui.main.rental

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rent.data.model.relations.RentalWithLocataire
import com.rent.databinding.RentalListItemBinding
import com.rent.global.listener.RentalItemClickListener
import java.util.*


class RentalViewHolder(
    private val binding: RentalListItemBinding,
    private val rentalItemClickListener: RentalItemClickListener?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(rentalAndLocataire: RentalWithLocataire) {
        // Populate the data into the template view using the data object
        binding.locCin.text = rentalAndLocataire.locataire.fullName
        val c = Calendar.getInstance()
        c.time = rentalAndLocataire.rental.dateDebut
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMinute = c.get(Calendar.MINUTE)
        var mYear = c.get(Calendar.YEAR)
        var mMonth = c.get(Calendar.MONTH)
        var mDay = c.get(Calendar.DAY_OF_MONTH)

        binding.locStart.text = mYear.toString() + "-" + (mMonth + 1) + "-" + (mDay)
        binding.stime.text = "$mHour:$mMinute"

        c.time = rentalAndLocataire.rental.dateFin
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)

        binding.locEnd.text = mYear.toString() + "-" + (mMonth + 1) + "-" + (mDay)
        binding.etime.text = "$mHour:$mMinute"

        binding.root.setOnClickListener {
            rentalItemClickListener?.onRentalItemClicked(rentalAndLocataire)
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