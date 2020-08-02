package com.rent.di.contribute


import com.rent.di.FragmentScope
import com.rent.ui.main.calendar.CalendarFragment
import com.rent.ui.main.calendar.CalendarModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ContributeFragmentModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [CalendarModule::class])
    abstract fun contributeCounterFragment(): CalendarFragment

}