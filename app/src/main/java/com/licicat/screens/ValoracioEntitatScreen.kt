package com.licicat.screens


import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.RatingBar
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.licicat.components.BottomBarNavigation
import com.licicat.model.Chat
import com.licicat.navigation.AppScreens
import java.util.*

import androidx.compose.material.Text
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.FieldValue

import com.google.firebase.messaging.RemoteMessage.Notification.*

import com.licicat.components.BottomBarNavigationEntitat

import com.licicat.*
import com.licicat.R
import java.text.SimpleDateFormat


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ValoracioEntitatScreen(navController: NavController, intent: Intent) {
    val ratingItems = remember {
        mutableStateListOf(
            RatingItem("Amabalitat"),
            RatingItem("Rapidesa"),
            RatingItem("Bona Comunicació"),
            RatingItem("Informació Present")
            // Agrega más elementos de valoración según tus necesidades
        )
    }
    val entityName = intent.getStringExtra("nom_entitat") ?: ""

    val averageRatingState = remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }
    var backgroundColor by remember { mutableStateOf(Color(android.graphics.Color.rgb(219, 41, 59))) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Valorant a " + entityName, style = MaterialTheme.typography.h6)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(AppScreens.HomeScreen.route) {
                        popUpTo(AppScreens.HomeScreen.route) { inclusive = true }
                    } }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomBarNavigation(navController)
        }
    ) {
        var valoracio by remember { mutableStateOf(0F) }
        val db = Firebase.firestore
        LaunchedEffect(true){
            db.collection("usersEntitat")
                .whereEqualTo("entitat", entityName)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("appEntitat", "${document.id} => ${document.data}")
                        valoracio =  (document.get("valoracio") as Double).toFloat()

                    }

                }
                .addOnFailureListener { exception ->
                    Log.w("app", "Error getting documents: ", exception)
                }
        }
        Column(){
            Column(modifier = Modifier
                .fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(
                            backgroundColor,
                            shape = RoundedCornerShape(bottomEnd = 300.dp, bottomStart = 300.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = averageRatingState.value.format(1),
                        style = MaterialTheme.typography.h4,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Nota media en tiempo real
                backgroundColor = getBackgroundColor(averageRatingState.value)



                // Elementos de valoración
                Column(modifier = Modifier.fillMaxWidth()) {
                    ratingItems.forEach { ratingItem ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            elevation = 4.dp
                        ) {
                            RatingItemRow(
                                ratingItem = ratingItem,
                                onRatingChanged = { newRating ->
                                    ratingItem.rating = newRating
                                    updateAverageRating(ratingItems, averageRatingState)
                                }
                            )
                        }
                    }

                }

                // Comentario extra opcional
                TextField(
                    value = comment,
                    onValueChange = { newComment -> comment = newComment },
                    label = { Text(stringResource(R.string.comentari_valoracio)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                // Botón de enviar
                Button(
                    onClick = { enviarValoracion(entityName, ratingItems, comment, averageRatingState, valoracio);
                              navController.navigate(AppScreens.HomeScreen.route) {
                            popUpTo(AppScreens.HomeScreen.route) { inclusive = true }
                        }},
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "Enviar")
                }
            }
        }

    }
}



private fun getBackgroundColor(averageRating: Float): Color {
    return when {
        averageRating >= 7f -> Color(android.graphics.Color.rgb(127, 223, 17))
        averageRating >= 4f -> Color(android.graphics.Color.rgb(242, 142, 37))
        else -> Color(android.graphics.Color.rgb(219, 41, 59))
    }
}

private fun updateAverageRating(ratingItems: List<RatingItem>, averageRatingState: MutableState<Float>) {
    val totalRating = ratingItems.sumBy { it.rating.toInt() }
    val totalRatingsCount = ratingItems.size
    val averageRating = if (totalRatingsCount > 0) totalRating.toFloat() / totalRatingsCount else 0f
    averageRatingState.value = averageRating * 2 // Escala de 0 a 10
}





@Composable
fun RatingItemRow(ratingItem: RatingItem, onRatingChanged: (Float) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {


        Text(text = ratingItem.title, modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.width(16.dp))

        RatingBar(
            rating = ratingItem.rating,
            onRatingChanged = onRatingChanged,



        )
    }
}


@Composable
fun RatingBar(rating: Float, onRatingChanged: (Float) -> Unit) {
    val maxRating = 5 // Número máximo de estrellas
    val selectedRating = remember { mutableStateOf(rating) }

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
        for (i in 1..maxRating) {
            val starColor = if (i <= selectedRating.value) {
                MaterialTheme.colors.primary
            } else {
                Color.LightGray
            }

            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier
                    .clickable {
                        selectedRating.value = i.toFloat()
                        onRatingChanged(selectedRating.value)
                    }
            )
        }
    }
}

data class RatingItem(
    val title: String,
    var rating: Float = 0f
)

fun Float.format(decimals: Int): String {
    return "%.${decimals}f".format(this)
}


fun enviarValoracion(nom_entitat: String, ratingItems: List<RatingItem>, comment: String, rating: MutableState<Float>, valoracioPrevia: Float) {
    val db = Firebase.firestore

    val current_user = FirebaseAuth.getInstance().currentUser
    val id_user = current_user?.uid

    val collectionRef = db.collection("usersEntitat")
    val collectionRef2 = db.collection("valoracioEmpresaToEntitat")
    val collectionRef3 = db.collection("usersEntitat")

    val entidadNombre = nom_entitat
    var entidadId: String = ""
    var valoracionId: String = ""
    val ratingTotal = rating.value

    collectionRef
        .whereEqualTo("entitat", entidadNombre)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                entidadId = document.getString("user_id") ?: ""
                println("El ID de la entidad $entidadNombre es $entidadId")
                val documentId = document.id
                var valoracioNova = 0F
                if(valoracioPrevia == 0.00001F){
                     valoracioNova = ratingTotal + valoracioPrevia
                }
                else {
                    valoracioNova = (ratingTotal + valoracioPrevia) / 2
                }


                val updateData = hashMapOf(
                    "valoracio" to valoracioNova
                )
                collectionRef.document(documentId)
                    .update(updateData as Map<String, Any>)
                    .addOnSuccessListener {
                        println("El campo valoracion se actualizó correctamente.")
                    }
                    .addOnFailureListener { e ->
                        println("Error al actualizar el campo valoracion: $e")
                    }

                val valoracion = hashMapOf(
                    "comment" to comment,
                    "data" to obtenerFechaActualString(),
                    "id_empresa" to id_user,
                    "id_entitat" to entidadId,
                    "pointsAm" to ratingItems[0].rating.toInt(),
                    "pointsBC" to ratingItems[1].rating.toInt(),
                    "pointsIF" to ratingItems[2].rating.toInt(),
                    "pointsRa" to ratingItems[3].rating.toInt(),
                    "ratingTotal" to ratingTotal
                )
                collectionRef2
                    .add(valoracion)
                    .addOnSuccessListener { documentReference ->
                        valoracionId = documentReference.id
                        println("Valoración añadida con ID: $valoracionId")


                        val query = collectionRef3.whereEqualTo("user_id", entidadId)

                        query.get().addOnSuccessListener { documents ->
                            for (document in documents) {
                                val documentId = document.id

                                val atributoArray = FieldValue.arrayUnion(valoracionId)
                                collectionRef3.document(documentId)
                                    .update("valoracions", atributoArray)
                                    .addOnSuccessListener {
                                        println("ID de valoración agregado al usuarioEntitat correctamente")
                                    }
                                    .addOnFailureListener { exception ->
                                        println("Error al agregar el ID de valoración al usuarioEntitat: $exception")
                                    }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        println("Error al añadir la valoración: $exception")
                    }
            }
        }
        .addOnFailureListener { exception ->
            println("Error al obtener el ID de la entidad: $exception")
        }
}


fun obtenerFechaActualString(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val fechaActual = Date()
    return dateFormat.format(fechaActual)
}


