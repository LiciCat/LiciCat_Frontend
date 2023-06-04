package com.licicat.screens

import android.annotation.SuppressLint
import android.icu.text.DecimalFormat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.licicat.AppType
import com.licicat.UserType
import com.licicat.components.BottomBarNavigation
import com.licicat.components.BottomBarNavigationEntitat
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.MutableLiveData
import com.example.licicat.Licitacio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.licicat.LicitacionsRepository
import com.licicat.components.CardLicitacio
import androidx.compose.material.icons.*

import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.concurrent.atomic.AtomicInteger

import androidx.compose.ui.res.stringResource
import com.licicat.R
import kotlinx.coroutines.delay


val licitacions_opt: MutableLiveData<List<Licitacio>> = MutableLiveData(emptyList())
var presentacio_opt =  mutableStateOf(false)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavController) {
    var licitacions by remember { mutableStateOf(emptyList<Licitacio>()) }
    var licitacionsOpt by remember { mutableStateOf(emptyList<Licitacio>()) }
    val originalLicitacions = remember { mutableStateOf(emptyList<Licitacio>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.label_perfil_screen_perfil)) },
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
        if (AppType.getUserType() == UserType.EMPRESA){


            val db = Firebase.firestore
            val current_user = FirebaseAuth.getInstance().currentUser
            val id_del_user = current_user?.uid
            var empresa by remember { mutableStateOf("") }
            var descripcio by remember { mutableStateOf("") }
            val optats = mutableListOf<Int>()
            var Noptats by remember { mutableStateOf(0) }



            LaunchedEffect(true) {
                licitacions_opt.value = emptyList()
                presentacio_opt.value = false
                db.collection("usersEmpresa")
                    .whereEqualTo("user_id", id_del_user)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            Log.d("appEntitat", "${document.id} => ${document.data}")
                            empresa = empresa + document.get("empresa") as String
                            descripcio = descripcio + document.get("descripcio") as String
                            val optatsDoc = document.get("optats") as List<Long>
                            optats.addAll(optatsDoc.map { it.toInt() })
                            Noptats = optatsDoc.size
                        }

                        trobar_opt(optats)

                    }
                    .addOnFailureListener { exception ->
                        Log.w("app", "Error getting documents: ", exception)
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
                            Spacer(Modifier.width(30.dp))
                            Column(modifier = Modifier
                                .padding(8.dp)
                                .padding(start = 30.dp)) {
                                Text(
                                    text = Noptats.toString(),
                                    style = MaterialTheme.typography.h5,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(bottom = 8.dp),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Licitacions optades",
                                    style = MaterialTheme.typography.body1,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    item{

                        Text(
                            text = empresa,
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .padding(top = 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item{
                        Text(
                            text = descripcio,
                            modifier = Modifier.padding(top = 16.dp),
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold

                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                    Log.d("pressentacio", "${presentacio_opt.value}")
                   if (presentacio_opt.value == true) {

                        items(items = licitacions_opt.value ?: emptyList()) { licitacio ->
                            CardLicitacio(
                                icon = Icons.Filled.AccountCircle,
                                title = licitacio.nom_organ,
                                description = licitacio.objecte_contracte,
                                date = licitacio.termini_presentacio_ofertes.toString(),
                                price = licitacio.pressupost_licitacio_asString,
                                navController = navController, // Nuevo parámetro agregado
                                location = licitacio.lloc_execucio, // ubicación de la licitación
                                denomination = null,
                                date_inici = null,
                                date_adjudicacio = null,
                                tipus_contracte = null,
                                enllac_publicacio = licitacio.enllac_publicacio
                            )


                        }
                   } else{
                        item {
                            Text(
                                text = "No ha optat a cap licitació ",
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
        else if (AppType.getUserType() == UserType.ENTITAT){
            val db = Firebase.firestore
            val current_user = FirebaseAuth.getInstance().currentUser
            val id_del_user = current_user?.uid
            var entitat by remember { mutableStateOf("") }
            var descripcio by remember { mutableStateOf("") }
            var valoracio by remember { mutableStateOf(0F) }

            LaunchedEffect(true) {
                db.collection("usersEntitat")
                    .whereEqualTo("user_id", id_del_user)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            Log.d("appEntitat", "${document.id} => ${document.data}")
                            entitat = entitat + document.get("entitat") as String
                            descripcio = descripcio + document.get("descripcio") as String
                            valoracio =  (document.get("valoracio") as Double).toFloat()
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
                    val containsEntitat = it.nom_organ?.toLowerCase() == entitat.toLowerCase()
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
                        Spacer(Modifier.width(30.dp))
                        var licit = licitacions.size
                        Column(modifier = Modifier
                            .padding(8.dp)
                            .padding(start = 16.dp)) {
                            Text(
                                text = licit.toString(),
                                style = MaterialTheme.typography.h5,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.label_licitacions_profile_screen),
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(bottom = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = valoracio.toString(),
                                style = MaterialTheme.typography.h5,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.label_valoracio_profile),
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(bottom = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(8.dp))



                    }
                }
                item{

                    Text(
                        text = entitat,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                item{
                    Text(
                        text = descripcio,
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold

                    )
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
                            text = stringResource(R.string.label_no_licitacions_disponibles),
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
}




fun trobar_opt(numeros: List<Int>) {
    val db = Firebase.firestore
    var documentosCompletados = 0
    for (numero in numeros) {
        db.collection("licitacionsOptades")
            .whereEqualTo("lic_id", numero)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("app", "${document.id} => ${document.data}")
                    var licitacio = Licitacio()
                    licitacio.nom_organ = document.get("title") as String
                    licitacio.objecte_contracte = document.get("description") as String
                    licitacio.termini_presentacio_ofertes = document.get("date") as String
                    licitacio.pressupost_licitacio_asString = document.get("price") as String
                    licitacio.lloc_execucio = document.get("location") as String
                    licitacio.enllac_publicacio = document.get("enllac_publicacio") as String
                    val listaActual = licitacions_opt.value?.toMutableList() ?: mutableListOf()
                    listaActual.add(licitacio)
                    licitacions_opt.value = listaActual

                }
                documentosCompletados++
                if (documentosCompletados == numeros.size) {
                    presentacio_opt.value = true
                }
            }
            .addOnFailureListener { exception ->
                Log.w("app", "Error getting documents: ", exception)
            }
    }
}


