package com.rent.global.helper.dialog

import com.rent.global.listener.CalendarBottomSheetListener
import java.util.*

class CalendarBottomSheet private constructor(
    val rentalColor: Int? = null,
    val dateRange: Pair<Date, Date>,
    val calendarBottomSheetListener: CalendarBottomSheetListener,
    val dismissActionBlock: (() -> Unit)? = null
) {

    companion object {
        fun build(
            rentalColor: Int? = null,
            dateRange: Pair<Date, Date>,
            calendarBottomSheetListener: CalendarBottomSheetListener,
            dismissActionBlock: (() -> Unit)? = null
        ): CalendarBottomSheet {
            return CalendarBottomSheet(
                rentalColor,
                dateRange,
                calendarBottomSheetListener,
                dismissActionBlock
            )
        }
    }
}