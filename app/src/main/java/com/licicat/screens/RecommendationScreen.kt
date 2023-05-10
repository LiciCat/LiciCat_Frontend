package com.licicat.screens

import kotlin.math.abs
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.example.licicat.Licitacio
import com.google.maps.android.compose.GoogleMap
import com.licicat.LicitacionsRepository
import com.licicat.components.BottomBarNavigation
import com.licicat.components.CardLicitacio

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import java.text.SimpleDateFormat
import java.util.*



// Función para calcular la similitud entre dos licitaciones
private fun calcularSimilitud(licitacion1: Licitacio, licitacion2: Licitacio, context: Context): Double {

    val similitudPrecio = calcularSimilitudPrecio(licitacion1.pressupost_licitacio, licitacion2.pressupost_licitacio)
    val similitudUbicacio = calcularSimilitudUbi(licitacion1.lloc_execucio, licitacion2.lloc_execucio, context)

    // Ponderar y combinar las similitudes según su importancia relativa
    val pesoPrecio = 0.5
    val pesoUbicacio = 0.5

    val similitudTotal = (similitudPrecio * pesoPrecio) + (similitudUbicacio * pesoUbicacio)
    println("Distancia normalizada: " + similitudUbicacio)
    return similitudTotal
}
// Función para calcular la similitud de precio (ejemplo de diferencia porcentual)
private fun calcularSimilitudPrecio(precio1: Int?, precio2: Int?): Double {
    if (precio1 == null || precio2 == null) {
        println("ES null")
        return 0.0
    } else if (precio1 == 0) {
        if (precio2 == 0) {
            return 1.0 // Ambos precios son 0, por lo tanto, son idénticos
        } else {
            val similitudMinima = 0.3 // Establece un valor mínimo de similitud deseado cuando precio1 es 0
            return similitudMinima
        }
    } else {
        val maxPrecio = maxOf(precio1, precio2).toDouble()
        val minPrecio = minOf(precio1, precio2).toDouble()

        val diferencia = maxPrecio - minPrecio
        val normalizacion = if (maxPrecio != 0.0) diferencia / maxPrecio else 0.0

        return 1.0 - normalizacion //0 representa ninguna similitud y 1 representa una similitud perfecta
    }
}

private fun getDistanceBetweenPoints(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Double {
    val theta = longitude1 - longitude2
    val distance = 60 * 1.1515 * (180/Math.PI) * Math.acos(
        Math.sin(latitude1 * (Math.PI/180)) * Math.sin(latitude2 * (Math.PI/180)) +
                Math.cos(latitude1 * (Math.PI/180)) * Math.cos(latitude2 * (Math.PI/180)) * Math.cos(theta * (Math.PI/180))
    )
    return Math.round(distance * 1.609344 * 100) / 100.0 // Redondear a dos decimales
}

private fun getCoordinates(ubi: String?, context: Context): Pair<Double, Double> {

    val geocoder = Geocoder(context)
    val result = ubi?.let { geocoder.getFromLocationName(it, 1) }
    lateinit var latLng: LatLng
    if (result != null && result.isNotEmpty()) {
        val location = result[0]
        latLng = LatLng(location.latitude, location.longitude)
        println("Latitud: ${latLng.latitude} Longitud: ${latLng.longitude}")
        return Pair(latLng.latitude, latLng.longitude)
    }
    return Pair(0.0, 0.0)
}


private fun calcularSimilitudUbi(ubi1: String?, ubi2: String?, context: Context): Double {
    if (ubi1 == null || ubi2 == null) {
        println("ES null")
        return 0.0
    } else {
        val ubiP1 = getCoordinates(ubi1, context)
        val ubiP2 = getCoordinates(ubi2, context)
        val distancia = getDistanceBetweenPoints(ubiP1.first, ubiP1.second, ubiP2.first, ubiP2.second)
        val maximaDistancia = 50.0 // Definir la máxima distancia considerada como "similitud" (en kilómetros)
        val normalizada = 1.0 - (distancia / maximaDistancia)
        if(normalizada < 0.0) return 0.0
        else if(ubi1 == "Catalunya" || ubi2 == "Catalunya") return 0.0
        else return normalizada
    }
    return 0.0
}




private fun obtenerLicitacionesFavoritas(onSuccess: (List<Licitacio>) -> Unit) {
    val db = Firebase.firestore
    val current_user = FirebaseAuth.getInstance().currentUser
    val id_del_user = current_user?.uid
    val numeros = mutableListOf<Int>()

    db.collection("usersEmpresa")
        .whereEqualTo("user_id", id_del_user)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                val numerosDocument = document.get("favorits") as List<Long>
                numeros.addAll(numerosDocument.map { it.toInt() })
            }
            obtenerLicitaciones(numeros, onSuccess)
        }
        .addOnFailureListener { exception ->
            Log.w("app", "Error getting documents: ", exception)
        }
}



private fun obtenerLicitaciones(numeros: List<Int>, onSuccess: (List<Licitacio>) -> Unit) {
    val db = Firebase.firestore
    val licitaciones = mutableListOf<Licitacio>()
    var documentosCompletados = 0

    for (numero in numeros) {
        db.collection("licitacionsFavorits")
            .whereEqualTo("lic_id", numero)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val licitacio = Licitacio(
                        nom_organ = document.get("title") as String,
                        objecte_contracte = document.get("description") as String,
                        termini_presentacio_ofertes = document.get("date") as String,
                        pressupost_licitacio_asString = document.get("price") as String,
                        pressupost_licitacio = (document.get("price") as String).replace(".", "").toInt(),
                        lloc_execucio = document.get("location") as String,
                        denominacio = document.get("denomination") as String,
                        data_publicacio_anunci = document.get("date_inici") as String,
                        data_publicacio_adjudicacio = document.get("date_adjudicacio") as String,
                        tipus_contracte = document.get("tipus_contracte") as String



                    )
                    licitaciones.add(licitacio)
                }
                documentosCompletados++
                if (documentosCompletados == numeros.size) {
                    onSuccess(licitaciones)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("app", "Error getting documents: ", exception)
            }
    }
}


private fun calcularSimilitudPromedio(lista1: List<Licitacio>, lista2: List<Licitacio>, context: Context): Double {
    var totalSimilitud = 0.0
    val totalComparaciones = lista1.size * lista2.size

    println("test_previ_bucle")

    for (licitacion1 in lista1) {
        for (licitacion2 in lista2) {
            totalSimilitud += calcularSimilitud(licitacion1, licitacion2, context)
            println("test_bucle")
        }
    }

    return totalSimilitud / totalComparaciones
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RecommendationScreen(navController: NavController, originalLicitacions: List<Licitacio>) {
    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController)
        }
    ) {
        val licitacions_favs = remember { mutableStateOf(emptyList<Licitacio>()) }
        val licitacions_all =  originalLicitacions
        val presentacio = remember { mutableStateOf(false) }


        LaunchedEffect(Unit) {
            obtenerLicitacionesFavoritas { licitaciones ->
                licitacions_favs.value = licitaciones
                presentacio.value = true
            }
            println("fin launched effect")

        }







        if (presentacio.value) {
            println("Control" + licitacions_favs.value.size + "<- favs || all ->"+ licitacions_all.size)
            val similitudPromedio = calcularSimilitudPromedio(licitacions_favs.value.toList(), licitacions_all, navController.context)
            var calc = 0.0
            println("Control" + similitudPromedio)
            println("prova favs" + licitacions_favs.value.get(0).pressupost_licitacio)

            val licitacionesSimilares = licitacions_all.filter {  calc = calcularSimilitudPromedio(listOf(it), licitacions_favs.value.toList(), navController.context);
                calc > similitudPromedio
            }.sortedByDescending { licitacion -> calc }


            println("fin" + licitacionesSimilares.size)

            LazyColumn(modifier = Modifier
                .padding(bottom = 56.dp)
                .fillMaxWidth()) {

                items(items = licitacionesSimilares) { licitacio ->
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
                        tipus_contracte = licitacio.tipus_contracte
                    )
                }
            }
        }
    }
}
