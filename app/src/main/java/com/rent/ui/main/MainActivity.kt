package com.rent.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jakewharton.threetenabp.AndroidThreeTen
import com.rent.R
import com.rent.base.BaseActivity
import com.rent.databinding.ActivityMainBinding
import com.rent.global.helper.ViewModelFactory
import com.rent.global.helper.LocaleHelper
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


class MainActivity : BaseActivity(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_calendar -> {
                    navController.navigate(R.id.calendarFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_rentals -> {
                    navController.navigate(R.id.rentalFragment)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_payments -> {
                    navController.navigate(R.id.paymentFragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.page)
        navController.setGraph(R.navigation.nav_graph, intent.extras)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        registerBaseObservers(viewModel)

        AndroidThreeTen.init(this)
        LocaleHelper.setLocale(applicationContext, "fr")

        binding.navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector
}
