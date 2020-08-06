package com.rent.ui.rental.detail

import androidx.lifecycle.ViewModel
import com.rent.data.model.relations.RentalWithLocataire
import com.rent.di.ViewModelKey
import com.rent.di.module.RepositoryModule
import com.rent.di.module.SchedulerModule
import com.rent.global.utils.ExtraKeys
import com.rent.ui.main.payment.PaymentListAdapter
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Named


@Module(includes = [RepositoryModule::class, SchedulerModule::class])
class RentalDetailModule {

    @Provides
    @IntoMap
    @ViewModelKey(RentalDetailViewModel::class)
    fun bindViewModel(viewModel: RentalDetailViewModel): ViewModel = viewModel

    @Provides
    @Named(ExtraKeys.RentalDetailActivity.RENAL_DETAIL_INJECT_RENTAL)
    fun provideExtraRental(rentalDetailActivity: RentalDetailActivity): RentalWithLocataire {
        return rentalDetailActivity.intent.getParcelableExtra(ExtraKeys.RentalDetailActivity.RENAL_DETAIL_EXTRA_RENTAL)!!
    }

    @Provides
    fun provideAdapter(): PaymentListAdapter {
        return PaymentListAdapter()
    }
}