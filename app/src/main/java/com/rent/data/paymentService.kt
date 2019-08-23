package com.rent.data


import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.*

interface PaymentServices {


    companion object {
        fun create(): PaymentServices {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(
                    RxJava2CallAdapterFactory.create()
                )
                .addConverterFactory(GsonConverterFactory.create())
//                    .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("http://xosted.alwaysdata.net/paiement/")
                .build()

            return retrofit.create(PaymentServices::class.java)
        }
    }


    @GET("read.php")
    fun selectPayments(): Observable<List<Model.payment>>


    @GET("read_one.php")
    fun selectPaymentById(@Query ("id") id:Int): Observable<Model.payment>

    @DELETE("delete.php")
    fun deletePayment(@Query ("id") id:Int): Observable<Model.msgResult>

    @GET("readbyLocationId.php")
    fun selectLocPayments(@Query ("locationid") locationKey:Int): Observable<List<Model.payment>>

    @POST("create.php")
    fun addPayment(@Body paiement:Model.payment): Observable<Model.msgResult>


    @PUT("update.php")
    fun updatePayment(@Body paiement:Model.payment): Observable<Model.payment>
}