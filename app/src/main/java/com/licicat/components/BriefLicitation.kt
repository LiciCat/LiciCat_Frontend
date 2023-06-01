package com.licicat.components


import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.widget.Toast
import java.io.IOException
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.licicat.navigation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Log
import androidx.compose.foundation.clickable
import com.licicat.AppType
import com.licicat.UserType
import kotlin.random.Random


fun findUbicacio(context: Context, location: String): Boolean {
    try {
        val geocoder = Geocoder(context)
        val results = location?.let { geocoder.getFromLocationName(it, 1) }
        if (results != null && results.isNotEmpty()) return true
        return false
    }
    catch (e: IOException){
        return false
    }
    catch (e: IllegalArgumentException) {
        return false
    }
}

@Composable
fun CardLicitacio(
    icon: ImageVector,
    title: String?,
    description: String?,
    date: String?,
    price: String?,
    navController: NavController,
    location: String?,
    denomination: String?,
    date_inici: String?,
    date_adjudicacio: String?,
    tipus_contracte: String?,
    enllac_publicacio: String?
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable (onClick = {
                val encodedEnlace = Uri.encode(enllac_publicacio)
                val data = date?.replace("/","")
                navController.navigate(AppScreens.withArgs(location,title,description,price,denomination,encodedEnlace,data))}),
        elevation = 8.dp
    ) {
        Log.d("app", "enllac_publicacio:"+ enllac_publicacio)
        var isFavorite by remember { mutableStateOf(false) }
        var id_lic by remember { mutableStateOf(Random.nextLong(1, 2147483647).toInt()) }
        val db = Firebase.firestore
        val current_user = FirebaseAuth.getInstance().currentUser

        db.collection("licitacionsFavorits")
            .whereEqualTo("title", title)
            .whereEqualTo("description", description)
            .whereEqualTo("date", date)
            .whereEqualTo("price", price)
            .whereArrayContains("users_ids", current_user?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    isFavorite = true
                }
                for (document in documents) {
                    id_lic = document.getLong("lic_id")?.toInt() ?: 0
                }
            }
            .addOnFailureListener { exception ->
                Log.w("app", "Error getting documents: ", exception)
            }
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Icono de usuario",
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center),
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(16.dp))
                if (title != null) {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(align = Alignment.CenterHorizontally)
                    )
                }
                if (location != null) {
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        IconButton(
                            onClick = {
                                if (findUbicacio(navController.context, location)) {
                                    navController.navigate(AppScreens.Args(location, title))
                                } else {
                                    Toast.makeText(
                                        navController.context,
                                        "Licitacio no disponible en el mapa.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(
                                painter = painterResource(id = AppScreens.MapScreen.icon),
                                contentDescription = AppScreens.MapScreen.title
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    if (AppType.getUserType() == UserType.EMPRESA) {
                        IconButton(
                            onClick = {
                                if (isFavorite) {
                                    db.collection("licitacionsFavorits")
                                        .whereEqualTo("title", title)
                                        .whereEqualTo("description", description)
                                        .whereEqualTo("date", date)
                                        .whereEqualTo("price", price)
                                        .whereArrayContains(
                                            "users_ids",
                                            current_user?.uid.toString()
                                        )
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            for (document in documents) {
                                                val usersIds =
                                                    document.get("users_ids") as MutableList<String>
                                                usersIds.remove(current_user?.uid.toString())
                                                document.reference.update("users_ids", usersIds)
                                            }
                                        }
                                    db.collection("usersEmpresa")
                                        .whereEqualTo("user_id", current_user?.uid)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            for (document in documents) {
                                                val favsIds =
                                                    document.get("favorits") as MutableList<Int>
                                                val newfavsIds =
                                                    favsIds.filter { it != id_lic.toInt() }
                                                document.reference.update("favorits", newfavsIds)
                                            }
                                        }
                                } else {
                                    db.collection("licitacionsFavorits")
                                        .whereEqualTo("title", title)
                                        .whereEqualTo("description", description)
                                        .whereEqualTo("date", date)
                                        .whereEqualTo("price", price)
                                        .get()
                                        .addOnSuccessListener { documents ->
                                            if (!documents.isEmpty) {
                                                for (document in documents) {
                                                    val usersIds =
                                                        document.get("users_ids") as MutableList<String>
                                                    usersIds.add(current_user?.uid.toString())
                                                    document.reference.update("users_ids", usersIds)
                                                }
                                            } else {
                                                val users = listOf(current_user?.uid.toString())
                                                id_lic = Random.nextLong(1, 2147483647).toInt()
                                                val data = hashMapOf(
                                                    "title" to title,
                                                    "description" to description,
                                                    "date" to date,
                                                    "price" to price,
                                                    "lic_id" to id_lic,
                                                    "location" to location,
                                                    "denomination" to denomination,
                                                    "date_inici" to date_inici,
                                                    "date_adjudicacio" to date_adjudicacio,
                                                    "tipus_contracte" to tipus_contracte,
                                                    "users_ids" to users,
                                                    "enllac_publicacio" to enllac_publicacio,
                                                )
                                                db.collection("licitacionsFavorits").add(data)
                                            }

                                            db.collection("usersEmpresa")
                                                .whereEqualTo("user_id", current_user?.uid)
                                                .get()
                                                .addOnSuccessListener { documents ->
                                                    for (document in documents) {

                                                        println("id_lic es igual a $id_lic")

                                                        val favsIds =
                                                            document.get("favorits") as MutableList<Int>
                                                        favsIds.add(id_lic)
                                                        document.reference.update(
                                                            "favorits",
                                                            favsIds
                                                        )
                                                    }
                                                }
                                        }

                                }
                                isFavorite = !isFavorite
                            },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Botón de favorito"
                            )
                        }
                    }
                }
            }
            if (denomination != null) {
                Text(
                    text = denomination,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (date != null) {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.body2
                    )
                }
                if (price != null) {
                    Text(
                        text = price,
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold
                    )
                    if(location != null) {
                        Text(
                            text = location,
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


