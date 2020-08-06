package com.rent.ui.rental.detail

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rent.R
import com.rent.base.BaseAndroidViewModel
import com.rent.data.model.payment.Payment
import com.rent.data.model.relations.LocataireWithPayment
import com.rent.data.model.relations.RentalWithLocataire
import com.rent.data.repository.locataire.LocataireRepository
import com.rent.data.repository.payment.PaymentRepository
import com.rent.data.repository.rental.RentalRepository
import com.rent.global.helper.Navigation
import com.rent.global.helper.dialog.PaymentDialog
import com.rent.global.helper.dialog.PhoneDialog
import com.rent.global.listener.*
import com.rent.global.utils.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Named


class RentalDetailViewModel @Inject constructor(
    application: Application,
    schedulerProvider: SchedulerProvider,
    private val rentalRepository: RentalRepository,
    private val paymentRepository: PaymentRepository,
    private val locataireRepository: LocataireRepository,
    @Named(ExtraKeys.RentalDetailActivity.RENAL_DETAIL_INJECT_RENTAL) var rentalWithLocataire: RentalWithLocataire
) : BaseAndroidViewModel(
    application,
    schedulerProvider
), ToolbarListener, PaymentDialogListener, PhoneDialogListener, CalendarBottomSheetListener,
    PaymentItemSwipeListener, PaymentItemClickListener {


    var sdate: Date = rentalWithLocataire.rental.dateDebut
    var edate: Date = rentalWithLocataire.rental.dateFin


    var payments = MutableLiveData<ArrayList<LocataireWithPayment>>()
    var paymentDialog = MutableLiveData<PaymentDialog>()
    var phoneDialog = MutableLiveData<PhoneDialog>()


    var rentalColor = MutableLiveData(Color.parseColor(rentalWithLocataire.rental.color))

    var cin = MutableLiveData(rentalWithLocataire.locataire.cin)
    var name = MutableLiveData(rentalWithLocataire.locataire.fullName)
    var startDate = MutableLiveData(rentalWithLocataire.rental.dateDebut.toString())
    var endDate = MutableLiveData(rentalWithLocataire.rental.dateFin.toString())
    var time = MutableLiveData("")


    init {
//        loadLocataire()
        prepareDates()
    }

//    private fun loadLocataire() {
//        showBlockingProgressBar()
//        viewModelScope.launch {
//            tryCatch({
//                val response = withContext(schedulerProvider.dispatchersIO()) {
//                    locataireRepository.getLocataireById(rentalWithLocataire.locataireOwnerId)
//                }
//                onLoadLocataireSuccess(response)
//            }, {
//                onLoadLocataireFails(it)
//            })
//        }
//    }

//    private fun onLoadLocataireFails(it: Throwable) {
//        onOperationFails(it)
//        navigate(Navigation.Back)
//    }
//
//    private fun onLoadLocataireSuccess(response: Locataire) {
//        hideBlockingProgressBar()
//        locataire.value = response
//    }

    private fun prepareDates() {
        val c = Calendar.getInstance()
        c.time = sdate

        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMinute = c.get(Calendar.MINUTE)
        var mYear = c.get(Calendar.YEAR)
        var mMonth = c.get(Calendar.MONTH)
        var mDay = c.get(Calendar.DAY_OF_MONTH)
        startDate.value = mYear.toString() + "-" + (mMonth + 1) + "-" + (mDay)

        c.time = edate


        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
        endDate.value = mYear.toString() + "-" + (mMonth + 1) + "-" + (mDay)

        time.value = "$mHour:$mMinute"
    }

    fun deleteRental() {
        showBlockingProgressBar()
        viewModelScope.launch {
            tryCatch({
                withContext(schedulerProvider.dispatchersIO()) {
                    rentalRepository.deleteRental(rentalWithLocataire.rental)
                }
                onDeleteSuccess()
            }, {
                onOperationFails(it)
            })
        }
    }

    private fun onDeleteSuccess() {
        hideBlockingProgressBar()
        showToast("Suppression avec succée")
        navigate(Navigation.Back)
    }

    fun showPaymentDialog() {
        paymentDialog.value = PaymentDialog.build(
            Payment(0, "", 0, "", rentalWithLocataire.rental.idRental),
            this,
            dismissPaymentDialog()
        )
    }

    private fun dismissPaymentDialog(): () -> Unit {
        return {
            paymentDialog.value = null
        }
    }

    override fun onSavePaymentButtonClicked(payment: Payment) {
        showBlockingProgressBar()
        viewModelScope.launch {
            tryCatch({
                withContext(schedulerProvider.dispatchersIO()) {
                    if (payment.idPayment == 0L) {
                        paymentRepository.addPayment(payment)
                    } else {
                        paymentRepository.updatePayment(payment)
                    }
                }
                onAddPaymentSuccess()
            }, {
                onOperationFails(it)
            })
        }
    }

    private fun onAddPaymentSuccess() {
        hideBlockingProgressBar()
        showToast("Paiement avec succée")
    }

    override fun onSaveClicked(tel: String) {
        rentalWithLocataire.locataire.numTel = tel
    }

    fun setRentalColor(color: Int) {
        rentalColor.value = color
    }

    fun showPhoneDialog() {
        phoneDialog.value = PhoneDialog.build(
            rentalWithLocataire.locataire.numTel,
            dismissPhoneBuild()
        )
    }

    private fun dismissPhoneBuild(): () -> Unit {
        return {
            phoneDialog.value = null
        }
    }

    override fun onSaveClicked(dateRange: Pair<String, String>) {
        startDate.value = dateRange.first
        endDate.value = dateRange.second
    }

    fun updateRental() {
        showBlockingProgressBar()
        rentalWithLocataire.rental.dateFin =
            formatDateTime.parse(endDate.value.toString() + " " + time.value.toString() + ":00")!!
        rentalWithLocataire.rental.dateDebut =
            formatDateTime.parse(startDate.value.toString() + " " + time.value.toString() + ":00")!!
        rentalWithLocataire.rental.color =
            String.format("#%06X", 0xFFFFFF and (rentalColor.value ?: R.color.colorPrimary))

        viewModelScope.launch {
            tryCatch({
                withContext(schedulerProvider.dispatchersIO()) {
                    rentalRepository.updateRental(rentalWithLocataire.rental)
                }
                onUpdateRentalSuccess()
            }, {
                onOperationFails(it)
            })
        }
    }

    private fun onOperationFails(throwable: Throwable) {
        hideBlockingProgressBar()
        DebugLog.e(TAG, throwable.toString())
        showToast(applicationContext.getString(R.string.global_operation_failed))
    }

    private fun onUpdateRentalSuccess() {
        hideBlockingProgressBar()
        showToast("Mise à jour succée")
        navigate(Navigation.Back)
    }

    fun updateLocataire() {
        showBlockingProgressBar()
        rentalWithLocataire.locataire.cin = cin.value!!
        rentalWithLocataire.locataire.fullName = name.value!!
        viewModelScope.launch {
            tryCatch({
                withContext(schedulerProvider.dispatchersIO()) {
                    locataireRepository.updateLocataire(rentalWithLocataire.locataire)
                }
                onUpdateLocataireSuccess()
            }, {
                onOperationFails(it)
            })
        }
    }

    private fun onUpdateLocataireSuccess() {
        hideBlockingProgressBar()
        showToast("Mise à jour succée")
    }

    fun getPaymentByRentalId() {
        showBlockingProgressBar()
        viewModelScope.launch {
            tryCatch({
                val response = withContext(schedulerProvider.dispatchersIO()) {
                    paymentRepository.getPaymentByRentalId(rentalWithLocataire.rental.idRental)
                }
                onGetPaymentByRentalIdSuccess(response)
            }, {
                onOperationFails(it)
            })
        }
    }

    private fun onGetPaymentByRentalIdSuccess(response: ArrayList<LocataireWithPayment>) {
        hideBlockingProgressBar()
        payments.value = response.also {
            if (it.isEmpty()) showToast("Pas de données à afficher")
        }
    }

    override fun onPaymentItemSwiped(locataireWithPayment: LocataireWithPayment, position: Int) {
        showBlockingProgressBar()
        viewModelScope.launch {
            tryCatch({
                withContext(schedulerProvider.dispatchersIO()) {
                    paymentRepository.deletePayment(locataireWithPayment.payment)
                }
                onDeletePaymentSuccess(locataireWithPayment, position)
            }, {
                onOperationFails(it)
            })
        }
    }

    private fun onDeletePaymentSuccess(locataireWithPayment: LocataireWithPayment, position: Int) {
        hideBlockingProgressBar()
        payments.value = payments.value?.apply {
            removeAt(position)
        }
        showSnackBarMessage("Paiment Supprimé") {
            addPayment(locataireWithPayment, position)
        }
    }

    private fun addPayment(locataireWithPayment: LocataireWithPayment, position: Int) {
        viewModelScope.launch {
            tryCatch({
                withContext(schedulerProvider.dispatchersIO()) {
                    paymentRepository.addPayment(locataireWithPayment.payment)
                }
                onReAddPaymentSuccess(locataireWithPayment, position)
            }, {
                onOperationFails(it)
            })
        }
    }

    private fun onReAddPaymentSuccess(locataireWithPayment: LocataireWithPayment, position: Int) {
        payments.value = payments.value?.apply {
            add(position, locataireWithPayment)
        }
    }

    override fun onPaymentItemClicked(payment: Payment) {
        paymentDialog.value = PaymentDialog.build(
            payment,
            this,
            dismissPaymentDialog()
        )
    }
}