package com.rent.data



object Model {
    data class location(val id: Int, val dateDebut: String, val dateFin : String, val color : String, val locataire : locataire)

    data class payment(val id: Int , val paiement_date: String,val montant: Int , val type :String, val location: location)

    data class locataire(val id: Int , val cin: String,val full_name: String , val numTel:List<String>)

}