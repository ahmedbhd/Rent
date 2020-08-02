package com.rent.ui.main.calendar

import androidx.lifecycle.ViewModel
import com.rent.di.FragmentScope
import com.rent.di.ViewModelKey
import com.rent.di.module.RepositoryModule
import com.rent.di.module.SchedulerModule
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap


@Module(includes = [RepositoryModule::class, SchedulerModule::class])
class CalendarModule {

    @Provides
    @IntoMap
    @ViewModelKey(CalendarViewModel::class)
    fun bindViewModel(viewModel: CalendarViewModel): ViewModel = viewModel

    @Provides
    @FragmentScope
    fun provideAdapter(): CalendarAdapter {
        return CalendarAdapter()
    }
}