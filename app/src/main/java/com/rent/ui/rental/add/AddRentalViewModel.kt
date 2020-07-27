package com.rent.ui.rental.add

import android.app.Application
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rent.base.BaseAndroidViewModel
import com.rent.data.model.locataire.Locataire
import com.rent.data.model.rental.Rental
import com.rent.data.repository.locataire.LocataireRepository
import com.rent.data.repository.rental.RentalRepository
import com.rent.global.listener.SchedulerProvider
import com.rent.global.listener.ToolbarListener
import com.rent.global.utils.DebugLog
import com.rent.global.utils.TAG
import com.rent.global.utils.tryCatch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddRentalViewModel @Inject constructor(
    application: Application,
    schedulerProvider: SchedulerProvider,
    private val rentalRepository: RentalRepository,
    private val locataireRepository: LocataireRepository
) : BaseAndroidViewModel(
    application,
    schedulerProvider
), ToolbarListener, AdapterView.OnItemSelectedListener {

    var newRental: Rental = Rental()
    var newLocataire: Locataire?= null
    var locataires = MutableLiveData<ArrayList<String>>()
    var resultloc: ArrayList<Locataire>? = ArrayList()
    var selectedLocatair: String = "-"

    init {
        locataires.value?.add("-")
        loadLocataires()
    }

    fun addRental() {
        showBlockingProgressBar()
        viewModelScope.launch {
            tryCatch({
                withContext(schedulerProvider.dispatchersIO()) {
                    rentalRepository.addRental(newRental)
                }
                onAddRentalSuccess()
            }, {
                onAddRentalFail(it)
            })
        }
    }

    private fun onAddRentalFail(throwable: Throwable) {
        hideBlockingProgressBar()
        DebugLog.e(TAG, throwable.toString())
        showToast("Opération échouée!")
    }

    private fun onAddRentalSuccess() {
        hideBlockingProgressBar()
        showToast("Ajout avec succée")
    }

    fun addLocataire() {
        showBlockingProgressBar()
        viewModelScope.launch {
            tryCatch({
                val response = withContext(schedulerProvider.dispatchersIO()) {
                    locataireRepository.addLocataire(newLocataire!!)
                }
                onAddLocataireSuccess(response)
            }, {

            })
        }
    }

    private fun onAddLocataireSuccess(locataire: Locataire) {
        hideBlockingProgressBar()
        newRental.locataire = locataire
        addRental()
    }

    fun loadLocataires() {
        showBlockingProgressBar()
        viewModelScope.launch {
            tryCatch( {
                val response = withContext(schedulerProvider.dispatchersIO()) {
                    locataireRepository.selectLocataire()
                }
                onGetLocatairesSuccess(response)
            }, {

            })
        }
    }

    private fun onGetLocatairesSuccess(response: List<Locataire>) {
        hideBlockingProgressBar()
        resultloc = ArrayList(response)
        response.forEach {
            locataires.value?.add(it.fullName + " - " + it.cin)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        selectedLocatair = parent.getItemAtPosition(position) as String
        if (position > 0)
            newRental.locataire = resultloc!![position - 1]
        else
            newRental.locataire = resultloc!![position]
    }
}