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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import com.google.firebase.messaging.RemoteMessage.Notification.*

import com.licicat.components.BottomBarNavigationEntitat

import com.licicat.*




@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ValoracioEntitatScreen(navController: NavController, intent: Intent) {
    val ratingItems = remember {
        mutableStateListOf(
            RatingItem("Amabilidad"),
            RatingItem("Rapidez"),
            RatingItem("Buena Comunicación")
            // Agrega más elementos de valoración según tus necesidades
        )
    }

    val averageRatingState = remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }
    var backgroundColor by remember { mutableStateOf(Color(android.graphics.Color.rgb(219, 41, 59))) }
    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController)
        }
    ) {

        Column(){
            Column(modifier = Modifier
                .fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
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
                                .padding(vertical = 8.dp),
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
                    label = { Text("Comentario adicional (opcional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )

                // Botón de enviar
                Button(
                    onClick = { },
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
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(text = ratingItem.title)

        Spacer(modifier = Modifier.width(16.dp))

        RatingBar(
            rating = ratingItem.rating,
            onRatingChanged = onRatingChanged
        )
    }
}


@Composable
fun RatingBar(rating: Float, onRatingChanged: (Float) -> Unit) {
    val maxRating = 5 // Número máximo de estrellas
    val selectedRating = remember { mutableStateOf(rating) }

    Row(verticalAlignment = Alignment.CenterVertically) {
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


