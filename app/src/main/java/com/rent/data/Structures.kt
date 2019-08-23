package com.rent.data



object Model {
    data class location(val id: Int = 0, var date_debut: String = "", var date_fin : String="", var color : String="", var locataire : locataire = locataire())

    data class payment(val id: Int , val paiement_date: String,val montant: Int , val type :String, val location: location)

    data class locataire(val id: Int=0, var cin: String="", var full_name: String="", var num_tel:String="")

    data class msgResult(val message:String)
}