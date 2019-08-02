package com.rent

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.rent.data.LocationServices
import com.rent.data.Model
import com.rent.tools.PhoneGrantings
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers


class MainActivity : AppCompatActivity() {
    private var disposable: Disposable? = null
    private val locationService by lazy {
        LocationServices.create()
    }

    private var locations: ArrayList<Model.location>? = ArrayList()
    private var location: Model.location? = null


    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    private fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction { add(frameId, fragment) }
    }

    private fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction { replace(frameId, fragment) }
    }
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                replaceFragment(HomeFragment.newInstance(), R.id.page)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                replaceFragment(LocationFragment.newInstance(), R.id.page)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                replaceFragment(PaymentFragment.newInstance(), R.id.page)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        println("in main activity")
        if (PhoneGrantings.isNetworkAvailable(this))
            selectLocationById()
        addFragment(HomeFragment.newInstance(), R.id.page)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }


    private fun selectLocations() {

        disposable =
            locationService.selectLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        locations = result as ArrayList<Model.location>?
                        println("hhhhhhhhhhhh $locations")
                    },
                    { error -> println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa") }
                )
    }

    private fun selectLocationById() {

        disposable =
            locationService.selectLocationById(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        location = result as Model.location?
                        println("hhhhhhhhhhhh $location")
                    },
                    { error -> println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa") }
                )
    }
}
