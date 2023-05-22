package com.licicat.model
import kotlin.collections.*
data class usersEntitat(
    val id: String?,
    val userId: String,
    val Entitat: String,
    val Nom: String,
    val email: String,
    val avatarUrl: String,
    val NIF: String,
    val Telefon: String,
    val seguidors: List<Int>
){
    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "user_id" to this.userId,
            "entitat" to this.Entitat,
            "nom_cognoms" to this.Nom,
            "email" to this.email,
            "nif" to this.NIF,
            "telefon" to this.Telefon,
            "avatar_url" to this.avatarUrl,
            "seguidors" to this.seguidors
        )
    }
}
