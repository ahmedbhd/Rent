package com.rent

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ClipData
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.google.android.material.bottomsheet.BottomSheetBehavior
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.rent.adapters.CustomListAdapter
import com.rent.adapters.MainAdapter
import com.rent.adapters.util.RecyclerItemTouchHelperListner
import com.rent.data.Model
import com.rent.data.PaymentServices
import com.rent.tools.PhoneGrantings
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_loc_detail.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import java.util.*
import android.widget.TextView
import com.rent.adapters.util.TimePickerFragment.Companion.time
import kotlinx.android.synthetic.main.custompopup.*
import java.text.SimpleDateFormat


class LocDetailActivity : AppCompatActivity() {

    private var location:Model.location? = null
    private val paymentService by lazy {
        PaymentServices.create()
    }
    private var disposable: Disposable? = null
    private var payments: MutableList<Model.payment>? = ArrayList()
    var list: RecyclerView? = null
    private var dataset = mutableListOf("Chicken", "Fish", "Beef", "Pork", "Lamb")

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

        det_cin.text = location!!.cin
        det_text.text = location!!.text
//        val format = SimpleDateFormat("yyyy-mm-dd mm:ss", Locale.getDefault())
//        val date = format.parse(location!!.start)

        det_date_debu.text = location!!.start
        det_date_fin.text = location!!.end
        list = findViewById<RecyclerView>(R.id.det_listview)

        myDialog =  Dialog(this)


        val bottomSheetBehavior : BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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






//        floatingActionButton.setOnClickListener {ShowPopup()}
    }


    private fun selectLocPayments() {
        disposable =
            paymentService.selectLocPayments(Integer.parseInt(location!!.id))
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


    fun ShowPopup(v :View) {
        myDialog.setCanceledOnTouchOutside(false)

        myDialog.show()

        myDialog.setContentView(R.layout.custompopup2)

        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txt: TextView = myDialog.findViewById(R.id.txtclose) as TextView
        val btn: Button = myDialog.findViewById(R.id.btnfollow) as Button
        val type: EditText = myDialog.findViewById(R.id.add_type) as EditText
        val amount: EditText = myDialog.findViewById(R.id.add_amount) as EditText
        val dateBtn: ImageView = myDialog.findViewById(R.id.imageDate) as ImageView
        val timeBtn: ImageView = myDialog.findViewById(R.id.imageTime) as ImageView
        val dateText: TextView = myDialog.findViewById(R.id.add_date) as TextView

        val time = myDialog.findViewById(com.rent.R.id.add_time) as TextView

        val c = Calendar.getInstance(Locale.FRANCE)
        mHour = c.get(Calendar.HOUR_OF_DAY)
        mMinute = c.get(Calendar.MINUTE)
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)

        time.text = "$mHour:$mMinute"
        dateText.text = mYear.toString()+ "-"+mMonth+ 1+ "-" + (mDay )

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


        txt.setOnClickListener { myDialog.dismiss() }
        btn.setOnClickListener {
            println(amount.text.toString())
            println(type.text.toString())

//            addPayment(Integer.parseInt(amount.text.toString()),date,type.text.toString())
            myDialog.dismiss()
        }

    }

    private fun addPayment ( amount: Int,  date:String,  type:String){
        println(Integer.parseInt(location!!.id))
        disposable =
            paymentService.addPayment(amount,date,Integer.parseInt(location!!.id),type)
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

}
