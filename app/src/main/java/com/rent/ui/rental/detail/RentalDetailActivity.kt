package com.rent.ui.rental.detail

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.*
import com.applikeysolutions.cosmocalendar.model.Day
import com.applikeysolutions.cosmocalendar.selection.RangeSelectionManager
import com.applikeysolutions.cosmocalendar.utils.SelectionType
import com.applikeysolutions.cosmocalendar.view.CalendarView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.rent.R
import com.rent.base.BaseActivity
import com.rent.ui.shared.adapter.PaymentListAdapter
import com.rent.ui.shared.view.ViewDialog
import com.rent.data.LocataireServices
import com.rent.data.LocationServices
import com.rent.data.PaymentServices
import com.rent.data.model.payment.Payment
import com.rent.data.model.rental.Rental
import com.rent.databinding.ActivityRentalDetailBinding
import com.rent.global.helper.ViewModelFactory
import com.rent.tools.PhoneGrantings
import com.rent.ui.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_rental_detail.*
import kotlinx.android.synthetic.main.add_cal_bottomsheet.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import yuku.ambilwarna.AmbilWarnaDialog
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class RentalDetailActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: RentalDetailViewModel by viewModels { viewModelFactory }

    private lateinit var viewDialog: ViewDialog
    private lateinit var locationOld: Rental
    private val paymentService by lazy {
        PaymentServices.create()
    }
    private val locationService by lazy {
        LocationServices.create()
    }
    private val locataireService by lazy {
        LocataireServices.create()
    }

    private var disposable: Disposable? = null
    private var payments: MutableList<Payment>? = ArrayList()
    var list: RecyclerView? = null

    private lateinit var customListAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable

    private var stringTel: String = "-"

    lateinit var myDialog: Dialog

    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0


    private var mDefaultColor: Int = 0
    private lateinit var newStringColor: String
    private lateinit var sdate: Date
    private lateinit var edate: Date
    private var calendarView: CalendarView? = null

    private lateinit var binding:ActivityRentalDetailBinding

    @SuppressLint("SetTextI18n", "Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rental_detail)

        val actionBar = (this as AppCompatActivity).supportActionBar
        actionBar!!.title = "Detail Location"

        val intentContent: String

        val intent = intent
        if (intent.hasExtra("myObject2")) { // actions when this activity is called from favourites list
            intentContent = intent.getStringExtra("myObject2")
            locationOld = Gson().fromJson(intentContent, Rental::class.java)
            println("location $locationOld")
            stringTel = locationOld.locataire.numTel
        }
        mDefaultColor = Color.parseColor(locationOld.color)


        val format = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault())
        sdate = format.parse(locationOld.dateDebut)
        edate = format.parse(locationOld.dateFin)

        det_cin.setText(locationOld.locataire.cin)
        det_text.setText(locationOld.locataire.fullName)
//        val format = SimpleDateFormat("yyyy-mm-dd mm:ss", Locale.getDefault())
//        val date = format.parse(location!!.start)

        det_date_debu.text = locationOld.dateDebut
        det_date_fin.text = locationOld.dateFin
        list = findViewById<RecyclerView>(R.id.det_listview)

        myDialog = Dialog(this)



        viewDialog = ViewDialog(this)


        val c = Calendar.getInstance()
        c.time = sdate

        mHour = c.get(Calendar.HOUR_OF_DAY)
        mMinute = c.get(Calendar.MINUTE)
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
        det_date_debu.text = mYear.toString() + "-" + (mMonth + 1) + "-" + (mDay)

        c.time = edate


        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
        det_date_fin.text = mYear.toString() + "-" + (mMonth + 1) + "-" + (mDay)

        add_time_det.text = "$mHour:$mMinute"

        initCalendar()


        imageTimeDet.setOnClickListener {

            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int
                    ->
                    add_time_det.text = "$hourOfDay:$minute"
                },
                0, 0, true
            )
            timePickerDialog.show()
        }

        val background = markDet.background
        (background as GradientDrawable).setColor(mDefaultColor)

        markDet.setOnClickListener {
            openColorPicker()
        }

        val bottomSheetListBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                bottomSheetListBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)

            } else {
                bottomSheetListBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

            }
        }

        bottomSheetListBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    toggleButton!!.isChecked = true
                    if (PhoneGrantings.isNetworkAvailable(applicationContext)) // online actions
                        selectLocPayments()
                    else
                        Toast.makeText(
                            applicationContext,
                            "Internet Non Disponible",
                            Toast.LENGTH_SHORT
                        ).show()

                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    toggleButton!!.isChecked = false
                }
            }

            override fun onSlide(view: View, v: Float) {

            }
        })


        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheetCal)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        imageDateAdd.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetListBehavior.isHideable = true
            bottomSheetListBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        cancel_add.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetListBehavior.isHideable = false
            bottomSheetListBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        save_add.setOnClickListener {
            println(calendarView!!.selectedDays[0])
            println(calendarView!!.selectedDays[calendarView!!.selectedDays.size - 1])

            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = format.format(calendarView!!.selectedDays[0].calendar.time)
            val end =
                format.format(calendarView!!.selectedDays[calendarView!!.selectedDays.size - 1].calendar.time)

            det_date_debu.text = start
            det_date_fin.text = end

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetListBehavior.isHideable = false
            bottomSheetListBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        add_tel_det.setOnClickListener {
            ShowPopupTel()
        }

        updateLoc.setOnClickListener {
            if (PhoneGrantings.isNetworkAvailable(applicationContext)) {

                locationOld.dateFin =
                    det_date_fin.text.toString() + " " + add_time_det.text.toString()
                locationOld.dateDebut =
                    det_date_debu.text.toString() + " " + add_time_det.text.toString()
                locationOld.color = newStringColor
                updateLocation()
            } else
                Toast.makeText(this, "Internet Non Disponible", Toast.LENGTH_SHORT).show()

        }


        updateLoca.setOnClickListener {
            if (PhoneGrantings.isNetworkAvailable(applicationContext)) {

                locationOld.locataire.cin = det_cin.text.toString()
                locationOld.locataire.fullName = det_text.text.toString()
                updateLocataire()
            } else
                Toast.makeText(this, "Internet Non Disponible", Toast.LENGTH_SHORT).show()

        }
    }


    private fun openColorPicker() {
        val onAmbilWarnaListener = object : AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog) {

            }

            override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                mDefaultColor = color
                val background = markDet.background
                (background as GradientDrawable).setColor(mDefaultColor)
                newStringColor = String.format("#%06X", 0xFFFFFF and mDefaultColor)
                initCalendar()
            }
        }
        val colorPicker = AmbilWarnaDialog(this, mDefaultColor, onAmbilWarnaListener)
        colorPicker.show()
    }

    private fun initCalendar() {
        calendarView = findViewById(R.id.calendar_view)
        calendarView!!.calendarOrientation = OrientationHelper.HORIZONTAL
        calendarView!!.selectionType = SelectionType.RANGE
        calendarView!!.selectedDayBackgroundColor = mDefaultColor

        if (calendarView!!.selectionManager is RangeSelectionManager) {
            val rangeSelectionManager = calendarView!!.selectionManager as RangeSelectionManager


            val sCalendar = Calendar.getInstance()
            sCalendar.time = sdate
            val eCalendar = Calendar.getInstance()
            eCalendar.time = edate

            rangeSelectionManager.toggleDay(Day(sCalendar))
            rangeSelectionManager.toggleDay(Day(eCalendar))
            calendarView!!.update()
        }
    }


    private fun prepareRecyclerView() {

        customListAdapter = PaymentListAdapter(payments!!, this, this)
        viewManager = LinearLayoutManager(this)

        colorDrawableBackground = ColorDrawable(Color.parseColor("#ff0000"))
        deleteIcon = ContextCompat.getDrawable(
            this,
            R.drawable.ic_delete_white_24dp
        )!!

        list!!.apply {
            setHasFixedSize(true)
            adapter = customListAdapter
            layoutManager = viewManager
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
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
                (customListAdapter as PaymentListAdapter).removeItem(
                    viewHolder.adapterPosition,
                    viewHolder
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
        itemTouchHelper.attachToRecyclerView(list)
    }


    @SuppressLint("SetTextI18n")
    fun ShowPopup(v: View) {
        myDialog.setCanceledOnTouchOutside(false)

        myDialog.show()

        myDialog.setContentView(R.layout.custompopup2)

        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txt: TextView = myDialog.findViewById(R.id.txtclose) as TextView
        val btn: Button = myDialog.findViewById(R.id.btnfollow) as Button
        val amount: EditText = myDialog.findViewById(R.id.add_amount) as EditText
        val dateBtn: ImageView = myDialog.findViewById(R.id.imageDate) as ImageView
        val timeBtn: ImageView = myDialog.findViewById(R.id.imageTime) as ImageView
        val dateText: TextView = myDialog.findViewById(R.id.add_date) as TextView

        val time = myDialog.findViewById(R.id.add_time) as TextView

        val c = Calendar.getInstance(Locale.FRANCE)
        mHour = c.get(Calendar.HOUR_OF_DAY)
        mMinute = c.get(Calendar.MINUTE)
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)

        time.text = "$mHour:$mMinute"
        dateText.text = mYear.toString() + "-" + (mMonth + 1) + "-" + (mDay)





        timeBtn.setOnClickListener {


            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view: TimePicker, hourOfDay: Int, minute: Int
                    ->
                    time.text = "$hourOfDay:$minute"
                },
                mHour, mMinute, true
            )
            timePickerDialog.show()
        }

        dateBtn.setOnClickListener {


            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth
                    ->
                    dateText.text = year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                },
                mYear, // Initial year selection
                mMonth, // Initial month selection
                mDay // Inital day selection
            )
            datePickerDialog.show()
        }


        var selectedType = "Avance"
        val users = arrayOf("Avance", "Paiement")
        val spinner: Spinner = myDialog.findViewById(R.id.types_spinner)
        val adapter = ArrayAdapter(
            this,
            R.layout.drop_down_list_types, users
        )
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                selectedType = parent.getItemAtPosition(pos).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }


        txt.setOnClickListener { myDialog.dismiss() }
        btn.setOnClickListener {
            println(amount.text.toString())
            if (PhoneGrantings.isNetworkAvailable(this))

                if (amount.text.isEmpty())
                    Toast.makeText(this, "Manque d'information", Toast.LENGTH_LONG).show()
                else
                    addPayment(
                        Integer.parseInt(amount.text.toString()),
                        dateText.text.toString() + " " + time.text.toString() + ":00",
                        selectedType
                    )
            else
                Toast.makeText(this, "Internet Non Disponible", Toast.LENGTH_SHORT).show()

        }

    }

    private fun ShowPopupTel() {
        myDialog.setCanceledOnTouchOutside(false)

        myDialog.show()

        myDialog.setContentView(R.layout.dialog_custom_phone)

//        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txt: TextView = myDialog.findViewById(R.id.txtcloseTel) as TextView
        val imgAdd: ImageView = myDialog.findViewById(R.id.addTelimg) as ImageView
        val addTel1: EditText = myDialog.findViewById(R.id.addtel) as EditText

        val btn: Button = myDialog.findViewById(R.id.saveTel) as Button
        val myLayout: LinearLayout = myDialog.findViewById(R.id.listnumbers) as LinearLayout


        val telArray = ArrayList<EditText>()

        if (stringTel != "") {
            val tab = stringTel.split(",")
            addTel1.setText(tab[0])
            for (i in 1 until tab.size) {
                val myEditText = EditText(this) // Pass it an Activity or Context
                myEditText.hint = "Telephone"
                myEditText.inputType = InputType.TYPE_CLASS_PHONE
                myEditText.setText(tab[i])
                myEditText.layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ) // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                myLayout.addView(myEditText)
                telArray.add(myEditText)
            }

        }

        imgAdd.setOnClickListener {
            val myEditText = EditText(this) // Pass it an Activity or Context
            myEditText.hint = "Telephone"
            myEditText.inputType = InputType.TYPE_CLASS_PHONE
            myEditText.layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ) // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
            myLayout.addView(myEditText)
            telArray.add(myEditText)
        }



        txt.setOnClickListener { myDialog.dismiss() }
        btn.setOnClickListener {
            stringTel = addTel1.text.toString()
            telArray.forEach {
                if (it.text.toString().isNotEmpty())
                    stringTel = stringTel + "," + it.text.toString()
            }
            println(stringTel)
            locationOld.locataire.numTel = stringTel
            myDialog.dismiss()
        }

    }


    private fun addPayment(amount: Int, date: String, type: String) {
        val newPayment = Payment(0, date, amount, type, locationOld)
        viewDialog.showDialog()
        println(newPayment)

        disposable =
            paymentService.addPayment(newPayment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        viewDialog.hideDialog()

                        Toast.makeText(this, "Payment Ajouté", Toast.LENGTH_LONG).show()
                        myDialog.dismiss()
                    },
                    { error ->
                        Toast.makeText(this, "Opération échouée!", Toast.LENGTH_LONG).show()
                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                        viewDialog.hideDialog()
                    }
                )
    }


    private fun selectLocPayments() {
        viewDialog.showDialog()

        disposable =
            paymentService.selectLocPayments(locationOld.idRental)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        payments = result as MutableList<Payment>?
                        println("hhhhhhhhhhhh $payments")
                        prepareRecyclerView()
                        customListAdapter.notifyDataSetChanged()

                        viewDialog.hideDialog()

                    },
                    { error ->
                        Toast.makeText(this, "Opération échouée!", Toast.LENGTH_LONG).show()
                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                        viewDialog.hideDialog()
                    }
                )
    }

    private fun updateLocataire() {
        viewDialog.showDialog()

        disposable =
            locataireService.updateLocataire(locationOld.locataire)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        viewDialog.hideDialog()

                        Toast.makeText(this, "Modification avec succée", Toast.LENGTH_LONG).show()

                    },
                    { error ->
                        Toast.makeText(this, "Opération échouée!", Toast.LENGTH_LONG).show()
                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                        viewDialog.hideDialog()
                    }
                )

    }

    private fun updateLocation() {
        viewDialog.showDialog()

        disposable =
            locationService.updateLocation(locationOld)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        viewDialog.hideDialog()

                        Toast.makeText(this, "Modification avec succée", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity().javaClass)

                        startActivity(intent)
                    },
                    { error ->
                        Toast.makeText(this, "Opération échouée!", Toast.LENGTH_LONG).show()
                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                        viewDialog.hideDialog()
                    }
                )
    }


    private fun delLocation() {
        viewDialog.showDialog()
        disposable =
            locationService.deleteLocation(locationOld.idRental)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->

                        println("msg  $result")
                        if (result.message == "location was deleted.") {

                            Toast.makeText(this, "Supprimé avec succée", Toast.LENGTH_SHORT).show()
                            val nextScreen = Intent(this, MainActivity::class.java)
                            startActivity(nextScreen)
                            viewDialog.hideDialog()

                        }
                    },
                    { error ->
                        viewDialog.hideDialog()
                        Toast.makeText(this, "Opération échouée!", Toast.LENGTH_LONG).show()
                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                    }
                )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_det, menu)
        super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.del_loc -> {
                if (PhoneGrantings.isNetworkAvailable(applicationContext))
                    delLocation()
                else
                    Toast.makeText(this, "Internet Non Disponible", Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }
}
