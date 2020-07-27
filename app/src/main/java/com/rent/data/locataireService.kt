package com.rent.data

import com.rent.data.model.locataire.Locataire
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface LocataireServices {


    companion object {
        fun create(): LocataireServices {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://xosted.alwaysdata.net/locataire/")
                .build()

            return retrofit.create(LocataireServices::class.java)
        }
    }


    @GET ("read.php")
    fun selectLocataires(): Observable<List<Locataire>>

    @POST("create.php")
    fun addLocataire(@Body loc : Locataire):Observable<Locataire>

    @PUT("update.php")
    fun updateLocataire(@Body loc: Locataire):Observable<Locataire>
}