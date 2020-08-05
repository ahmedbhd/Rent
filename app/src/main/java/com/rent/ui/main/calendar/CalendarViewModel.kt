package com.rent.ui.main.calendar

import android.app.Application
import androidx.lifecycle.*
import com.rent.R
import com.rent.base.BaseAndroidViewModel
import com.rent.data.model.flight.Flight
import com.rent.data.model.rental.Rental
import com.rent.data.repository.rental.RentalRepository
import com.rent.global.helper.Navigation
import com.rent.global.listener.CalendarItemClickListener
import com.rent.global.listener.SchedulerProvider
import com.rent.global.listener.ToolbarListener
import com.rent.global.utils.DebugLog
import com.rent.global.utils.TAG
import com.rent.global.utils.generateFlights
import com.rent.global.utils.tryCatch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class CalendarViewModel @Inject constructor(
    application: Application,
    schedulerProvider: SchedulerProvider,
    private val rentalRepository: RentalRepository
) : BaseAndroidViewModel(
    application,
    schedulerProvider
), ToolbarListener, CalendarItemClickListener {

    var rentals = MutableLiveData<ArrayList<Rental>>()
    var rentalItems = MutableLiveData<Map<LocalDate, List<Flight>>>()
    var selectDate = MutableLiveData<LocalDate>()
    var calendarItemsList = MediatorLiveData<ArrayList<Flight>>()

    init {
        calendarItemsList.addSource(selectDate) {
            rentalItems.value?.let { flightsList ->
                calendarItemsList.value = ArrayList(flightsList[it].orEmpty())
            }
        }
        loadRentals()
    }

    fun setSelectedDate(date: LocalDate?) {
        selectDate.value = date
    }

    fun loadRentals() {
        showBlockingProgressBar()
        viewModelScope.launch {
            tryCatch({
                val response = withContext(schedulerProvider.dispatchersIO()) {
                    rentalRepository.getRentals()
                }
                onLoadRentalsSuccess(response)
            }, {
                onLoadRentalsFails(it)
            })
        }
    }

    private fun onLoadRentalsFails(throwable: Throwable) {
        hideBlockingProgressBar()
        DebugLog.e(TAG, throwable.toString())
        showToast(applicationContext.getString(R.string.global_operation_failed))
    }

    private fun onLoadRentalsSuccess(response: List<Rental>) {
        hideBlockingProgressBar()
        rentals.value = ArrayList(response)
        rentalItems.value = generateFlights(rentals.value!!)
            .groupBy {
                it.time.toLocalDate()
            }
    }

    override fun onCalendarItemClicked(flight: Flight) {
        navigate(Navigation.RentalDetailActivityNavigation(rentals.value!!.filter { it.idRental == flight.idRental }
            .first()))
    }
}