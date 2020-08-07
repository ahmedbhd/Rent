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
import com.rent.global.helper.Navigation
import com.rent.global.helper.dialog.PhoneDialog
import com.rent.global.listener.PhoneDialogListener
import com.rent.global.listener.SchedulerProvider
import com.rent.global.listener.ToolbarListener
import com.rent.global.utils.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class AddRentalViewModel @Inject constructor(
    application: Application,
    schedulerProvider: SchedulerProvider,
    private val rentalRepository: RentalRepository,
    private val locataireRepository: LocataireRepository
) : BaseAndroidViewModel(
    application,
    schedulerProvider
), ToolbarListener, AdapterView.OnItemSelectedListener, PhoneDialogListener {

    var locataires = MutableLiveData<ArrayList<String>>()
    var resultRentals: ArrayList<Locataire>? = ArrayList()
    var phoneDialog = MutableLiveData<PhoneDialog>()
    var cin = MutableLiveData<String>()
    var name = MutableLiveData<String>()
    var startDate = MutableLiveData<String>(formatDate.format(Date()))
    var endDate = MutableLiveData<String>(formatDate.format(Date()))
    var addTime = MutableLiveData<String>(applicationContext.getString(R.string._00_00_00))
    var house = MutableLiveData<String>()

    var cinError = MutableLiveData(false)
    var nameError = MutableLiveData(false)
    var dateError = MutableLiveData(false)


    var newRental: Rental = Rental()
    private var newLocataire: Locataire? = null
    private var selectedLocataire: String =
        applicationContext.getString(R.string.add_rental_empty_locataire)
    private var stringTel = ""
    private var rentals = ArrayList<Rental>()


    init {
        loadLocatairesAndRentals()
    }

    fun showPhoneDialog() {
        phoneDialog.value = PhoneDialog.build(
            stringTel,
            dismissPhoneBuild()
        )
    }

    private fun dismissPhoneBuild(): () -> Unit {
        return {
            phoneDialog.value = null
        }
    }

    private fun checkAvailability(): Boolean {
        return rentals.none { row ->
            (((newRental.dateDebut.time >= row.dateDebut.time &&
                    newRental.dateDebut.time <= row.dateFin.time) ||
                    (newRental.dateFin.time >= row.dateDebut.time &&
                            newRental.dateFin.time <= row.dateFin.time)
                    ) && (row.house == newRental.house))
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
        showToast(R.string.global_operation_failed)
    }

    private fun onAddRentalSuccess() {
        hideBlockingProgressBar()
        showToast(R.string.global_add_succeeded)
        navigate(Navigation.Back)
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
                onAddRentalFail(it)
            })
        }
    }

    private fun onAddLocataireSuccess(locataire: Locataire) {
        hideBlockingProgressBar()
        newRental.locataireOwnerId = locataire.idLocataire
        addRental()
    }

    private fun loadLocatairesAndRentals() {
        showBlockingProgressBar()
        viewModelScope.launch {
            tryCatch({
                val locataires = withContext(schedulerProvider.dispatchersIO()) {
                    locataireRepository.getLocataires()
                }
                val rentals = withContext(schedulerProvider.dispatchersIO()) {
                    rentalRepository.getRentals()
                }
                onGetLocatairesSuccess(locataires, rentals)
            }, {
                onLoadLocataireFail(it)
            })
        }
    }

    private fun onLoadLocataireFail(throwable: Throwable) {
        hideBlockingProgressBar()
        DebugLog.e(TAG, throwable.toString())
    }

    private fun onGetLocatairesSuccess(response: List<Locataire>, rentalsResponse: List<Rental>) {
        hideBlockingProgressBar()
        rentals = ArrayList(rentalsResponse)
        resultRentals = ArrayList(response)
        val array = ArrayList<String>()
//        array.ic_add(applicationContext.getString(R.string.add_rental_empty_locataire))
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
        when (parent.id) {
            R.id.spinnerIRentalHouse -> {
                house.value = parent.adapter?.getItem(position)?.toString()
            }
            R.id.spinnerusers -> {
                selectedLocataire = parent.getItemAtPosition(position) as String
                if (position > 0)
                    newRental.locataireOwnerId = resultRentals!![position - 1].idLocataire
                else
                    newRental.locataireOwnerId = resultRentals!![position].idLocataire
            }
        }
    }

    override fun onSaveClicked(tel: String) {
        var correctTel = tel
        while (correctTel.endsWith(',')) {
            correctTel = correctTel.removeSuffix(",")
        }
        stringTel = correctTel
    }

    fun onAddRentalClicked() {
        newRental.house = house.value!!
        newRental.dateDebut =
            formatDateTime.parse(startDate.value + " " + addTime.value + SECONDS)!!
        newRental.dateFin = formatDateTime.parse(endDate.value + " " + addTime.value + SECONDS)!!
        checkInputs()
    }

    private fun checkInputs() {
        if (checkAvailability()) {
            if (validateCin() and
                validateName() and
                validateTel() and
                validateDate()
            ) {
                addLocataire()
                return
            } else if (validateLocataire() and validateDate()) {
                addRental()
                return
            }
        } else {
            showToast(R.string.global_wrong_rental_details)
        }
    }

    private fun validateCin(): Boolean {
        return if (cin.value.isNullOrEmpty()) {
            cinError.value = true
            false
        } else {
            cinError.value = false
            true
        }
    }

    private fun validateName(): Boolean {
        return if (name.value.isNullOrEmpty()) {
            nameError.value = true
            false
        } else {
            nameError.value = false
            true
        }
    }

    private fun validateDate(): Boolean {
        return if (newRental.dateFin.after(newRental.dateDebut)) {
            dateError.value = false
            true
        } else {
            dateError.value = true
            false
        }
    }

    private fun validateTel(): Boolean {
        return if (stringTel.split(",")
                .any { it.isValidPhoneNumber() }
        ) {
            true
        } else {
            showToast(R.string.global_wrong_phone)
            false
        }
    }

    private fun validateLocataire(): Boolean {
        return if (selectedLocataire != applicationContext.getString(R.string.add_rental_empty_locataire)) {
            true
        } else {
            showToast(R.string.global_wrong_locataire)
            false
        }
    }

    override fun onStartActionClick() {
        navigate(Navigation.Back)
    }
}