package com.rent

import android.app.TimePickerDialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.OrientationHelper
import com.applikeysolutions.cosmocalendar.model.Day
import com.applikeysolutions.cosmocalendar.selection.RangeSelectionManager
import com.applikeysolutions.cosmocalendar.utils.SelectionType
import com.applikeysolutions.cosmocalendar.view.CalendarView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.rent.tools.getColorCompat
import kotlinx.android.synthetic.main.activity_add_loc.*
import kotlinx.android.synthetic.main.add_cal_bottomsheet.*
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
import java.util.*
import android.view.MotionEvent
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.NonNull
import androidx.core.view.MotionEventCompat
import com.rent.adapters.util.TimePickerFragment.Companion.time
import java.text.SimpleDateFormat


class AddLocActivity : AppCompatActivity()  {

    private var mDefaultColor :Int = 0
    private lateinit var newStringColor:String
    private var calendarView: CalendarView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_loc)
        val actionBar = (this as AppCompatActivity).supportActionBar
        actionBar!!.title = "Nouveau Location"
        initViews()

        val bottomSheetBehavior : BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheetCal )
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        imageDateAdd.setOnClickListener{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        }
        cancel_add.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        save_add.setOnClickListener {
            println(calendarView!!.selectedDays[0])
            println(calendarView!!.selectedDays[calendarView!!.selectedDays.size-1])

            val format =  SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = format.format(calendarView!!.selectedDays[0].calendar.time)
            val end = format.format( calendarView!!.selectedDays[calendarView!!.selectedDays.size-1].calendar.time)

            add_date_start.text = start
            add_date_end.text = end
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }

        imageTimeAdd.setOnClickListener {

            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay:Int, minute: Int
                    -> add_time_add.text = "$hourOfDay:$minute"
                },
                0, 0, true
            )
            timePickerDialog.show()
        }
        mDefaultColor = this.getColorCompat(R.color.orange_800)
//        mark.setBackgroundColor(mDefaultColor)

        mark.setOnClickListener {
            openColorPicker(this)
        }

    }
    private fun openColorPicker(mContext:Context){
        val onAmbilWarnaListener = object: OnAmbilWarnaListener {
            override fun  onCancel (dialog : AmbilWarnaDialog){

            }
            override fun onOk(dialog: AmbilWarnaDialog, color:Int){
                mDefaultColor = color
                val background = mark.background
                (background as GradientDrawable).setColor(mDefaultColor)
                newStringColor = String.format("#%06X", 0xFFFFFF and mDefaultColor)
                Toast.makeText(mContext,newStringColor,Toast.LENGTH_LONG).show()
            }
        }
        val colorPicker = AmbilWarnaDialog(this, mDefaultColor,onAmbilWarnaListener)
        colorPicker.show()
    }

    private fun initViews() {
        calendarView = findViewById<CalendarView>(R.id.calendar_view)
        calendarView!!.calendarOrientation = OrientationHelper.HORIZONTAL
        calendarView!!.selectionType = SelectionType.RANGE
        if (calendarView!!.selectionManager is RangeSelectionManager) {
            val rangeSelectionManager = calendarView!!.selectionManager as RangeSelectionManager
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 3)
            rangeSelectionManager.toggleDay( Day(Calendar.getInstance()))
            rangeSelectionManager.toggleDay( Day(calendar))
            calendarView!!.update()
        }
    }





}
