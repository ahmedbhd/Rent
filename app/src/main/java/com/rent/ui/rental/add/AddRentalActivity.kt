package com.rent.ui.rental.add

import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.TimePicker
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.OrientationHelper
import com.applikeysolutions.cosmocalendar.utils.SelectionType
import com.applikeysolutions.cosmocalendar.view.CalendarView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.rent.R
import com.rent.base.BaseActivity
import com.rent.databinding.ActivityAddRentalBinding
import com.rent.global.helper.Navigation
import com.rent.global.helper.ViewModelFactory
import com.rent.global.utils.getColorCompat
import com.rent.global.utils.observeOnlyNotNull
import com.rent.ui.shared.dialog.CustomPhoneDialog
import kotlinx.android.synthetic.main.activity_add_rental.*
import kotlinx.android.synthetic.main.bottom_sheet_calendar.*
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class AddRentalActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: AddRentalViewModel by viewModels { viewModelFactory }

    private var calendarView: CalendarView? = null
    lateinit var myDialog: Dialog

    private var mDefaultColor: Int = 0
    private lateinit var newStringColor: String
    private lateinit var binding: ActivityAddRentalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_rental)
        val actionBar = (this as AppCompatActivity).supportActionBar
        actionBar!!.title = "Nouveau Location"
        myDialog = Dialog(this)
        mDefaultColor = this.getColorCompat(R.color.greencircle)
        viewModel.newRental.color = "#9FE554"

        initCalendar()

        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheetCal)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        imageDateAdd.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        }
        cancel_add.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        save_add.setOnClickListener {
            println(calendarView!!.selectedDays[0])
            println(calendarView!!.selectedDays[calendarView!!.selectedDays.size - 1])

            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = format.format(calendarView!!.selectedDays[0].calendar.time)
            val end =
                format.format(calendarView!!.selectedDays[calendarView!!.selectedDays.size - 1].calendar.time)

            add_date_start.text = start
            add_date_end.text = end

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        }

        imageTimeAdd.setOnClickListener {

            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int
                    ->
                    add_time_add.text = "$hourOfDay:$minute"


                },
                0, 0, true
            )
            timePickerDialog.show()
        }


        mark.setOnClickListener {
            openColorPicker()
        }

        registerBindingAndBaseObservers()
    }

    private fun registerBindingAndBaseObservers() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        registerBaseObservers(viewModel)
        registerPhoneDialog()
    }


    private fun openColorPicker() {
        val onAmbilWarnaListener = object : OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog) {

            }

            override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                mDefaultColor = color
                val background = mark.background
                (background as GradientDrawable).setColor(mDefaultColor)
                newStringColor = String.format("#%06X", 0xFFFFFF and mDefaultColor)
                viewModel.newRental.color = newStringColor
                initCalendar()
            }
        }
        val colorPicker = AmbilWarnaDialog(this, mDefaultColor, onAmbilWarnaListener)
        colorPicker.show()
    }

    private fun initCalendar() {
        calendarView = findViewById<CalendarView>(R.id.calendar_view)
        calendarView!!.calendarOrientation = OrientationHelper.HORIZONTAL
        calendarView!!.selectionType = SelectionType.RANGE
        calendarView!!.selectedDayBackgroundColor = mDefaultColor
    }

    private fun registerPhoneDialog() {
        viewModel.phoneDialog.observeOnlyNotNull(
            this
        ) { phoneDialog ->
            showPhoneDialog(
                phoneDialog.dismissActionBlock
            )
        }
    }

    private fun showPhoneDialog(dismissActionBlock: (() -> Unit)?) {
        if (!isFinishing) {
            CustomPhoneDialog(
                this,
                "",
                viewModel,
                dismissActionBlock
            ).show()
        }
    }

    override fun navigate(navigationTo: Navigation) {
        super.navigate(navigationTo)
        when (navigationTo) {
            is Navigation.Back -> finish()
        }
    }
}
