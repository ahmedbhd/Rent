package com.rent.ui.shared.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.OrientationHelper
import com.applikeysolutions.cosmocalendar.model.Day
import com.applikeysolutions.cosmocalendar.selection.RangeSelectionManager
import com.applikeysolutions.cosmocalendar.utils.SelectionType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rent.R
import com.rent.databinding.BottomSheetCalendarBinding
import com.rent.global.listener.CalendarBottomSheetListener
import com.rent.global.utils.setClickWithDebounce
import kotlinx.android.synthetic.main.bottom_sheet_calendar.*
import java.text.SimpleDateFormat
import java.util.*


class BottomSheetCalendar(
    context: Context,
    private val rentalColor: Int? = null,
    private val dateRange: Pair<Date, Date>,
    private val calendarBottomSheetListener: CalendarBottomSheetListener,
    private val dismissActionBlock: (() -> Unit)? = null
) : BottomSheetDialog(context) {
    private val binding: BottomSheetCalendarBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.bottom_sheet_calendar,
            null,
            true
        )
    private val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.calendarView.calendarOrientation = OrientationHelper.HORIZONTAL
        binding.calendarView.selectionType = SelectionType.RANGE
        binding.calendarView.selectedDayBackgroundColor =
            rentalColor ?: R.color.colorPrimary

        if (binding.calendarView.selectionManager is RangeSelectionManager) {
            val rangeSelectionManager =
                binding.calendarView.selectionManager as RangeSelectionManager


            val sCalendar = Calendar.getInstance()
            sCalendar.time = dateRange.first
            val eCalendar = Calendar.getInstance()
            eCalendar.time = dateRange.second

            rangeSelectionManager.toggleDay(Day(sCalendar))
            rangeSelectionManager.toggleDay(Day(eCalendar))
            binding.calendarView.update()
        }
        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheetCal)

        binding.saveAdd.setClickWithDebounce {
            val start = format.format(binding.calendarView.selectedDays[0].calendar.time)
            val end =
                format.format(binding.calendarView.selectedDays[binding.calendarView.selectedDays.size - 1].calendar.time)
            calendarBottomSheetListener.onSaveClicked(Pair(start, end))

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
//            bottomSheetListBehavior.isHideable = false
//            bottomSheetListBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.cancelAdd.setClickWithDebounce {
            dismiss()
        }

        setOnDismissListener {
            dismissActionBlock?.invoke()
            dismiss()
        }
        setCancelable(false)
        binding.executePendingBindings()
        setContentView(binding.root)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}