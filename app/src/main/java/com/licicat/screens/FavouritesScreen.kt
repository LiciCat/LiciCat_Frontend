package com.licicat.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.licicat.components.BottomBarNavigation
import com.licicat.navigation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.material.*
import androidx.compose.material.icons.filled.AccountCircle
import com.google.firebase.firestore.DocumentReference
import com.licicat.components.CardLicitacio
import com.licicat.model.usersEmpresa


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FavouritesScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController)
        }
    ) {

        val db = Firebase.firestore
        val current_user = FirebaseAuth.getInstance().currentUser
        val id_del_user = current_user?.uid
        val numeros = mutableListOf<Int>()

        db.collection("usersEmpresa")
            .whereEqualTo("user_id", id_del_user)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("app", "${document.id} => ${document.data}")
                    val numerosDocument = document.get("favorits") as List<Long>
                    numeros.addAll(numerosDocument.map { it.toInt() })
                }
                trobar_lic(numeros)
            }
            .addOnFailureListener { exception ->
                Log.w("app", "Error getting documents: ", exception)
            }

        /*
        val current_user = FirebaseAuth.getInstance().currentUser

        println("HOLAAAAAAAAAAAAAAA")
        Log.d("app", current_user.toString())
        Log.d("app", "User ID: ${current_user?.uid}")
        Log.d("app", "Email: ${current_user?.email}")
        val db = Firebase.firestore
        if (current_user != null) {
            println("HOLAAAAAAAAAAAAAAA")
            val userRef = db.collection("usersEmpresa").document(current_user.uid)
            userRef.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("Consulta completada")
                    } else {
                        println("Error al consultar la base de datos")
                    }
                }
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null) {
                        Log.d("app", documentSnapshot.data.toString())
                        val user = documentSnapshot.data

                        if (user != null) {
                            println("HOLAAAAAAAAAAAAAAA")
                            //val favouriteLicitations = user.user_id
                            //println(favouriteLicitations)
                        }
                    }
                }
        }
    }
    */
    }
}


fun trobar_lic(numeros: List<Int>) {
    val db = Firebase.firestore
    for (numero in numeros) {
        db.collection("licitacionsFavorits")
            .whereEqualTo("lic_id", numero)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("app", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("app", "Error getting documents: ", exception)
            }
    }
}

/*
val userId = user.uid
            val query = db.collection("licitacionsFavorits").whereEqualTo("userId", userId)
            db.collection("licitacionsFavorits")
                .whereArrayContains("usuarios", db.document("usersEmpresa/$userId"))
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                                CardLicitacio(
                                    icon = Icons.Filled.AccountCircle,
                                    title = document.getString("title"),
                                    description = document.getString("description"),
                                    date = document.getString("date"),
                                    price = document.getString("price")
                                )
                            }
                }
                .addOnFailureListener { exception ->
                }
        }

        LazyColumn {
            items(items = favLicitacions) { licitacio ->
                CardLicitacio(
                    icon = licitacio.icon,
                    title = licitacio.title,
                    description = licitacio.description,
                    date = licitacio.date,
                    price = licitacio.price
                )
            }
        }
 */