package com.rent.ui.shared.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.BaseSwipeAdapter
import com.rent.R
import com.rent.ui.shared.view.ViewDialog
import com.rent.data.LocationServices
import com.rent.data.model.rental.Rental
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// Online Favourite list Adapter
class LocListViewAdapter(
    private val mContext: Context,
    private val activity: Activity,
    private var locations: ArrayList<Rental>,
    private val manager: FragmentActivity?
) : BaseSwipeAdapter() {


    private lateinit var viewDialog: ViewDialog
    lateinit var myDialog: Dialog

    override fun getCount(): Int {
        return locations.count()
    }


    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }

    @SuppressLint("InflateParams")
    override fun generateView(position: Int, parent: ViewGroup): View {
        val v = LayoutInflater.from(mContext).inflate(R.layout.rental_list_item, null)
        val swipeLayout = v.findViewById(getSwipeLayoutResourceId(position)) as SwipeLayout
//        swipeLayout.addSwipeListener(object : SimpleSwipeListener() {
//            override fun onOpen(layout: SwipeLayout) {
//                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash))
//                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.eye))
//            }
//        })


        swipeLayout.setOnDoubleClickListener { _, _ ->
            run {
//                val intent = Intent(mContext, LocDetailActivity().javaClass)
//                val res: Model.location? = locations[position]
//                println("res ${res.toString()}")
//                intent.putExtra("myObject2", Gson().toJson(res))
//                manager!!.startActivity(intent)
                println("position$position")

            }
        }

        // delete action
//        v.findViewById<Button>(R.id.delete).setOnClickListener {
//            delLocation(locations[position].id, position)
//        }
//        v.findViewById<Button>(R.id.open).setOnClickListener {
//            ShowPopupTel(locations[position].locataire.num_tel)
//        }

        //display the details in detail activity
        myDialog = Dialog(mContext)
        viewDialog = ViewDialog(activity)


        return v
    }

    @SuppressLint("SetTextI18n")
    override fun fillValues(position: Int, convertView: View) {


        val p = locations[position]

        val cin = convertView.findViewById(R.id.loc_cin) as TextView
        val dateStart = convertView.findViewById(R.id.loc_start) as TextView
        val dateEnd = convertView.findViewById(R.id.loc_end) as TextView
        val timeEnd = convertView.findViewById(R.id.etime) as TextView
        val timeStart = convertView.findViewById(R.id.stime) as TextView


        // Populate the data into the template view using the data object
        cin.text = p.locataire.fullName
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        var date = format.parse(p.dateDebut)
        var mDay = DateFormat.format("dd", date)
        var mMonth = DateFormat.format("MM", date)
        var mYear = DateFormat.format("yyyy", date)
        var mHour = DateFormat.format("hh", date)
        var mMinute = DateFormat.format("mm", date)

        dateStart.text = "$mYear-$mMonth-$mDay"
        timeStart.text = "$mHour:$mMinute"

        date = format.parse(p.dateFin)
        mDay = DateFormat.format("dd", date)
        mMonth = DateFormat.format("MM", date)
        mYear = DateFormat.format("yyyy", date)
        mHour = DateFormat.format("hh", date)
        mMinute = DateFormat.format("mm", date)

        dateEnd.text = "$mYear-$mMonth-$mDay"
        timeEnd.text = "$mHour:$mMinute"


    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private val locationServices by lazy {
        LocationServices.create()
    }
    private var disposable: Disposable? = null

    //====================================== delete this restaurant from favourite in data base ======================================
    private fun delLocation(id: Int, position: Int) {
        viewDialog.showDialog()
        println("id " + id)
        disposable =
            locationServices.deleteLocation(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->


                        println("msg  " + result)
                        if (result.message == "location was deleted.") {

                            Toast.makeText(mContext, "Delete succeeded", Toast.LENGTH_SHORT).show()

                            locations.removeAt(position)
                            notifyDataSetChanged()
                            viewDialog.hideDialog()


                        }
                    },
                    { error ->
                        viewDialog.hideDialog()
                        Toast.makeText(mContext, "Opération échouée!", Toast.LENGTH_LONG).show()
                        println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                    }
                )
    }

    private fun phoneCall(tel: String) {
//        val callIntent = Intent(Intent.ACTION_CALL)
//        callIntent.data = Uri.parse("tel:$tel")
//        println("in call")
//        if (ActivityCompat.checkSelfPermission(
//                mContext,
//                Manifest.permission.CALL_PHONE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//        println("to call")
//        manager!!.startActivity(callIntent)


        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel"))
        manager!!.startActivity(intent)
    }


    fun ShowPopupTel(stringTel: String) {
        myDialog.setCanceledOnTouchOutside(false)

        myDialog.show()

        myDialog.setContentView(R.layout.dialog_custom_call)

//        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        val txt: TextView = myDialog.findViewById(R.id.txtcloseCall) as TextView
        val addTel1: TextView = myDialog.findViewById(R.id.callnumberCall) as TextView

        val myLayout: LinearLayout = myDialog.findViewById(R.id.listcalls) as LinearLayout




        if (stringTel != "") {
            val tab = stringTel.split(",")
            addTel1.text = tab[0]
            addTel1.setOnClickListener {
                phoneCall(tab[0])
            }
            for (i in 1 until tab.size) {
                val myTextView = TextView(mContext) // Pass it an Activity or Context
                myTextView.text = tab[i]

                myTextView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.phonesettings,
                    0,
                    0,
                    0
                )
                myTextView.compoundDrawablePadding = 5
                myTextView.gravity = Gravity.CENTER
                myTextView.setPadding(5, 5, 5, 5)
                myTextView.textSize = 20f
                val parms = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ) // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                parms.gravity = Gravity.CENTER
                parms.setMargins(5, 5, 5, 5)
                myTextView.layoutParams = parms
                myLayout.addView(myTextView)
                myTextView.setOnClickListener {
                    phoneCall(tab[i])
                }
            }

        } else {
            addTel1.visibility = View.GONE
        }

//        txt.setOnClickListener { myDialog.dismiss() }


    }
}