package com.rent.ui.main.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rent.data.model.flight.Flight
import com.rent.databinding.ItemCalendarBinding
import com.rent.global.listener.CalendarItemClickListener
import com.rent.global.utils.setClickWithDebounce
import java.time.format.DateTimeFormatter
import java.util.*


class CalendarViewHolder(
    private val binding: ItemCalendarBinding,
    private val onItemClickListener: CalendarItemClickListener?
) : RecyclerView.ViewHolder(binding.root) {

    private val formatter = DateTimeFormatter.ofPattern("EEE'\n'dd MMM'\n'HH:mm", Locale.FRANCE)

    fun bind(flight: Flight, position: Int) {
        binding.itemFlightDateText.setBackgroundColor(flight.color)
        binding.time = formatter.format(flight.time)

        binding.code = flight.departure.code
        binding.city = flight.departure.city

        binding.root.setClickWithDebounce {
            onItemClickListener?.onCalendarItemClicked(position)
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClickListener: CalendarItemClickListener?
        ): CalendarViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemCalendarBinding.inflate(inflater, parent, false)
            return CalendarViewHolder(
                binding,
                onItemClickListener
            )
        }
    }
}
