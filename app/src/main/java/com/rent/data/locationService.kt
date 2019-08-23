package com.rent.data

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
    fun selectLocations(): Observable<List<Model.location>>

    @POST("create.php")
    fun ajouterLocation(@Body loc: Model.location):Observable<Model.location>

    @GET("read.php")
    fun selectLocationById(@Query ("id") id:Int): Observable<Model.location>

    @DELETE("delete.php")
    fun deleteLocation(@Query ("id") id:Int): Observable<Model.msgResult>

    @PUT("update.php")
    fun updateLocation(@Body loc: Model.location):Observable<Model.location>
}