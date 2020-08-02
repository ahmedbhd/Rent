package com.rent.di.contribute

import com.rent.ui.main.MainActivity
import com.rent.ui.main.MainModule
import com.rent.ui.rental.add.AddRentalActivity
import com.rent.ui.rental.add.AddRentalModule
import com.rent.ui.rental.detail.RentalDetailActivity
import com.rent.ui.rental.detail.RentalDetailModule
import com.rent.ui.splash.SplashActivity
import com.rent.ui.splash.SplashModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ContributeActivityModule {

    @ContributesAndroidInjector(modules = [SplashModule::class])
    abstract fun contributeSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [RentalDetailModule::class])
    abstract fun contributeRentalDetailActivity(): RentalDetailActivity

    @ContributesAndroidInjector(modules = [AddRentalModule::class])
    abstract fun contributeAddRentalActivity(): AddRentalActivity

    @ContributesAndroidInjector(modules = [MainModule::class, ContributeFragmentModule::class])
    abstract fun contributeMainActivity(): MainActivity
}