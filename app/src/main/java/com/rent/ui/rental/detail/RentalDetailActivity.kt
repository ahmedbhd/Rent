package com.rent.ui.rental.detail

import android.app.TimePickerDialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TimePicker
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.*
import com.applikeysolutions.cosmocalendar.model.Day
import com.applikeysolutions.cosmocalendar.selection.RangeSelectionManager
import com.applikeysolutions.cosmocalendar.utils.SelectionType
import com.applikeysolutions.cosmocalendar.view.CalendarView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.rent.R
import com.rent.base.BaseActivity
import com.rent.data.model.relations.LocataireWithPayment
import com.rent.databinding.ActivityRentalDetailBinding
import com.rent.global.helper.Navigation
import com.rent.global.helper.ViewModelFactory
import com.rent.global.utils.formatDate
import com.rent.global.utils.observeOnlyNotNull
import com.rent.ui.main.calendar.REQUEST_CODE
import com.rent.ui.main.payment.PaymentListAdapter
import com.rent.ui.shared.dialog.CustomPaymentDialog
import com.rent.ui.shared.dialog.CustomPhoneDialog
import kotlinx.android.synthetic.main.bottom_sheet_calendar.*
import kotlinx.android.synthetic.main.bottom_sheet_payments.*
import yuku.ambilwarna.AmbilWarnaDialog
import java.util.*
import javax.inject.Inject


class RentalDetailActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: RentalDetailViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var customListAdapter: PaymentListAdapter

    private lateinit var binding: ActivityRentalDetailBinding

    private lateinit var paymentRecyclerView: RecyclerView

    private var calendarView: CalendarView? = null

    private lateinit var paymentBottomSheetCallback: BottomSheetBehavior.BottomSheetCallback
    private lateinit var bottomSheetBehaviorPayments: BottomSheetBehavior<*>
    private lateinit var bottomSheetBehaviorCalendar: BottomSheetBehavior<*>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rental_detail)

        paymentRecyclerView = findViewById(R.id.det_listview)
        initCalendar()

        paymentBottomSheetCallback = object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    toggleButton!!.isChecked = true
                    viewModel.getPaymentByRentalId()
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    toggleButton!!.isChecked = false
                }
            }

            override fun onSlide(view: View, v: Float) {

            }
        }

        binding.imageTimeDet.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int
                    ->
                    viewModel.time.value = "$hourOfDay:$minute"
                },
                0, 0, true
            )
            timePickerDialog.show()
        }

        binding.markDet.setOnClickListener {
            openColorPicker()
        }

        bottomSheetBehaviorPayments = BottomSheetBehavior.from(bottomSheet)
        toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                bottomSheetBehaviorPayments.setState(BottomSheetBehavior.STATE_EXPANDED)

            } else {
                bottomSheetBehaviorPayments.setState(BottomSheetBehavior.STATE_COLLAPSED)

            }
        }


        bottomSheetBehaviorCalendar = BottomSheetBehavior.from(bottomSheetCal)
        bottomSheetBehaviorCalendar.isHideable = true
        bottomSheetBehaviorCalendar.state = BottomSheetBehavior.STATE_HIDDEN


        binding.imageDateAdd.setOnClickListener {
            bottomSheetBehaviorCalendar.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehaviorPayments.isHideable = true
            bottomSheetBehaviorPayments.state = BottomSheetBehavior.STATE_HIDDEN
        }
        cancel_add.setOnClickListener {
            bottomSheetBehaviorCalendar.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehaviorCalendar.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehaviorPayments.isHideable = false
            bottomSheetBehaviorPayments.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        save_add.setOnClickListener {
            println(calendarView!!.selectedDays[0])
            println(calendarView!!.selectedDays[calendarView!!.selectedDays.size - 1])

            val start = formatDate.format(calendarView!!.selectedDays[0].calendar.time)
            val end =
                formatDate.format(calendarView!!.selectedDays[calendarView!!.selectedDays.size - 1].calendar.time)

            viewModel.startDate.value = start
            viewModel.endDate.value = end

            bottomSheetBehaviorCalendar.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehaviorPayments.isHideable = false
            bottomSheetBehaviorPayments.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        registerBindingAndBaseObservers()

    }

    override fun onResume() {
        super.onResume()
        bottomSheetBehaviorPayments.addBottomSheetCallback(paymentBottomSheetCallback)

    }

    override fun onPause() {
        bottomSheetBehaviorPayments.removeBottomSheetCallback(paymentBottomSheetCallback)
        super.onPause()
    }

    private fun registerBindingAndBaseObservers() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        registerBaseObservers(viewModel)
        registerRentalDetailObservers()
    }

    private fun registerRentalDetailObservers() {
        registerPhoneDialog()
        registerPaymentDialog()
        registerPaymentsListObserver()
    }

    private fun registerPaymentsListObserver() {
        viewModel.payments.observeOnlyNotNull(this) {
            prepareRecyclerView(it)
        }
    }

    private fun registerPaymentDialog() {
        viewModel.paymentDialog.observeOnlyNotNull(this) {
            if (!isFinishing) {
                CustomPaymentDialog(
                    this,
                    it.payment,
                    it.paymentDialogListener,
                    it.dismissActionBlock
                ).show()
            }
        }
    }

    private fun registerPhoneDialog() {
        viewModel.phoneDialog.observeOnlyNotNull(
            this
        ) { phoneDialog ->
            showPhoneDialog(
                phoneDialog.stringTel,
                phoneDialog.dismissActionBlock
            )
        }
    }

    private fun showPhoneDialog(stringTel: String, dismissActionBlock: (() -> Unit)?) {
        if (!isFinishing) {
            CustomPhoneDialog(
                this,
                stringTel,
                viewModel,
                dismissActionBlock
            ).show()
        }
    }

    private fun openColorPicker() {
        val onAmbilWarnaListener = object : AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog) {

            }

            override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                viewModel.setRentalColor(color)
                initCalendar()
            }
        }
        val colorPicker = AmbilWarnaDialog(
            this,
            viewModel.rentalColor.value ?: R.color.colorPrimary,
            onAmbilWarnaListener
        )
        colorPicker.show()
    }

    private fun initCalendar() {
        calendarView = findViewById(R.id.calendar_view)
        calendarView!!.calendarOrientation = OrientationHelper.HORIZONTAL
        calendarView!!.selectionType = SelectionType.RANGE
        calendarView!!.selectedDayBackgroundColor =
            viewModel.rentalColor.value ?: R.color.colorPrimary

        if (calendarView!!.selectionManager is RangeSelectionManager) {
            val rangeSelectionManager = calendarView!!.selectionManager as RangeSelectionManager


            val sCalendar = Calendar.getInstance()
            sCalendar.time = viewModel.sdate
            val eCalendar = Calendar.getInstance()
            eCalendar.time = viewModel.edate

            rangeSelectionManager.toggleDay(Day(sCalendar))
            rangeSelectionManager.toggleDay(Day(eCalendar))
            calendarView!!.update()
        }
    }


    private fun prepareRecyclerView(payments: ArrayList<LocataireWithPayment>) {

        customListAdapter.setData(payments)
        customListAdapter.setSwipeListener(viewModel)
        customListAdapter.setClickListener(viewModel)

        val colorDrawableBackground = ColorDrawable(Color.parseColor("#ff0000"))
        val deleteIcon = ContextCompat.getDrawable(
            this,
            R.drawable.ic_delete_white_24dp
        )!!

        paymentRecyclerView.apply {
            setHasFixedSize(true)
            adapter = customListAdapter
            layoutManager =
                LinearLayoutManager(this@RentalDetailActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                viewHolder2: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                (customListAdapter).removeItem(
                    viewHolder.bindingAdapterPosition
                )
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMarginVertical =
                    (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {
                    colorDrawableBackground.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt(),
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.left + iconMarginVertical,
                        itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                        itemView.bottom - iconMarginVertical
                    )
                } else {
                    colorDrawableBackground.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    deleteIcon.setBounds(
                        itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth,
                        itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical,
                        itemView.bottom - iconMarginVertical
                    )
                    deleteIcon.level = 0
                }

                colorDrawableBackground.draw(c)

                c.save()

                if (dX > 0)
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else
                    c.clipRect(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )

                deleteIcon.draw(c)

                c.restore()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }


        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(paymentRecyclerView)
    }

    override fun navigate(navigationTo: Navigation) {
        super.navigate(navigationTo)
        when (navigationTo) {
            is Navigation.Back -> {
                setResult(REQUEST_CODE)
                finish()
            }
        }
    }
}
