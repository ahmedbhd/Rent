package com.rent.data.db

import android.content.Context
import androidx.room.Room

object DatabaseBuilder {
    fun getRentDatabase (context: Context) : Database {
        return Room.databaseBuilder(context, Database::class.java, DB_NAME)
            .build()
    }
}
const val DB_NAME = "db_rent"
