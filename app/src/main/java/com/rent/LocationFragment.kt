package com.rent


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.daimajia.swipe.util.Attributes
import com.rent.adapters.LocListViewAdapter
import com.rent.data.LocationServices
import com.rent.data.Model
import com.rent.tools.PhoneGrantings
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class LocationFragment : Fragment() {
    private var disposable: Disposable? = null
    private val locationService by lazy {
        LocationServices.create()
    }
    private var locations: ArrayList<Model.location>? = ArrayList()
    private var mAdapter: LocListViewAdapter? = null
    var list: ListView? = null

    companion object {
        fun newInstance(): LocationFragment {
            return LocationFragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_location, container, false)

        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.title = "Locations"

        list = root.findViewById(R.id.flistfav) as ListView

        if (PhoneGrantings.isNetworkAvailable(activity!!)) // online actions
            selectLocations()
        else
            Toast.makeText(context, "Internet Non Disponible", Toast.LENGTH_SHORT).show()


            // Inflate the layout for this fragment
        return root
    }


    private fun selectLocations() {

        disposable =
            locationService.selectLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        mAdapter = LocListViewAdapter(context!!, result as MutableList<Model.location>, activity)
                        mAdapter!!.mode = Attributes.Mode.Single
                        list!!.adapter = mAdapter
                        println("hhhhhhhhhhhh $locations")
                    },
                    { error -> println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa") }
                )
    }

}
