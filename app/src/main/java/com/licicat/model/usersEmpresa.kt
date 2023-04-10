package com.licicat.model

data class usersEmpresa(
    val id: String?,
    val userId: String,
    val Empresa: String,
    val Nom: String,
    val email: String,
    val avatarUrl: String,
    val NIF: String,
    val Telefon: String
){
    fun toMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "user_id" to this.userId,
            "empresa" to this.Empresa,
            "nom_cognoms" to this.Nom,
            "email" to this.email,
            "nif" to this.NIF,
            "telefon" to this.Telefon,
            "avatar_url" to this.avatarUrl
        )
    }
}
