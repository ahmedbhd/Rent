package com.rent.ui.main.calendar

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rent.data.model.flight.Flight
import com.rent.global.listener.CalendarItemClickListener


class CalendarAdapter : RecyclerView.Adapter<CalendarViewHolder>() {

    private var calendarItemClickListener: CalendarItemClickListener? = null
    private var items = ArrayList<Flight>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder.create(parent, calendarItemClickListener)
    }

    override fun onBindViewHolder(viewHolder: CalendarViewHolder, position: Int) {
        viewHolder.bind(items[position])
    }

    fun setListener(listener: CalendarItemClickListener) {
        calendarItemClickListener = listener
    }

    fun setData(data: ArrayList<Flight>) {
        items = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

}