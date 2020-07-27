package com.rent.di.module

import com.rent.data.repository.locataire.LocataireRepository
import com.rent.data.repository.locataire.LocataireRepositoryImp
import com.rent.data.repository.payment.PaymentRepository
import com.rent.data.repository.payment.PaymentRepositoryImp
import com.rent.data.repository.rental.RentalRepository
import com.rent.data.repository.rental.RentalRepositoryImp
import dagger.Binds
import dagger.Module


@Module
abstract class RepositoryModule {

    @Binds
    abstract fun provideRentalRepository(repo: RentalRepositoryImp): RentalRepository

    @Binds
    abstract fun providePaymentFragment(repo: PaymentRepositoryImp): PaymentRepository

    @Binds
    abstract fun provideLocataireRepository(repo: LocataireRepositoryImp): LocataireRepository

}