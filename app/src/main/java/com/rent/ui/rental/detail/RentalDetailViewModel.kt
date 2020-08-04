package com.rent.ui.rental.detail

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rent.R
import com.rent.base.BaseAndroidViewModel
import com.rent.data.model.payment.Payment
import com.rent.data.model.rental.Rental
import com.rent.data.repository.locataire.LocataireRepository
import com.rent.data.repository.payment.PaymentRepository
import com.rent.data.repository.rental.RentalRepository
import com.rent.global.helper.Navigation
import com.rent.global.helper.dialog.PaymentDialog
import com.rent.global.helper.dialog.PhoneDialog
import com.rent.global.listener.*
import com.rent.global.utils.DebugLog
import com.rent.global.utils.ExtraKeys
import com.rent.global.utils.TAG
import com.rent.global.utils.tryCatch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.collections.ArrayList


class RentalDetailViewModel @Inject constructor(
    application: Application,
    schedulerProvider: SchedulerProvider,
    private val rentalRepository: RentalRepository,
    private val paymentRepository: PaymentRepository,
    private val locataireRepository: LocataireRepository,
    @Named(ExtraKeys.RentalDetailActivity.RENAL_DETAIL_INJECT_RENTAL) var oldRental: Rental
) : BaseAndroidViewModel(
    application,
    schedulerProvider
), ToolbarListener, PaymentDialogListener, PhoneDialogListener, CalendarBottomSheetListener,
    PaymentItemSwipeListener, PaymentItemClickListener {

    val formatDate = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault())

    var sdate: Date = formatDate.parseObject(oldRental.dateDebut) as Date
    var edate: Date = formatDate.parseObject(oldRental.dateFin) as Date


    var payments = MutableLiveData<ArrayList<Payment>>()
    var paymentDialog = MutableLiveData<PaymentDialog>()
    var phoneDialog = MutableLiveData<PhoneDialog>()


    var rentalColor = MutableLiveData(Color.parseColor(oldRental.color))
    var stringTel = ""

    var cin = MutableLiveData(oldRental.locataire.cin)
    var name = MutableLiveData(oldRental.locataire.fullName)
    var startDate = MutableLiveData(oldRental.dateDebut)
    var endDate = MutableLiveData(oldRental.dateFin)
    var time = MutableLiveData("")


    init {
        prepareDates()
    }

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
                    rentalRepository.deleteRental(oldRental)
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
            Payment(0, "", 0, "", oldRental),
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
                    if (payment.idPayment == 0) {
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
        stringTel = tel
    }

    fun setRentalColor(color: Int) {
        rentalColor.value = color
    }

    fun showPhoneDialog() {
        phoneDialog.value = PhoneDialog.build(
            oldRental.locataire.numTel,
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
        oldRental.dateFin =
            endDate.value.toString() + " " + time.value.toString()
        oldRental.dateDebut =
            startDate.value.toString() + " " + endDate.value.toString()
        oldRental.color =
            String.format("#%06X", 0xFFFFFF and (rentalColor.value ?: R.color.colorPrimary))

        viewModelScope.launch {
            tryCatch({
                withContext(schedulerProvider.dispatchersIO()) {
                    rentalRepository.updateRental(oldRental)
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
        oldRental.locataire.cin = cin.value!!
        oldRental.locataire.fullName = name.value!!
        viewModelScope.launch {
            tryCatch({
                withContext(schedulerProvider.dispatchersIO()) {
                    locataireRepository.updateLocataire(oldRental.locataire)
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
                    paymentRepository.getPaymentByRentalId(oldRental.idRental)
                }
                onGetPaymentByRentalIdSuccess(response)
            }, {
                onOperationFails(it)
            })
        }
    }

    private fun onGetPaymentByRentalIdSuccess(response: List<Payment>) {
        hideBlockingProgressBar()
        payments.value = ArrayList(response)
    }

    override fun onPaymentItemSwiped(position: Int) {
        showBlockingProgressBar()
        viewModelScope.launch {
            tryCatch({
                withContext(schedulerProvider.dispatchersIO()) {
                    payments.value?.get(position)?.let { paymentRepository.deletePayment(it) }
                }
                onDeletePaymentSuccess()
            }, {
                onOperationFails(it)
            })
        }
    }

    private fun onDeletePaymentSuccess() {
        hideBlockingProgressBar()
    }

    override fun onPaymentItemClicked(payment: Payment) {
        paymentDialog.value = PaymentDialog.build(
            payment,
            this,
            dismissPaymentDialog()
        )
    }
}