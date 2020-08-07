package com.rent.data.repository.locataire

import com.rent.base.BaseRepository
import com.rent.data.db.Database
import com.rent.data.model.locataire.Locataire
import com.rent.global.helper.SharedPreferences
import javax.inject.Inject


class LocataireRepositoryImp
@Inject constructor(
    sharedPreferences: SharedPreferences,
    database: Database
) : BaseRepository(sharedPreferences, database), LocataireRepository {
    override suspend fun getLocataires() = database.locataireDao().getLocataires()

    override suspend fun addLocataire(locataire: Locataire): Locataire {
        database.locataireDao().addLocataire(locataire)
        return database.locataireDao().getLastLocataire()
    }

    override suspend fun getLocataireById(id: Long) = database.locataireDao().getLocataireById(id)

    override suspend fun deleteLocataire(locataire: Locataire) =
        database.locataireDao().deleteLocataire(locataire)

    override suspend fun updateLocataire(locataire: Locataire) =
        database.locataireDao().updateLocataire(locataire)

    override suspend fun synchronise(locataires: List<Locataire>) {
        database.locataireDao().deleteAllLocataire()
        database.locataireDao().addAllLocataire(*locataires.toTypedArray())
    }
}