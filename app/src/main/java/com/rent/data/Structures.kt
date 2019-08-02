package com.rent.data

import java.sql.Timestamp
import java.util.*


object Model {
    data class location(val id: String, val cin: String, val color : String, val start : String, val end : String, val tel : String
                                , val text : String)

    data class payment(val id: Int , val amount: Int,val date: String , val locationKey:Int, val type: String)


}