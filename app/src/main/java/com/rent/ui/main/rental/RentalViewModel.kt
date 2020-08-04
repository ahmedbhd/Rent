package com.rent.ui.main.rental

import android.app.Application
import android.widget.Filter
import android.widget.Filterable
import android.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.rent.R
import com.rent.base.BaseAndroidViewModel
import com.rent.data.model.rental.Rental
import com.rent.data.repository.rental.RentalRepository
import com.rent.global.helper.FetchState
import com.rent.global.helper.Navigation
import com.rent.global.helper.dialog.CallDialog
import com.rent.global.listener.*
import com.rent.global.utils.tryCatch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class RentalViewModel @Inject constructor(
    application: Application,
    schedulerProvider: SchedulerProvider,
    private val rentalRepository: RentalRepository
) : BaseAndroidViewModel(
    application,
    schedulerProvider
), ToolbarListener, RentalItemClickListener, SearchView.OnQueryTextListener,
    RentalItemSwipeListener, DialogCustomCallListener, Filterable {

    var locations = MutableLiveData<ArrayList<Rental>>()
    private var backUpLocations: ArrayList<Rental>? = ArrayList()
    var callDialog = MutableLiveData<CallDialog>()
    var telToBeCalled = MutableLiveData<String>()
    private var fetch: MutableLiveData<FetchState> = MutableLiveData()

    init {
        loadRentals()
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
        loadRentals()
    }


    private fun loadRentals() {
        fetch.value = FetchState.Fetching
        viewModelScope.launch {
            tryCatch({
                val response = withContext(schedulerProvider.dispatchersIO()) {
                    rentalRepository.getRentals()
                }
                onLoadRentalsSuccess(response)
            }, {
                onLoadDataFails(it)
            })
        }
    }

    private fun onLoadDataFails(throwable: Throwable) {
        fetch.value = FetchState.FetchError(throwable)
    }

    private fun onLoadRentalsSuccess(response: List<Rental>) {
        fetch.value = FetchState.FetchDone(response.isEmpty())
        locations.value = ArrayList(response).also {
            backUpLocations = it
        }
    }

    override fun onRentalItemClicked(rental: Rental) {
        navigate(Navigation.RentalDetailActivityNavigation(rental))
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if (newText.isNotEmpty()) {
            println(newText)
            locations.value = backUpLocations!!.filter { row ->
                row.locataire.fullName.toLowerCase(Locale.getDefault())
                    .contains(newText.toLowerCase(Locale.getDefault())) || row.locataire.numTel.contains(
                    newText
                )
            } as ArrayList<Rental>
            println(locations)
        } else {
            locations.value = backUpLocations
        }
        return false
    }

    override fun onRentalItemSwiped(stringTel: String) {
        callDialog.value = CallDialog.build(
            stringTel,
            this,
            dismissCallDialog()
        )
    }

    private fun dismissCallDialog(dismissActionBlock: (() -> Unit)? = null): () -> Unit {
        return {
            dismissActionBlock?.invoke()
            callDialog.value = null
        }
    }

    override fun onCallClicked(tel: String) {
        telToBeCalled.value = tel
    }

    fun setTelNull() {
        telToBeCalled.value = null
    }


    override fun getFilter(): Filter {
        var locationFilteredList: MutableList<Rental>
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    locationFilteredList = locations.value!!
                } else {
                    val filteredList: MutableList<Rental> = ArrayList()
                    for (row in locations.value!!) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.locataire.fullName.toLowerCase(Locale.getDefault()).contains(
                                charString.toLowerCase(
                                    Locale.getDefault()
                                )
                            ) || row.locataire.numTel.contains(
                                charSequence
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }

                    locationFilteredList = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = locationFilteredList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                locationFilteredList = filterResults.values as MutableList<Rental>

                // refresh the list with filtered data
                locations.value = ArrayList(locationFilteredList)
            }
        }
    }
}