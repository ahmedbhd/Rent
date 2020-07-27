package com.rent.base

import com.rent.data.db.Database
import com.rent.global.helper.SharedPreferences


/**
 * this is the base repository class, all project repositories should extends this class.
 */
abstract class BaseRepository(
    protected val sharedPreferences: SharedPreferences,
    protected val database: Database
)
