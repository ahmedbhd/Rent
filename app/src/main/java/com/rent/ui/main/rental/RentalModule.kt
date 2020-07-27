package com.rent.ui.main.rental

import androidx.lifecycle.ViewModel
import com.rent.di.ViewModelKey
import com.rent.di.module.RepositoryModule
import com.rent.di.module.SchedulerModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [RepositoryModule::class, SchedulerModule::class])
abstract class RentalModule {

    @Binds
    @IntoMap
    @ViewModelKey(RentalViewModel::class)
    abstract fun bindViewModel(viewModel: RentalViewModel): ViewModel
}