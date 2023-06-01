package com.licicat.screens

import android.annotation.SuppressLint
import android.icu.text.DecimalFormat
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.example.licicat.Licitacio
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.licicat.LicitacionsRepository
import com.licicat.components.BottomBarNavigation
import com.licicat.components.CardLicitacio
import androidx.compose.material.icons.*
import com.licicat.AppType
import com.licicat.UserType
import com.licicat.components.BottomBarNavigationEntitat
import kotlinx.coroutines.delay

var presentacioEntitat =  mutableStateOf(false)

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileEntitatScreen(navController: NavController, entitat:String?) {
    var licitacions by remember { mutableStateOf(emptyList<Licitacio>()) }
    val originalLicitacions = remember { mutableStateOf(emptyList<Licitacio>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                actions = {
                    IconButton(onClick = {  navController.navigate("configuration_screen") }) {
                        Icon(Icons.Default.Settings, contentDescription = "Ajustes")
                    }
                }
            )
        },
        bottomBar = {
            if (AppType.getUserType() == UserType.EMPRESA) BottomBarNavigation(navController)
            //else if (AppType.getUserType() == UserType.UNKNOWN) BottomBarNavigation(navController)
            else if (AppType.getUserType() == UserType.ENTITAT) BottomBarNavigationEntitat(navController)
        }
    ) {
        val db = Firebase.firestore

        presentacioEntitat.value = false

        var descripcio by remember { mutableStateOf("") }
        var valoracio by remember { mutableStateOf(0F) }
        var numSeguidors by remember { mutableStateOf(0) }

        LaunchedEffect(true) {


            db.collection("usersEntitat")
                .whereEqualTo("entitat", entitat)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("appEntitat", "${document.id} => ${document.data}")

                        descripcio = descripcio + document.get("descripcio") as String
                        valoracio =  (document.get("valoracio") as Double).toFloat()
                        numSeguidors =  (document.get("numSeguidors") as Long).toInt()
                        val decimalFormat = DecimalFormat("#.#")
                        val formattedNumber = decimalFormat.format(valoracio)
                        valoracio = formattedNumber.toFloat()
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w("app", "Error getting documents: ", exception)
                }
            val repository = LicitacionsRepository()
            val licitacionsData = MutableLiveData<List<Licitacio>>()
            licitacionsData.value = emptyList()
            licitacionsData.value = repository.getLicitacions()
            originalLicitacions.value = licitacionsData.value ?: emptyList()

            licitacions = originalLicitacions.value.filter {
                val containsEntitat = it.nom_organ?.toLowerCase() == entitat?.toLowerCase()
                containsEntitat
            }
            delay(1000)

        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 56.dp)){
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Icono de usuario",
                            modifier = Modifier
                                .size(64.dp)
                                .align(Alignment.Center),
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    var licit = licitacions.size
                    Column(modifier = Modifier.padding(8.dp).padding(start = 16.dp)) {
                        Text(
                            text = licit.toString(),
                            style = MaterialTheme.typography.h5,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Licitacions",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(bottom = 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = valoracio.toString(),
                            style = MaterialTheme.typography.h5,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Valoració",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(bottom = 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = numSeguidors.toString(),
                            style = MaterialTheme.typography.h5,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Seguidors",
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.padding(bottom = 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }


                }
            }
            item{

                if (entitat != null) {
                    Text(
                        text = entitat,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            item{
                Text(
                    text = descripcio,
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold

                )
                Button(
                    onClick = { /* Accio de seguir  */ },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .height(36.dp)
                        .width(120.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFFF454A),
                        disabledBackgroundColor = Color(0xFFF78058),
                        contentColor = Color.White,
                        disabledContentColor = Color.White

                    )
                ) {
                    Text(text = "Seguir")
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
            if (licitacions.isNotEmpty()) {

                items(items = licitacions) { licitacio ->
                    CardLicitacio(
                        icon = Icons.Filled.AccountCircle,
                        title = licitacio.nom_organ,
                        description = licitacio.objecte_contracte,
                        date = licitacio.termini_presentacio_ofertes.toString(),
                        price = licitacio.pressupost_licitacio_asString,
                        navController = navController, // Nuevo parámetro agregado
                        location = licitacio.lloc_execucio, // ubicación de la licitación
                        denomination = licitacio.denominacio,
                        date_inici = licitacio.data_publicacio_anunci,
                        date_adjudicacio = licitacio.data_publicacio_adjudicacio,
                        tipus_contracte = licitacio.tipus_contracte,
                        enllac_publicacio = licitacio.enllac_publicacio
                    )


                }
            } else{
                item {
                    Text(
                        text = "No hi ha licitacions disponibles",
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }


        }


    }
}



