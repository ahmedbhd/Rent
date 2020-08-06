package com.rent.global.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.format.DateFormat
import com.rent.data.model.flight.Airport
import com.rent.data.model.flight.Flight
import com.rent.data.model.relations.RentalWithLocataire
import java.time.YearMonth
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList


@SuppressLint("Range")
fun generateFlights(locations: ArrayList<RentalWithLocataire>): List<Flight> {
    val list = mutableListOf<Flight>()

    locations.forEach(Consumer { loc ->
        val localDateStart = loc.rental.dateDebut
        val localDateEnd = loc.rental.dateFin

        val datesInRange = ArrayList<Date>()
        val startCalendar = GregorianCalendar()
        startCalendar.time = localDateStart

        val endCalendar = GregorianCalendar()
        endCalendar.time = localDateEnd
        endCalendar.add(Calendar.DATE, 1)

        while (startCalendar.before(endCalendar)) {
            val result = startCalendar.time
            datesInRange.add(result)
            startCalendar.add(Calendar.DATE, 1)
        }

        datesInRange.forEach(Consumer { d ->

            val c = Calendar.getInstance()
            c.time = d

            val mDay = DateFormat.format("dd", d)
            val mMonth = DateFormat.format("MM", d)
            val mYear = DateFormat.format("yyyy", d)
            val mHour = c.get(Calendar.HOUR_OF_DAY)
            val mMinute = c.get(Calendar.MINUTE)


            val currentMonth = YearMonth.of(mYear.toString().toInt(), mMonth.toString().toInt())

            val currentMonth16 = currentMonth.atDay(mDay.toString().toInt())
            list.add(
                Flight(
                    loc.rental.idRental,
                    currentMonth16.atTime(mHour.toString().toInt(), mMinute.toString().toInt()),
                    Airport(
                        loc.locataire.numTel,
                        loc.locataire.fullName
                    ),
                    Airport("Abuja", "ABV"),
                    Color.parseColor(loc.rental.color)
                )
            )
        })

    })
    return list
}
