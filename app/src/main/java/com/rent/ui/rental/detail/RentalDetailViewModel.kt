package com.rent.ui.rental.detail

import android.app.Application
import com.rent.base.BaseAndroidViewModel
import com.rent.global.listener.SchedulerProvider
import com.rent.global.listener.ToolbarListener
import javax.inject.Inject

class RentalDetailViewModel @Inject constructor(
    application: Application,
    schedulerProvider: SchedulerProvider
) : BaseAndroidViewModel(
    application,
    schedulerProvider
), ToolbarListener {
}