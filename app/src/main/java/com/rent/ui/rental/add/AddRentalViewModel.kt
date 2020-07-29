package com.rent.ui.rental.add

import android.app.Application
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rent.R
import com.rent.base.BaseAndroidViewModel
import com.rent.data.model.locataire.Locataire
import com.rent.data.model.rental.Rental
import com.rent.data.repository.locataire.LocataireRepository
import com.rent.data.repository.rental.RentalRepository
import com.rent.global.helper.dialog.PhoneDialog
import com.rent.global.listener.PhoneDialogListener
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
), ToolbarListener, AdapterView.OnItemSelectedListener, PhoneDialogListener {

    var newRental: Rental = Rental()
    var newLocataire: Locataire? = null
    var locataires = MutableLiveData<ArrayList<String>>()
    var resultloc: ArrayList<Locataire>? = ArrayList()
    var selectedLocataire: String =
        applicationContext.getString(R.string.add_rental_empty_locataire)
    var phoneDialog = MutableLiveData<PhoneDialog>()
    var cin = MutableLiveData<String>()
    var name = MutableLiveData<String>()
    var startDate = MutableLiveData<String>(applicationContext.getString(R.string.date))
    var endDate = MutableLiveData<String>(applicationContext.getString(R.string.date))
    var addTime = MutableLiveData<String>(applicationContext.getString(R.string._00_00_00))
    var stringTel = ""


    init {
        loadLocataires()
        loadRentals()
    }

    fun showPhoneDialog() {
        phoneDialog.value = PhoneDialog.build(
            dismissPhoneBuild(null)
        )
    }

    private fun dismissPhoneBuild(dismissActionBlock: (() -> Unit)? = null): () -> Unit {
        return {
            dismissActionBlock?.invoke()
            phoneDialog.value = null
        }
    }

    private fun addRental() {
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
        showToast(applicationContext.getString(R.string.global_operation_failed))
    }

    private fun onAddRentalSuccess() {
        hideBlockingProgressBar()
        showToast(applicationContext.getString(R.string.global_add_succeeded))
    }

    private fun addLocataire() {
        showBlockingProgressBar()
        newLocataire = Locataire(0, cin.value!!, name.value!!, stringTel)
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

    private fun loadLocataires() {
        showBlockingProgressBar()
        viewModelScope.launch {
            tryCatch({
                val response = withContext(schedulerProvider.dispatchersIO()) {
                    locataireRepository.selectLocataire()
                }
                onGetLocatairesSuccess(response)
            }, {
                onLoadLocataireFail(it)
            })
        }
    }

    private fun onLoadLocataireFail(throwable: Throwable) {
        hideBlockingProgressBar()
        DebugLog.e(TAG, throwable.toString())
    }

    private fun onGetLocatairesSuccess(response: List<Locataire>) {
        hideBlockingProgressBar()
        resultloc = ArrayList(response)
        val array = ArrayList<String>()
        array.add(" - ")
        response.forEach {
            array.add(it.fullName + applicationContext.getString(R.string.add_rental_empty_locataire) + it.cin)
        }
        locataires.value = array
        DebugLog.e(TAG, "locataires  ${locataires.value}")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        DebugLog.d(TAG, "onNothingSelected")
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        selectedLocataire = parent.getItemAtPosition(position) as String
        if (position > 0)
            newRental.locataire = resultloc!![position - 1]
        else
            newRental.locataire = resultloc!![position]
    }

    override fun onSaveClicked(tel: String) {
        stringTel = tel
    }

    fun onAddRentalClicked() {
        newRental.dateDebut =
            startDate.value + " " + addTime.value + ":00"
        newRental.dateFin =
            endDate.value + " " + addTime.value + ":00"
        checkInputs()
    }

    private fun checkInputs() {
        if (!cin.value.isNullOrEmpty() && !name.value.isNullOrEmpty() && startDate.value != applicationContext.getString(
                R.string.date
            )
        ) {
            println("here2")

            addLocataire()
            return
        }

        if ((selectedLocataire != applicationContext.getString(R.string.add_rental_empty_locataire)) && startDate.value != applicationContext.getString(
                R.string.date
            )
        ) {
            addRental()

            return
        }

        showToast(applicationContext.getString(R.string.add_rental_incorrect_information))
    }

    private fun loadRentals() {
        showBlockingProgressBar()
        viewModelScope.launch {
            tryCatch({
                val response = withContext(schedulerProvider.dispatchersIO()) {
                    rentalRepository.selectRental()
                }
                onGetRentalsSuccess(response)
            }, {
                hideBlockingProgressBar()
            })
        }
    }

    private fun onGetRentalsSuccess(response: List<Rental>) {
        hideBlockingProgressBar()
        DebugLog.e(TAG, "rentals  $response")
    }
}