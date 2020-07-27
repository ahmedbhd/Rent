package com.rent.di.module

import android.app.Application
import android.content.Context
import com.rent.di.ApplicationContext
import com.rent.di.ApplicationScope
import dagger.Module
import dagger.Provides

@Module(includes = [RepositoryModule::class, GlideModule::class, PreferencesModule::class, DatabaseModule::class])
class ApplicationModule {

    @Provides
    @ApplicationScope
    @ApplicationContext
    fun context(application: Application): Context {
        return application.applicationContext
    }
}