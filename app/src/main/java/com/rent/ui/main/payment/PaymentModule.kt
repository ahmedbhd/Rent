package com.rent.ui.main.payment

import androidx.lifecycle.ViewModel
import com.rent.di.FragmentScope
import com.rent.di.ViewModelKey
import com.rent.di.module.RepositoryModule
import com.rent.di.module.SchedulerModule
import com.rent.ui.main.rental.RentalListAdapter
import com.rent.ui.main.rental.RentalViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap


@Module(includes = [RepositoryModule::class, SchedulerModule::class])
class PaymentModule {

    @Provides
    @IntoMap
    @ViewModelKey(PaymentViewModel::class)
    fun bindViewModel(viewModel: PaymentViewModel): ViewModel = viewModel

    @Provides
    @FragmentScope
    fun provideAdapter(): PaymentListAdapter {
        return PaymentListAdapter()
    }
}