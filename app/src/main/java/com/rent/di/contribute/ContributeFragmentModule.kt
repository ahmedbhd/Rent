package com.rent.di.contribute


import com.rent.di.FragmentScope
import com.rent.ui.main.calendar.CalendarFragment
import com.rent.ui.main.calendar.CalendarModule
import com.rent.ui.main.payment.PaymentFragment
import com.rent.ui.main.payment.PaymentModule
import com.rent.ui.main.rental.RentalFragment
import com.rent.ui.main.rental.RentalModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ContributeFragmentModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [CalendarModule::class])
    abstract fun contributeCalendarFragment(): CalendarFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [RentalModule::class])
    abstract fun contributeRentalFragment(): RentalFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [PaymentModule::class])
    abstract fun contributePaymentFragment(): PaymentFragment
}