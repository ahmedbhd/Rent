package com.rent.di.module

import android.content.Context
import com.rent.di.ApplicationContext
import com.rent.di.ApplicationScope
import com.rent.global.helper.SharedPreferences
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides

@Module(includes = [ApplicationModule::class, ParsingModule::class])
class PreferencesModule {

    @Provides
    @ApplicationScope
    fun sharedPreferences(@ApplicationContext context: Context, moshi: Moshi): SharedPreferences {
        return SharedPreferences(context, moshi)
    }
}