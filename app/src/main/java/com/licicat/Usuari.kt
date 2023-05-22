package com.licicat

import java.sql.Timestamp
import java.util.*

data class Usuari (
    var user_id : String? = null,
    var empresa : String? = null,
    var email : String? = null,
    var nif : Int? = null,
    var telefon : Int? = null,
    var nom_cognoms : String? = null,
    var descripcio : String? = null
)