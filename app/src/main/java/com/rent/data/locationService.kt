package com.rent.data

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationServices {


    companion object {
        fun create(): LocationServices {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(GsonConverterFactory.create())
//                    .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("https://bhd4.000webhostapp.com/")
                .build()

            return retrofit.create(LocationServices::class.java)
        }
    }


    @GET("services.php?action=selectLocations")
    fun selectLocations(): Observable<List<Model.location>>


    @GET("services.php?action=selectLocationById")
    fun selectLocationById(@Query ("id") id:Int): Observable<Model.location>

    @GET("services.php?action=delLocation")
    fun deleteLocation(@Query ("id") id:Int): Observable<String>
}