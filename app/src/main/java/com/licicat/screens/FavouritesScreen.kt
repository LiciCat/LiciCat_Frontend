package com.licicat.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.navigation.NavController
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
import androidx.compose.ui.unit.dp
import com.licicat.AppType
import com.licicat.LicitacionsRepository
import com.licicat.UserType
import com.licicat.components.*


val licitacions_favs: MutableLiveData<List<Licitacio>> = MutableLiveData(emptyList())
var presentacio =  mutableStateOf(false)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FavouritesScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            if (AppType.getUserType() == UserType.EMPRESA) BottomBarNavigation(navController)
            //else if (AppType.getUserType() == UserType.UNKNOWN) BottomBarNavigation(navController)
            else if (AppType.getUserType() == UserType.ENTITAT) BottomBarNavigationEntitat(navController)
        }
    ) {

        licitacions_favs.value = emptyList()
        val db = Firebase.firestore
        val current_user = FirebaseAuth.getInstance().currentUser
        val id_del_user = current_user?.uid
        val numeros = mutableListOf<Int>()
        presentacio.value = false

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
        LazyColumn(modifier = Modifier
            .padding(bottom = 56.dp)
            .fillMaxWidth()) {

            items(items = licitacions_favs.value ?: emptyList()) { licitacio ->
                CardLicitacio(
                    icon = Icons.Filled.AccountCircle,
                    title = licitacio.nom_organ,
                    description = licitacio.objecte_contracte,
                    date = licitacio.termini_presentacio_ofertes.toString(),
                    price = licitacio.pressupost_licitacio_asString,
                    navController = navController,
                    location = licitacio.lloc_execucio,
                    denomination = licitacio.denominacio,
                    date_inici = licitacio.data_publicacio_anunci,
                    date_adjudicacio = licitacio.data_publicacio_adjudicacio,
                    tipus_contracte = licitacio.tipus_contracte,
                    enllac_publicacio = licitacio.enllac_publicacio
                )
            }
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
                    licitacio.lloc_execucio = document.get("location") as String;
                    licitacio.denominacio=document.get("denomination") as String;
                    licitacio.data_publicacio_anunci=document.get("date_inici") as String;
                    licitacio.data_publicacio_adjudicacio=document.get("date_adjudicacio") as String;
                    licitacio.tipus_contracte=document.get("tipus_contracte") as String;
                    licitacio.enllac_publicacio=document.get("enllac_publicacio") as String;
                    val listaActual = licitacions_favs.value?.toMutableList() ?: mutableListOf()
                    listaActual.add(licitacio)
                    licitacions_favs.value = listaActual
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

