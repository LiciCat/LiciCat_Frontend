package com.licicat.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.lifecycle.MutableLiveData
import com.example.licicat.Licitacio
import com.google.firebase.firestore.DocumentReference
import com.licicat.components.CardLicitacio
import com.licicat.model.usersEmpresa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.licicat.LicitacionsRepository
import com.licicat.components.BottomBarNavigation
import com.licicat.components.CardLicitacio


var licitacions_favs: MutableList<Licitacio> = mutableListOf()
var presentacio =  mutableStateOf(false)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FavouritesScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController)
        }
    ) {
        licitacions_favs.clear()
        val db = Firebase.firestore
        val current_user = FirebaseAuth.getInstance().currentUser
        val id_del_user = current_user?.uid
        val numeros = mutableListOf<Int>()

        LaunchedEffect(true) {
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
            }
        }
        if (presentacio.value == true) {
            LicitacionsList()
        }
}

@Composable
fun LicitacionsList() {
    LazyColumn {
        items(items = licitacions_favs) { licitacio ->
                CardLicitacio(
                    icon = Icons.Filled.AccountCircle,
                    title = licitacio.nom_organ,
                    description = licitacio.objecte_contracte,
                    date = licitacio.termini_presentacio_ofertes.toString(),
                    price = licitacio.pressupost_licitacio_asString
                )
        }
    }
}

fun trobar_lic(numeros: List<Int>) {
    val db = Firebase.firestore
    var documentosCompletados = 0
    for (numero in numeros) {
        db.collection("licitacionsFavorits")
            .whereEqualTo("lic_id", numero)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("app", "${document.id} => ${document.data}")
                    var licitacio = Licitacio()
                    licitacio.nom_organ = document.get("title") as String;
                    licitacio.objecte_contracte = document.get("description") as String;
                    licitacio.termini_presentacio_ofertes = document.get("date") as String;
                    licitacio.pressupost_licitacio_asString = document.get("price") as String;
                    licitacions_favs.add(licitacio);
                }
                documentosCompletados++
                if (documentosCompletados == numeros.size) {
                    presentacio.value = true
                }
            }
            .addOnFailureListener { exception ->
                Log.w("app", "Error getting documents: ", exception)
            }
    }
}
