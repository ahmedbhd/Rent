package com.rent.data.db

import androidx.room.*
import com.rent.data.model.locataire.Locataire

@Dao
interface LocataireDao {

    @Query("SELECT * from locataire")
    suspend fun getLocataires(): List<Locataire>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLocataire(locataire: Locataire)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLocataire(rental: Locataire)

    @Delete
    suspend fun deleteLocataire(locataire: Locataire)

    @Query("SELECT * from locataire where idLocataire=:id LIMIT 1")
    suspend fun getLocataireById(id: Int): Locataire

    @Query("SELECT * from locataire where idLocataire = (SELECT MAX (idLocataire) from locataire) LIMIT 1")
    suspend fun getLastLocataire(): Locataire
}