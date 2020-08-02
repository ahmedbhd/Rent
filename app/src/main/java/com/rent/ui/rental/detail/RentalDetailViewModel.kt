package com.rent.ui.rental.detail

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.rent.base.BaseAndroidViewModel
import com.rent.data.model.rental.Rental
import com.rent.data.repository.payment.PaymentRepository
import com.rent.data.repository.rental.RentalRepository
import com.rent.global.listener.SchedulerProvider
import com.rent.global.listener.ToolbarListener
import com.rent.global.utils.ExtraKeys
import com.rent.global.utils.tryCatch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named


class RentalDetailViewModel @Inject constructor(
    application: Application,
    schedulerProvider: SchedulerProvider,
    private val rentalRepository: RentalRepository,
    private val paymentRepository: PaymentRepository,
    @Named(ExtraKeys.RentalDetailActivity.RENAL_DETAIL_INJECT_RENTAL) var oldRental: Rental
) : BaseAndroidViewModel(
    application,
    schedulerProvider
), ToolbarListener {

    fun deleteRental() {
        showBlockingProgressBar()
        viewModelScope.launch {
            tryCatch({
                withContext(schedulerProvider.dispatchersIO()) {
                    rentalRepository.deleteRental(oldRental)
                }
            }, {

            })
        }
    }
}