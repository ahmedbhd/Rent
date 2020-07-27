package com.rent.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.threetenabp.AndroidThreeTen
import com.rent.R
import com.rent.base.BaseActivity
import com.rent.ui.shared.adapter.util.LocaleHelper
import com.rent.data.LocationServices
import com.rent.data.model.rental.Rental
import com.rent.global.helper.ViewModelFactory
import com.rent.tools.PhoneGrantings
import com.rent.ui.main.home.HomeFragment
import com.rent.ui.main.payment.PaymentFragment
import com.rent.ui.main.rental.RentalFragment
import com.rent.ui.splash.SplashViewModel
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


class MainActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    private var disposable: Disposable? = null
    private val locationService by lazy {
        LocationServices.create()
    }

    private var locations: ArrayList<Rental>? = ArrayList()
    private var location: Rental? = null


    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    private fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction { add(frameId, fragment) }
    }

    private fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction { replace(frameId, fragment) }
    }

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    replaceFragment(HomeFragment.newInstance(), R.id.page)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_dashboard -> {
                    replaceFragment(
                        RentalFragment.newInstance(),
                        R.id.page
                    )
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    replaceFragment(
                        PaymentFragment.newInstance(),
                        R.id.page
                    )
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
        AndroidThreeTen.init(this)
        LocaleHelper.setLocale(applicationContext, "fr")

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
                        locations = result as ArrayList<Rental>?
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
                        location = result
                        println("hhhhhhhhhhhh $location")
                    },
                    { error -> println(error.message + "aaaaaaaaaaaaaaaaaaaaaaaaaaaa") }
                )
    }


}
