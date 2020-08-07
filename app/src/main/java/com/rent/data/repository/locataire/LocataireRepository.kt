package com.rent.data.repository.locataire

import androidx.annotation.WorkerThread
import com.rent.data.model.locataire.Locataire

interface LocataireRepository {

    @WorkerThread
    suspend fun getLocataires(): List<Locataire>

    @WorkerThread
    suspend fun addLocataire(locataire: Locataire): Locataire

    @WorkerThread
    suspend fun getLocataireById(id: Long): Locataire

    @WorkerThread
    suspend fun deleteLocataire(locataire: Locataire)

    @WorkerThread
    suspend fun updateLocataire(locataire: Locataire)

    @WorkerThread
    suspend fun synchronise(locataires: List<Locataire>)
}