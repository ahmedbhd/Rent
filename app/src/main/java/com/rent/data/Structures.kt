package com.rent.data

import java.sql.Timestamp
import java.util.*


object Model {
    data class location(val id: String, val cin: String, val color : String, val start : String, val end : String, val tel : String
                                , val text : String)

    data class payment(val id: String , val amount: String,val date: String , val locationKey:String, val type: String)

    data class locataire(val id: Int , val cin: String,val full_name: String , val numTel:List<String>)

}