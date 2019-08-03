package com.rent

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loc_detail)

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
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    toggleButton!!.isChecked = false
                }
            }

            override fun onSlide(view: View, v: Float) {

            }
        })


        if (PhoneGrantings.isNetworkAvailable(this)) // online actions
            selectLocPayments()
        else
            Toast.makeText(this, "Internet Non Disponible", Toast.LENGTH_SHORT).show()



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
        customListAdapter = CustomListAdapter(payments!!)
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

}
