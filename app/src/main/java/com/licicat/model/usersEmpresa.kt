package com.licicat.model

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


val db = Firebase.firestore
val usersRef = db.collection("usersEmpresa")

data class usersEmpresa(
    val id: String?,
    val user_id: String,
    val empresa: String,
    val nom_cognoms: String,
    val email: String,
    val avatar_url: String,
    val nif: String,
    val telefon: String
){
    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "user_id" to this.user_id,
            "empresa" to this.empresa,
            "nom_cognoms" to this.nom_cognoms,
            "email" to this.email,
            "nif" to this.nif,
            "telefon" to this.telefon,
            "avatar_url" to this.avatar_url,
        )
    }
}
