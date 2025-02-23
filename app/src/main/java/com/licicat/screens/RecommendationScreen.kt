package com.licicat.screens

import android.content.Context
import android.location.Geocoder
import android.util.Log
import kotlinx.coroutines.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

import androidx.navigation.NavController
import com.example.licicat.Licitacio

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

import kotlin.math.log


var mcUbi:MutableMap<String, Pair<Double, Double> > = mutableMapOf()
// Función para calcular el TF-IDF entre dos documentos
fun calcularTFIDF(doc1: String, doc2: String, corpus: List<String>): Double {
    // Tokenización de los documentos en términos individuales (palabras)
    val palabrasDoc1 = doc1.split(" ")
    val palabrasDoc2 = doc2.split(" ")

    // Construcción del vocabulario único a partir del corpus
    val vocabulario = corpus.flatMap { it.split(" ") }.distinct()

    // Cálculo del TF (Term Frequency) para cada documento
    val tfDoc1 = calcularTF(palabrasDoc1)
    val tfDoc2 = calcularTF(palabrasDoc2)

    // Cálculo del IDF (Inverse Document Frequency)
    val idf = calcularIDF(vocabulario, corpus)

    // Cálculo del TF-IDF
    val tfidfDoc1 = calcularTFIDF(tfDoc1, idf)
    val tfidfDoc2 = calcularTFIDF(tfDoc2, idf)

    // Cálculo de la similitud entre los documentos (por ejemplo, usando la similitud del coseno)
    return calcularSimilitudCoseno(tfidfDoc1, tfidfDoc2)
}

// Función para calcular el TF (Term Frequency)
fun calcularTF(palabras: List<String>): Map<String, Double> {
    val frecuencia = mutableMapOf<String, Int>()
    val tf = mutableMapOf<String, Double>()

    for (palabra in palabras) {
        frecuencia[palabra] = frecuencia.getOrDefault(palabra, 0) + 1
    }

    val totalPalabras = palabras.size.toDouble()

    for ((palabra, count) in frecuencia) {
        tf[palabra] = count / totalPalabras
    }

    return tf
}

// Función para calcular el IDF (Inverse Document Frequency)
fun calcularIDF(vocabulario: List<String>, corpus: List<String>): Map<String, Double> {
    val idf = mutableMapOf<String, Double>()

    val numDocumentos = corpus.size.toDouble()

    for (palabra in vocabulario) {
        val numDocumentosContienenPalabra = corpus.count { it.contains(palabra) }
        idf[palabra] = log(numDocumentos / (1 + numDocumentosContienenPalabra), 10.0)
    }

    return idf
}

// Función para calcular el TF-IDF
fun calcularTFIDF(tf: Map<String, Double>, idf: Map<String, Double>): Map<String, Double> {
    val tfidf = mutableMapOf<String, Double>()

    for ((palabra, tfValue) in tf) {
        tfidf[palabra] = tfValue * idf[palabra]!!
    }

    return tfidf
}

// Función para calcular la similitud del coseno entre dos documentos representados por vectores TF-IDF
fun calcularSimilitudCoseno(doc1: Map<String, Double>, doc2: Map<String, Double>): Double {
    val dotProduct = doc1.map { (palabra, tfidf) -> tfidf * (doc2[palabra] ?: 0.0) }.sum()
    val normDoc1 = Math.sqrt(doc1.values.map { it * it }.sum())
    val normDoc2 = Math.sqrt(doc2.values.map { it * it }.sum())

    val similitud =  dotProduct / (normDoc1 * normDoc2)
    return (similitud + 1) / 2
}

fun ajustarSimilitud(similitud: Double, umbral: Double): Double {
    return if (similitud >= umbral) {
        1.0
    } else {
        0.0
    }
}

// Función para calcular la similitud entre dos licitaciones
private fun calcularSimilitud(licitacion1: Licitacio, licitacion2: Licitacio, context: Context, corpus: List<Pair<String,String>>): Double {

    val similitudPrecio = calcularSimilitudPrecio(licitacion1.pressupost_licitacio, licitacion2.pressupost_licitacio)
    val similitudUbicacio = calcularSimilitudUbi(licitacion1.lloc_execucio, licitacion2.lloc_execucio, context)
    val similitudDescripcio = 0.0//calcularTFIDF(licitacion1.objecte_contracte ?: "", licitacion2.objecte_contracte ?: "", corpus.map { it.first })
    val similitudOrgan = calcularTFIDF(licitacion1.nom_organ ?: "", licitacion2.nom_organ ?: "", corpus.map { it.second })

    val umbral = 0.505 // Establecer el umbral deseado
    //val similitudFitedDescripcio = ajustarSimilitud(similitudDescripcio, umbral)

    // Ponderar y combinar las similitudes según su importancia relativa
    val pesoPrecio = 0.25
    val pesoUbicacio = 0.55
    val pesoDescripcio = 0.0
    val pesoOrgan = 0.15

    val similitudTotal = (similitudPrecio * pesoPrecio) + (similitudUbicacio * pesoUbicacio) + (similitudOrgan * pesoOrgan)
    println("Nom organ normalizada: " + similitudOrgan)
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
        }
        else return 0.3
    } else if(precio2 == 0){ return 0.3}

    else {
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

    if (mcUbi.contains(ubi)){
        println("ACCEDINT A CACHE")
        return mcUbi.get(ubi)!!

    }
    else {
        val geocoder = Geocoder(context)
        val result = ubi?.let { geocoder.getFromLocationName(it, 1) }
        lateinit var latLng: LatLng
        if (result != null && result.isNotEmpty()) {
            val location = result[0]
            latLng = LatLng(location.latitude, location.longitude)
            println("Latitud: ${latLng.latitude} Longitud: ${latLng.longitude}")
            mcUbi.put(ubi, Pair(latLng.latitude, latLng.longitude))
            println("ACCES A API")
            return Pair(latLng.latitude, latLng.longitude)
        }
        return Pair(0.0, 0.0)
    }
}


private fun calcularSimilitudUbi(ubi1: String?, ubi2: String?, context: Context): Double {
    if (ubi1 == null || ubi2 == null) {
        println("ES null")
        return 0.0
    } else {

        val ubiP1 = getCoordinates(ubi1, context)
        val ubiP2 = getCoordinates(ubi2, context)
        println("Funciona ubi aqui")
        val distancia = getDistanceBetweenPoints(ubiP1.first, ubiP1.second, ubiP2.first, ubiP2.second)
        val maximaDistancia = 50.0 // Definir la máxima distancia considerada como "similitud" (en kilómetros)
        val normalizada = 1.0 - (distancia / maximaDistancia)
        if(normalizada < 0.0) return 0.0
        else if(ubi1 == "Catalunya" || ubi2 == "Catalunya") return 0.0
        else return normalizada
    }
    return 0.0
}







private fun calcularSimilitudPromedio(lista1: List<Licitacio>, lista2: List<Licitacio>, context: Context, corpus: List<Pair<String,String>>): Double {
    var totalSimilitud = 0.0
    val totalComparaciones = lista1.size * lista2.size

    println("test_previ_bucle")

    for (licitacion1 in lista1) {
        for (licitacion2 in lista2) {
            totalSimilitud += calcularSimilitud(licitacion1, licitacion2, context, corpus)
            println("test_bucle")
        }
    }

    return totalSimilitud / totalComparaciones
}



fun RecommendationScreen(navController: NavController, originalLicitacions: List<Licitacio>, callback: (List<Licitacio>) -> Unit) {
    val licitacions_favs = mutableListOf<Licitacio>()
    val licitacions_all = originalLicitacions
    val isLoading = mutableStateOf(true)
    var l : List<Licitacio> = emptyList()


    val db = Firebase.firestore
    val current_user = FirebaseAuth.getInstance().currentUser
    val id_del_user = current_user?.uid
    val numeros = mutableListOf<Int>()
    val licitaciones = mutableListOf<Licitacio>()

    db.collection("usersEmpresa")
        .whereEqualTo("user_id", id_del_user)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                val numerosDocument = document.get("favorits") as List<Long>
                numeros.addAll(numerosDocument.map { it.toInt() })
            }

            val tasks = mutableListOf<Task<QuerySnapshot>>()

            for (numero in numeros) {
                val task = db.collection("licitacionsFavorits")
                    .whereEqualTo("lic_id", numero)
                    .get()
                tasks.add(task)
            }

            Tasks.whenAllComplete(tasks)
                .addOnSuccessListener { snapshots ->
                    for (snapshot in snapshots) {
                        val documents = snapshot.result as QuerySnapshot
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
                            println("Bucle intern:")
                            println(licitaciones.size)
                            println(licitacio.nom_organ)
                        }
                    }
                    println("Mida de licitaciones: ")
                    println(licitaciones.size)
                    isLoading.value = false
                    if (!isLoading.value) {
                        val objecteYOrganizacionList: List<Pair<String, String>> = licitacions_all.map { licitacion ->
                            val objecteContracte = licitacion.objecte_contracte ?: ""
                            val nombreOrganizacion = licitacion.nom_organ ?: ""
                            Pair(objecteContracte, nombreOrganizacion)
                        }

                        val similitudPromedio = calcularSimilitudPromedio(
                            licitaciones.toList(),
                            licitacions_all,
                            navController.context,
                            objecteYOrganizacionList
                        )

                        var calc = 0.0

                        val licitacionesSimilares = licitacions_all.filter {
                            calc = calcularSimilitudPromedio(
                                licitaciones.toList(),
                                listOf(it),
                                navController.context,
                                objecteYOrganizacionList
                            )
                            calc > similitudPromedio
                        }.sortedByDescending { licitacion -> calc }

                        println("Mida de la llista de licitacions similars:")
                        println(licitacionesSimilares.size)
                        l = licitacionesSimilares
                        callback(l)
                    }

                    //return emptyList()
                }
                .addOnFailureListener { exception ->
                    Log.w("app", "Error getting documents: ", exception)
                }
        }
        .addOnFailureListener { exception ->
            Log.w("app", "Error getting documents: ", exception)
        }
    println("mida l:")
    println(l.size)
}




