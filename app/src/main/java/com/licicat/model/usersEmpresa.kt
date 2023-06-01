package com.licicat.model
import kotlin.collections.*
data class usersEmpresa(
    val id: String?,
    val userId: String,
    val Empresa: String,
    val Nom: String,
    val email: String,
    val avatarUrl: String,
    val NIF: String,
    val Telefon: String,
    val favorits: List<Int>,
    val optats: List<Int>,
    val descripcio: String
){
    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "user_id" to this.userId,
            "empresa" to this.Empresa,
            "nom_cognoms" to this.Nom,
            "email" to this.email,
            "nif" to this.NIF,
            "telefon" to this.Telefon,
            "avatar_url" to this.avatarUrl,
            "favorits" to this.favorits,
            "optats" to this.optats,
            "descripcio" to this.descripcio
        )
    }
}
