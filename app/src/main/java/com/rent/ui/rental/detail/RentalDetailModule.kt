package com.rent.ui.rental.detail

import androidx.lifecycle.ViewModel
import com.rent.di.ViewModelKey
import com.rent.di.module.RepositoryModule
import com.rent.di.module.SchedulerModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module(includes = [RepositoryModule::class, SchedulerModule::class])
abstract class RentalDetailModule {
    @Binds
    @IntoMap
    @ViewModelKey(RentalDetailViewModel::class)
    abstract fun bindViewModel(viewModel: RentalDetailViewModel): ViewModel
}