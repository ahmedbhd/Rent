package com.rent.ui.splash

import androidx.lifecycle.ViewModel
import com.rent.di.ViewModelKey
import com.rent.di.module.RepositoryModule
import com.rent.di.module.SchedulerModule
import com.rent.ui.main.rental.RentalViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [RepositoryModule::class, SchedulerModule::class])
abstract class SplashModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindViewModel(viewModel: SplashViewModel): ViewModel
}