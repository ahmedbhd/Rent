package com.rent.global.listener

import com.rent.data.model.flight.Flight


interface CalendarItemClickListener {
    fun onCalendarItemClicked(flight: Flight)
}