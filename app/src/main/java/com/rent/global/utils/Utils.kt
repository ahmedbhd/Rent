package com.rent.global.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.format.DateFormat
import com.rent.ui.main.home.Flight
import com.rent.data.model.rental.Rental
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList


private typealias Airport = Flight.Airport

@SuppressLint("Range")
fun generateFlights(locations: ArrayList<Rental>): List<Flight> {
    val list = mutableListOf<Flight>()

//    val currentMonth = YearMonth.now()
//
//    val currentMonth16 = currentMonth.atDay(16)
//    list.add(Flight(currentMonth16.atTime(14, 0), Airport("Lagos", "LOS"), Airport("Abuja", "ABV"), com.rent.R.color.brown_700))
//
//    val currentMonth17 = currentMonth.atDay(17)
//    list.add(Flight(currentMonth17.atTime(15, 0), Airport("Tunis", "TN"), Airport("Paris", "FR"), com.rent.R.color.default_connected_day_selected_text_color))
//    list.add(Flight(currentMonth17.atTime(14, 0), Airport("Lagos", "LOS"), Airport("Abuja", "ABV"), com.rent.R.color.brown_700))
//    list.add(Flight(currentMonth17.atTime(21, 30), Airport("Enugu", "ENU"), Airport("Owerri", "QOW"), com.rent.R.color.blue_grey_700))
//
//    val currentMonth22 = currentMonth.atDay(22)
//    list.add(Flight(currentMonth22.atTime(13, 20), Airport("Ibadan", "IBA"), Airport("Benin", "BNI"), com.rent.R.color.blue_800))
//    list.add(Flight(currentMonth22.atTime(17, 40), Airport("Sokoto", "SKO"), Airport("Ilorin", "ILR"), com.rent.R.color.red_800))
//
//    list.add(
//        Flight(
//            currentMonth.atDay(3).atTime(20, 0),
//            Airport("Makurdi", "MDI"),
//            Airport("Calabar", "CBQ"),
//            com.rent.R.color.teal_700
//        )
//    )
//
//    list.add(
//        Flight(
//            currentMonth.atDay(12).atTime(18, 15),
//            Airport("Kaduna", "KAD"),
//            Airport("Jos", "JOS"),
//            com.rent.R.color.cyan_700
//        )
//    )
//
//    val nextMonth13 = currentMonth.plusMonths(1).atDay(13)
//    list.add(Flight(nextMonth13.atTime(7, 30), Airport("Kano", "KAN"), Airport("Akure", "AKR"), com.rent.R.color.pink_700))
//    list.add(Flight(nextMonth13.atTime(10, 50), Airport("Minna", "MXJ"), Airport("Zaria", "ZAR"), com.rent.R.color.green_700))
//
//    list.add(
//        Flight(
//            currentMonth.minusMonths(1).atDay(9).atTime(20, 15),
//            Airport("Asaba", "ABB"),
//            Airport("Port Harcourt", "PHC"),
//            com.rent.R.color.orange_800
//        )
//    )

    locations.forEach(Consumer { loc ->
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val localDateStart = format.parse(loc.dateDebut)
        val localDateEnd = format.parse(loc.dateFin)

        val datesInRange = ArrayList<Date>()
        val calendar = GregorianCalendar()
        calendar.time = localDateStart

        val endCalendar = GregorianCalendar()
        endCalendar.time = localDateEnd
        endCalendar.add(Calendar.DATE, 1)

        while (calendar.before(endCalendar)) {
            val result = calendar.time
            datesInRange.add(result)
            calendar.add(Calendar.DATE, 1)
        }

        datesInRange.forEach(Consumer { d ->
            val mDay = DateFormat.format("dd", d)
            val mMonth = DateFormat.format("MM", d)
            val mYear = DateFormat.format("yyyy", d)
            val mHour = DateFormat.format("hh", d)
            val mMinute = DateFormat.format("mm", d)


            val currentMonth = YearMonth.of(mYear.toString().toInt(), mMonth.toString().toInt())

            val currentMonth16 = currentMonth.atDay(mDay.toString().toInt())
            list.add(
                Flight(
                    currentMonth16.atTime(mHour.toString().toInt(), mMinute.toString().toInt()),
                    Airport(
                        loc.locataire.numTel,
                        loc.locataire.fullName
                    ),
                    Airport("Abuja", "ABV"),
                    Color.parseColor(loc.color)
                )
            )
        })

    })
    return list
}
