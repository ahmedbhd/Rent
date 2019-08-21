package com.rent.adapters

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat.startActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.daimajia.swipe.SimpleSwipeListener
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.BaseSwipeAdapter
import com.google.gson.Gson
import com.rent.LocDetailActivity
import com.rent.MainActivity
import com.rent.R
import com.rent.data.LocationServices
import com.rent.data.Model
import com.rent.tools.PhoneGrantings

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import android.text.format.DateFormat;

// Online Favourite list Adapter
class LocListViewAdapter(private val mContext: Context, private var locations: MutableList<Model.location>, private val manager: FragmentActivity?) : BaseSwipeAdapter() {
    override fun getCount(): Int {
        return locations.count()
    }


    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }

    @SuppressLint("InflateParams")
    override fun generateView(position: Int, parent: ViewGroup): View {
        val v = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null)
        val swipeLayout = v.findViewById(getSwipeLayoutResourceId(position)) as SwipeLayout
        swipeLayout.addSwipeListener(object : SimpleSwipeListener() {
            override fun onOpen(layout: SwipeLayout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash))
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.eye))
            }
        })


        Log.d("position", locations.toString())



        swipeLayout.setOnClickListener {
            run {
                val intent = Intent(mContext, LocDetailActivity().javaClass)
                val res: Model.location? = locations[position]
                println("res ${res.toString()}")
                intent.putExtra("myObject2", Gson().toJson(res))
                manager!!.startActivity(intent)
            }
        }

        // delete action
        v.findViewById<Button>(R.id.delete).setOnClickListener {
            delLocation(locations[position].id, position)
        }

         //display the details in detail activity
        v.findViewById<Button>(R.id.open).setOnClickListener {
//            phoneCall(locations[position].tel)
        }
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
        cin.text = p.locataire.cin
        val format =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        var date = format.parse(p.dateDebut)
        var mDay          =  DateFormat.format("dd",   date)
        var mMonth  = DateFormat.format("MM",   date)
        var mYear         = DateFormat.format("yyyy", date)
        var mHour         = DateFormat.format("hh", date)
        var mMinute         = DateFormat.format("mm", date)

        dateStart.text = "$mYear-$mMonth-$mDay"
        timeStart.text = "$mHour:$mMinute"

         date = format.parse(p.dateFin)
         mDay          =  DateFormat.format("dd",   date)
         mMonth  = DateFormat.format("MM",   date)
         mYear         = DateFormat.format("yyyy", date)
         mHour         = DateFormat.format("hh", date)
         mMinute         = DateFormat.format("mm", date)

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

        disposable =
            locationServices.deleteLocation( id )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { result ->
                                    run {

                                        println(result)
                                        if (result=="success") {

                                            Toast.makeText(mContext, "Delete succeeded", Toast.LENGTH_SHORT).show()

                                            locations.removeAt(position)
                                            notifyDataSetChanged()

                                        }
                                    }
                                },
                                { error -> println(error.message+"aaaaaaaaa") }
                        )
    }

    private fun phoneCall(tel:String){
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$tel")
        println("in call")
        if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        println("to call")
        manager!!.startActivity(callIntent)
    }

}