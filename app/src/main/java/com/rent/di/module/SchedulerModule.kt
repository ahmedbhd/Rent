package com.rent.di.module

import com.rent.global.helper.AppSchedulerProvider
import com.rent.global.listener.SchedulerProvider
import dagger.Module
import dagger.Provides

@Module
class SchedulerModule {

    @Provides
    fun provideSchedulerProvider(): SchedulerProvider {
        return AppSchedulerProvider()
    }
}