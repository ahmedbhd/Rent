package com.rent.ui.main.rental

import androidx.lifecycle.ViewModel
import com.rent.di.FragmentScope
import com.rent.di.ViewModelKey
import com.rent.di.module.RepositoryModule
import com.rent.di.module.SchedulerModule
import com.rent.ui.main.calendar.CalendarAdapter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(includes = [RepositoryModule::class, SchedulerModule::class])
class RentalModule {

    @Provides
    @IntoMap
    @ViewModelKey(RentalViewModel::class)
    fun bindViewModel(viewModel: RentalViewModel): ViewModel = viewModel

    @Provides
    @FragmentScope
    fun provideAdapter(): RentalListAdapter {
        return RentalListAdapter()
    }

}