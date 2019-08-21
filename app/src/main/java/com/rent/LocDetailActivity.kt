package com.rent

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.applikeysolutions.cosmocalendar.model.Day
import com.applikeysolutions.cosmocalendar.selection.RangeSelectionManager
import com.applikeysolutions.cosmocalendar.utils.SelectionType
import com.applikeysolutions.cosmocalendar.view.CalendarView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.rent.adapters.CustomListAdapter
import com.rent.adapters.util.TimePickerFragment.Companion.time
import com.rent.data.Model
import com.rent.data.PaymentServices
import com.rent.tools.PhoneGrantings
import com.rent.tools.getColorCompat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_loc_detail.*
import kotlinx.android.synthetic.main.activity_loc_detail.imageDateAdd
import kotlinx.android.synthetic.main.add_cal_bottomsheet.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import yuku.ambilwarna.AmbilWarnaDialog
import java.text.SimpleDateFormat
import java.util.*


class LocDetailActivity : AppCompatActivity() {

    private var location:Model.location? = null
    private val paymentService by lazy {
        PaymentServices.create()
    }
    private var disposable: Disposable? = null
    private var payments: MutableList<Model.payment>? = ArrayList()
    var list: RecyclerView? = null

    private lateinit var customListAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable


    lateinit var myDialog: Dialog

    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0



    private var mDefaultColor :Int = 0
    private lateinit var newStringColor:String
    private lateinit var sdate:Date
    private lateinit var edate:Date
    private var calendarView: CalendarView? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loc_detail)
        val actionBar = (this as AppCompatActivity).supportActionBar
        actionBar!!.title = "Detail Location"

        val intentContent: String

        val intent = intent
        if (intent.hasExtra("myObject2")) { // actions when this activity is called from favourites list
            intentContent = intent.getStringExtra("myObject2")
            location = Gson().fromJson(intentContent, Model.location::class.java)
            println("location $location")
        }
        val format =  SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        edate = format.parse(location!!.dateFin)
        sdate = format.parse(location!!.dateDebut)

        initViews()
        det_cin.setText(location!!.locataire.cin)
        det_text.setText( location!!.locataire.full_name)
//        val format = SimpleDateFormat("yyyy-mm-dd mm:ss", Locale.getDefault())
//        val date = format.parse(location!!.start)

        det_date_debu.text = location!!.dateDebut
        det_date_fin.text = location!!.dateFin
        list = findViewById<RecyclerView>(R.id.det_listview)

        myDialog =  Dialog(this)






        val c = Calendar.getInstance()
        c.time = sdate

        mHour = c.get(Calendar.HOUR_OF_DAY)
        mMinute = c.get(Calendar.MINUTE)
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
        det_date_debu.text = mYear.toString()+ "-"+(mMonth+ 1)+ "-" + (mDay )

        c.time = edate


        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
        det_date_fin.text = mYear.toString()+ "-"+(mMonth+ 1)+ "-" + (mDay )

        add_time_det.text = "$mHour:$mMinute"

        imageTimeDet.setOnClickListener {

            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay:Int, minute: Int
                    -> add_time_det.text = "$hourOfDay:$minute"
                },
                0, 0, true
            )
            timePickerDialog.show()
        }

        mDefaultColor = Color.parseColor(location!!.color)
        val background = markDet.background
        (background as GradientDrawable).setColor(mDefaultColor)

        markDet.setOnClickListener {
            openColorPicker(this)
        }

        val bottomSheetListBehavior : BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                bottomSheetListBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)

            } else {
                bottomSheetListBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)

            }
        }

        bottomSheetListBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    toggleButton!!.isChecked = true
                    if (PhoneGrantings.isNetworkAvailable(applicationContext)) // online actions
                        selectLocPayments()
                    else
                        Toast.makeText(applicationContext, "Internet Non Disponible", Toast.LENGTH_SHORT).show()

                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    toggleButton!!.isChecked = false
                }
            }

            override fun onSlide(view: View, v: Float) {

            }
        })



        val bottomSheetBehavior : BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheetCal )
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        imageDateAdd.setOnClickListener{
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
            println(calendarView!!.selectedDays[calendarView!!.selectedDays.size-1])

            val format =  SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val start = format.format(calendarView!!.selectedDays[0].calendar.time)
            val end = format.format( calendarView!!.selectedDays[calendarView!!.selectedDays.size-1].calendar.time)

            det_date_debu.text = start
            det_date_fin.text = end
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            bottomSheetListBehavior.isHideable = false
            bottomSheetListBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

//        floatingActionButton.setOnClickListener {ShowPopup()}
    }


    private fun openColorPicker(mContext: Context){
        val onAmbilWarnaListener = object: AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun  onCancel (dialog : AmbilWarnaDialog){

            }
            override fun onOk(dialog: AmbilWarnaDialog, color:Int){
                mDefaultColor = color
                val background = markDet.background
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




            val sCalendar = Calendar.getInstance()
            sCalendar.time = sdate
            val eCalendar = Calendar.getInstance()
            eCalendar.time = edate

            rangeSelectionManager.toggleDay( Day(sCalendar))
            rangeSelectionManager.toggleDay( Day(eCalendar))
            calendarView!!.update()
        }
    }


    private fun prepareRecyclerView(){
        val supportFragmentManager = supportFragmentManager

        customListAdapter = CustomListAdapter(payments!! , this)
        viewManager = LinearLayoutManager(this)

        colorDrawableBackground = ColorDrawable(Color.parseColor("#ff0000"))
        deleteIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_white_24dp)!!

        list!!.apply {
            setHasFixedSize(true)
            adapter = customListAdapter
            layoutManager = viewManager
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                (customListAdapter as CustomListAdapter).removeItem(viewHolder.adapterPosition, viewHolder)
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
                val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {
                    colorDrawableBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
                } else {
                    colorDrawableBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    deleteIcon.setBounds(itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth, itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical, itemView.bottom - iconMarginVertical)
                    deleteIcon.level = 0
                }

                colorDrawableBackground.draw(c)

                c.save()

                if (dX > 0)
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

                deleteIcon.draw(c)

                c.restore()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }


        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(list)
    }


    @SuppressLint("SetTextI18n")
    fun ShowPopup(v :View) {
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
        dateText.text = mYear.toString()+ "-"+(mMonth+ 1)+ "-" + (mDay )





        timeBtn.setOnClickListener {


            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view: TimePicker, hourOfDay:Int, minute: Int
                    -> time.text = "$hourOfDay:$minute"
                },
                mHour, mMinute, true
            )
            timePickerDialog.show()
        }

        dateBtn.setOnClickListener {



            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth
                    -> dateText.text =year.toString()+"-"+ dayOfMonth+ "-" + (monthOfYear + 1)  },
                mYear, // Initial year selection
                mMonth, // Initial month selection
                mDay // Inital day selection
            )
            datePickerDialog.show()
        }


        val users = arrayOf("Avance", "Reste")
        val spinner: Spinner = myDialog.findViewById(com.rent.R.id.types_spinner)
        val adapter = ArrayAdapter(this,R.layout.drop_down_list_types , users)
        spinner.adapter = adapter
        spinner.onItemSelectedListener =  object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                println("hhhhhhhhh"+parent.getItemAtPosition(pos))

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }


        txt.setOnClickListener { myDialog.dismiss() }
        btn.setOnClickListener {
            println(amount.text.toString())

//            addPayment(Integer.parseInt(amount.text.toString()),date,type.text.toString())
            myDialog.dismiss()
        }

    }

    private fun addPayment ( amount: Int,  date:String,  type:String){
        println(location!!.id)
        disposable =
            paymentService.addPayment(Model.payment(0,date,amount,type,location!!))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                       println(result+"dddddddddddddddddd")
                        if (result == "success")
                            Toast.makeText(this, "Payment Ajouté", Toast.LENGTH_LONG).show()

                    },
                    { error -> println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa") }
                )
    }
    private fun selectLocPayments() {
        disposable =
            paymentService.selectLocPayments(location!!.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        payments = result as MutableList<Model.payment>?
                        println("hhhhhhhhhhhh $payments")
                        prepareRecyclerView()
                        customListAdapter.notifyDataSetChanged()


                    },
                    { error -> println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa") }
                )
    }
}
