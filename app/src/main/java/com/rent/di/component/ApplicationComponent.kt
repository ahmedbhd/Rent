package com.rent.di.component

import android.app.Application
import com.rent.di.ApplicationScope
import com.rent.di.contribute.ContributeActivityModule
import com.rent.di.module.ApplicationModule
import com.rent.RentApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule

@ApplicationScope
@Component(modules = [AndroidSupportInjectionModule::class, ApplicationModule::class, ContributeActivityModule::class])
interface ApplicationComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }

    fun inject(app: RentApplication)
}