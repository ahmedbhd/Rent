package com.rent.di.module

import android.content.Context
import com.rent.data.db.Database
import com.rent.data.db.DatabaseBuilder
import com.rent.di.ApplicationContext
import com.rent.di.ApplicationScope
import dagger.Module
import dagger.Provides

@Module(includes = [ApplicationModule::class])
class DatabaseModule {

    @Provides
    @ApplicationScope
    fun databaseProvider(@ApplicationContext context: Context): Database {
        return DatabaseBuilder.getRentDatabase(context)
    }
}