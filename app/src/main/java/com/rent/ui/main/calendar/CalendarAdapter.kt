package com.rent.ui.main.calendar

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rent.data.model.flight.Flight
import com.rent.global.listener.CalendarItemClickListener


class CalendarAdapter : RecyclerView.Adapter<CalendarViewHolder>() {

    private var calendarItemClickListener: CalendarItemClickListener? = null
    var flights = mutableListOf<Flight>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder.create(parent, calendarItemClickListener)
    }

    override fun onBindViewHolder(viewHolder: CalendarViewHolder, position: Int) {
        viewHolder.bind(flights[position], position)
    }

    fun setListener(listener: CalendarItemClickListener) {
        calendarItemClickListener = listener
    }

    fun setData(data: ArrayList<Flight>?) {
        data?.let {
            flights = data
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = flights.size

}