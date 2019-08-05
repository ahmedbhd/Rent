package com.rent.data


import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PaymentServices {


    companion object {
        fun create(): PaymentServices {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(GsonConverterFactory.create())
//                    .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("https://bhd4.000webhostapp.com/")
                .build()

            return retrofit.create(PaymentServices::class.java)
        }
    }


    @GET("services.php?action=selectPayments")
    fun selectPayments(): Observable<List<Model.payment>>


    @GET("services.php?action=selectLocationById")
    fun selectLocationById(@Query ("id") id:Int): Observable<Model.location>

    @GET("services.php?action=delPayment")
    fun deletePayment(@Query ("id") id:Int): Observable<String>

    @GET("services.php?action=selectLocPayments")
    fun selectLocPayments(@Query ("locationKey") locationKey:Int): Observable<List<Model.payment>>

    @GET("services.php?action=addPayment")
    fun addPayment(@Query ("amount") amount:Int ,
                   @Query ("date") date:String,
                   @Query ("locationKey") locationKey:Int,
                   @Query ("type") type:String): Observable<String>
}