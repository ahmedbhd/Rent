package com.rent.data.repository.locataire

import androidx.annotation.WorkerThread
import com.rent.data.model.locataire.Locataire

interface LocataireRepository {

    @WorkerThread
    suspend fun getLocataire(): List<Locataire>

    @WorkerThread
    suspend fun addLocataire(locataire: Locataire): Locataire

    @WorkerThread
    suspend fun getLocataireById(id: Int): Locataire

    @WorkerThread
    suspend fun deleteLocataire(locataire: Locataire)

    @WorkerThread
    suspend fun updateLocataire(locataire: Locataire)
}