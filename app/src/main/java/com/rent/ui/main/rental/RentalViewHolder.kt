package com.rent.ui.main.rental

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rent.R
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
        binding.tvRentalItemName.text = rentalAndLocataire.locataire.fullName
        val calendar = Calendar.getInstance()
        calendar.time = rentalAndLocataire.rental.dateDebut
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.tvRentalItemDateStart.text = binding.root.context.getString(
            R.string.global_date_string_format,
            year,
            month + 1,
            day
        )
        binding.tvRentalItemTimeStart.text = binding.root.context.getString(
            R.string.global_time_string_format,
            hour,
            minute
        )

        calendar.time = rentalAndLocataire.rental.dateFin
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.tvRentalItemDateEnd.text = binding.root.context.getString(
            R.string.global_date_string_format,
            year,
            month + 1,
            day
        )
        binding.tvRentalItemTimeEnd.text = binding.root.context.getString(
            R.string.global_time_string_format,
            hour,
            minute
        )

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