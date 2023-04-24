package com.licicat.model

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


val db = Firebase.firestore
val usersRef = db.collection("usersEmpresa")

data class usersEmpresa(
    val id: String?,
    val user_id: String,
    val Empresa: String,
    val Nom: String,
    val email: String,
    val avatarUrl: String,
    val NIF: String,
    val Telefon: String
){
    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "user_id" to this.user_id,
            "empresa" to this.Empresa,
            "nom_cognoms" to this.Nom,
            "email" to this.email,
            "nif" to this.NIF,
            "telefon" to this.Telefon,
            "avatar_url" to this.avatarUrl,
        )
    }
}
