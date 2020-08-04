package com.rent.ui.main.payment

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.rent.R
import com.rent.base.BaseAndroidViewModel
import com.rent.data.model.payment.Payment
import com.rent.data.repository.payment.PaymentRepository
import com.rent.global.helper.FetchState
import com.rent.global.helper.dialog.PaymentDialog
import com.rent.global.listener.*
import com.rent.global.utils.tryCatch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PaymentViewModel @Inject constructor(
    application: Application,
    schedulerProvider: SchedulerProvider,
    private val paymentRepository: PaymentRepository
) : BaseAndroidViewModel(
    application,
    schedulerProvider
), ToolbarListener, PaymentDialogListener, PaymentItemClickListener, PaymentItemSwipeListener {

    var paymentDialog = MutableLiveData<PaymentDialog> ()
    private var fetch: MutableLiveData<FetchState> = MutableLiveData()
    var payments = MutableLiveData<ArrayList<Payment>>()

    init {
        loadPayments()
    }


    val isRefreshing: LiveData<Boolean> = fetch.map {
        it == FetchState.Refreshing
    }

    val isEmptyLoading: LiveData<Boolean> = fetch.map {
        it == FetchState.Fetching
    }

    val isEmptyData: LiveData<Boolean> = fetch.map {
        it == FetchState.FetchDone(true)
    }

    val errorText: LiveData<String> = fetch.map {
        if (it is FetchState.FetchError) {
            when (it.throwable) {
                is IOException -> {
                    applicationContext.getString(R.string.global_error_unavailable_network)
                }
                is HttpException -> {
                    when (it.throwable.code()) {
                        //other default handler
                        else -> applicationContext.getString(R.string.global_error_server)
                    }
                }
                else -> {
                    applicationContext.getString(R.string.global_error_server)
                }
            }
        } else {
            ""
        }
    }

    var isError = errorText.map {
        it.isNotEmpty()
    }

    fun onRefresh() {
        fetch.value = FetchState.Refreshing
        loadPayments()
    }

    private fun loadPayments() {
        fetch.value = FetchState.Fetching
        viewModelScope.launch {
            tryCatch({
                val response = withContext(schedulerProvider.dispatchersIO()){
                    paymentRepository.getPayments()
                }
                onLoadPaymentSuccess(response)
            }, {
                onLoadPaymentFails(it)
            })
        }
    }

    private fun onLoadPaymentFails(throwable: Throwable) {
        fetch.value = FetchState.FetchError(throwable)
    }

    private fun onLoadPaymentSuccess(response: List<Payment>) {
        fetch.value = FetchState.FetchDone(response.isEmpty())
        payments.value = ArrayList(response)
    }

    override fun onSavePaymentButtonClicked(payment: Payment) {
        TODO("Not yet implemented")
    }

    override fun onPaymentItemClicked(payment: Payment) {
        TODO("Not yet implemented")
    }

    override fun onPaymentItemSwiped(position:Int) {
        TODO("Not yet implemented")
    }
}