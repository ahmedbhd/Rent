package com.rent.ui.main.home

import android.app.Application
import com.rent.base.BaseAndroidViewModel
import com.rent.global.listener.SchedulerProvider
import com.rent.global.listener.ToolbarListener
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    application: Application,
    schedulerProvider: SchedulerProvider
) : BaseAndroidViewModel(
    application,
    schedulerProvider
), ToolbarListener {
}