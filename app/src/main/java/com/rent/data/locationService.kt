package com.rent.data

import com.rent.data.model.rental.Rental
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.*

interface LocationServices {


    companion object {
        fun create(): LocationServices {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(GsonConverterFactory.create())
//                    .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("http://xosted.alwaysdata.net/location/")
                .build()

            return retrofit.create(LocationServices::class.java)
        }
    }


    @GET("read.php")
    fun selectLocations(): Observable<List<Rental>>

    @POST("create.php")
    fun ajouterLocation(@Body loc: Rental):Observable<Rental>

    @GET("read.php")
    fun selectLocationById(@Query ("id") id:Int): Observable<Rental>

    @DELETE("delete.php")
    fun deleteLocation(@Query ("id") id:Int): Observable<Model.msgResult>

    @PUT("update.php")
    fun updateLocation(@Body loc: Rental):Observable<Rental>
}