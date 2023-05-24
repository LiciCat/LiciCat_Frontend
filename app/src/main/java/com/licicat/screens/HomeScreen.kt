package com.licicat.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.example.licicat.Licitacio
import com.licicat.LicitacionsRepository
import com.licicat.components.BottomBarNavigation
import com.licicat.components.CardLicitacio
import com.licicat.Usuari
import androidx.compose.material.ExtendedFloatingActionButton

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.ui.draw.clip

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.licicat.model.usersEmpresa
import com.licicat.components.CardUsuari
import com.licicat.ui.theme.redLicicat

import java.text.SimpleDateFormat
import java.util.*




fun formatDate(day: Int, month: Int, year: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.MONTH, month)
        set(Calendar.YEAR, year)
    }
    val date = calendar.time
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(date)
}

fun filtrarLicitacions(
    licitacions: List<Licitacio>,
    opcionesSeleccionadas: List<String>,
    rangoPrecios: Pair<Float, Float>,
    fechaFormateada: String,
    opcionesSeleccionadasTipus: List<String>
): List<Licitacio> {
    return if (opcionesSeleccionadas.isNotEmpty()) {
        // Filtrar por ubicación
        licitacions.filter { it.lloc_execucio in opcionesSeleccionadas }
    } else {
        // Mostrar todas las licitaciones
        licitacions
    }.filter {
        val precio = it.pressupost_licitacio_asString?.replace(".", "")?.toFloatOrNull()
        precio != null && precio >= rangoPrecios.first && precio <= rangoPrecios.second
    }.let { filteredList ->
        if (fechaFormateada == "31/12/0002") {
            filteredList
        } else {
            filteredList.filter { it.termini_presentacio_ofertes.toString() == fechaFormateada }
            //Canviar el == per un == o <
        }
    }.let { filteredList ->
        if (opcionesSeleccionadasTipus.isNotEmpty()) {
            filteredList.filter { it.tipus_contracte in opcionesSeleccionadasTipus }
        } else {
            filteredList
        }
    }
}

fun Cerca_Usuaris(
    usuaris: List<Usuari>,
    searchText: String
): List<Usuari> {
    return usuaris.filter {
        it.empresa?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false ||
        it.email?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false ||
        it.nom_cognoms?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false ||
        it.descripcio?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false
    }
}

fun Cerca_Licitacio(
    licitacions: List<Licitacio>,
    searchText: String
): List<Licitacio> {
    return licitacions.filter {
                it.nom_organ?.toLowerCase()
                    ?.contains(searchText.toLowerCase()) ?: false ||
                it.nom_ambit?.toLowerCase()
                    ?.contains(searchText.toLowerCase()) ?: false ||
                it.nom_departament_ens?.toLowerCase()
                    ?.contains(searchText.toLowerCase()) ?: false ||
                it.nom_unitat?.toLowerCase()
                    ?.contains(searchText.toLowerCase()) ?: false ||
                it.tipus_contracte?.toLowerCase()
                    ?.contains(searchText.toLowerCase()) ?: false ||
                it.subtipus_contracte?.toLowerCase()
                    ?.contains(searchText.toLowerCase()) ?: false ||
                it.denominacio?.toLowerCase()
                    ?.contains(searchText.toLowerCase()) ?: false ||
                it.objecte_contracte?.toLowerCase()
                    ?.contains(searchText.toLowerCase()) ?: false ||
                it.lloc_execucio?.toLowerCase()
                    ?.contains(searchText.toLowerCase()) ?: false ||
                it.descripcio_lot?.toLowerCase()
                    ?.contains(searchText.toLowerCase()) ?: false ||
                it.denominacio_adjudicatari?.toLowerCase()
                    ?.contains(searchText.toLowerCase()) ?: false
    }
}

fun getUsuaris(callback: (List<Usuari>) -> Unit) {
    val listaActual: MutableList<Usuari> = mutableListOf()
    val db = Firebase.firestore
    db.collection("usersEmpresa").get()
        .addOnSuccessListener { usuaris ->
            for (u in usuaris) {
                val usu = Usuari()
                usu.user_id = u.get("user_id") as? String ?: ""
                usu.empresa = u.get("empresa") as? String ?: ""
                usu.email = u.get("email") as? String ?: ""
                usu.nif = (u.get("nif") as? String) ?: ""
                usu.telefon = (u.get("telefon") as? String) ?: ""
                usu.nom_cognoms = u.get("nom_cognoms") as? String ?: ""
                usu.descripcio = u.get("descripcio") as? String ?: ""



                listaActual.add(usu)
            }

            callback(listaActual)
        }
        .addOnFailureListener { exception ->
            Log.w("app", "Error getting documents: ", exception)
            callback(emptyList())
        }
}






@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    // Crea una còpia addicional de la llista de licitacions original
    val originalLicitacions = remember { mutableStateOf(emptyList<Licitacio>()) }
    var licitacions by remember { mutableStateOf(emptyList<Licitacio>()) }

    var originalLicitacionsSimilars = remember { mutableStateOf(emptyList<Licitacio>()) }
    var similars by remember { mutableStateOf(emptyList<Licitacio>()) }

    val selectedTabIndex = remember { mutableStateOf(0) }

    val originalUsuaris = remember { mutableStateOf(emptyList<Usuari>()) }
    var usuaris by remember { mutableStateOf(emptyList<Usuari>()) }

    val isLoading = remember { mutableStateOf(true) }

    val expanded = remember { mutableStateOf(false) }
    val expandedSimilar = remember { mutableStateOf(false) }
    var opcionesSeleccionadas by remember { mutableStateOf(emptyList<String>()) }
    var opcionesSeleccionadasTipus by remember { mutableStateOf(emptyList<String>()) }
    var rangoPrecios by remember { mutableStateOf(Pair(0f, Float.MAX_VALUE)) }
    var dia by remember { mutableStateOf(0) }
    var mes by remember { mutableStateOf(0) }
    var any by remember { mutableStateOf(0) }


    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController)
        }
    ) {
        if (isLoading.value) {
            // Muestra un indicador de carga mientras se obtienen los datos
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
        else {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {




                if (expanded.value) {
                    val onDismiss = { expanded.value = false }
                    PantallaSeleccion(onApplyFilter = { opciones, rango, day, month, year, opcionesTipus ->
                        opcionesSeleccionadas =
                            if (opciones?.isNotBlank() == true) opciones.split(", ") else emptyList()
                        rangoPrecios = rango ?: Pair(0f, 5000000f)
                        dia = day ?: 0
                        mes = month ?: 0
                        any = year ?: 0
                        opcionesSeleccionadasTipus =
                            if (opcionesTipus?.isNotBlank() == true) opcionesTipus.split(", ") else emptyList()
                    }, onDismiss = onDismiss)
                }

                val fechaFormateada = formatDate(dia, mes, any)
                println(fechaFormateada)

                licitacions = filtrarLicitacions(
                    originalLicitacions.value,
                    opcionesSeleccionadas,
                    rangoPrecios,
                    fechaFormateada,
                    opcionesSeleccionadasTipus
                )


                TabRow(
                    selectedTabIndex.value,
                ) {
                    Tab(
                        selected = selectedTabIndex.value == 0,
                        onClick = { selectedTabIndex.value = 0 }
                    ) {
                        Text("Usuaris")
                    }
                    Tab(
                        selected = selectedTabIndex.value == 1,
                        onClick = { selectedTabIndex.value = 1 }
                    ) {
                        Text("Licitacions")
                    }
                    Tab(
                        selected = selectedTabIndex.value == 2,
                        onClick = {
                            selectedTabIndex.value = 2
                        }
                    ) {
                        Text("Licitacions Similars")
                    }
                }



                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var searchText by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Cerca") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Icona de cerca"
                            )
                        },
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Default,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                // Filtra la llista originalLicitacions en funció del text de cerca
                                if (selectedTabIndex.value == 2) {
                                    similars = Cerca_Licitacio(originalLicitacionsSimilars.value, searchText)
                                }
                                if (selectedTabIndex.value == 1) {
                                    licitacions = Cerca_Licitacio(originalLicitacions.value, searchText)
                                }
                                if (selectedTabIndex.value == 0) {
                                    usuaris = Cerca_Usuaris(originalUsuaris.value, searchText)
                                }
                            }
                        ),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.White,
                            cursorColor = Color.Black
                        )
                    )

                    if (!expanded.value) {
                        ExtendedFloatingActionButton(
                            onClick = { expanded.value = true },
                            icon = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Filled.FilterList,
                                        tint = Color.White,
                                        contentDescription = "Filtro",
                                        modifier = Modifier.size(24.dp) // Ajusta la mida de l'icono
                                    )
                                }
                            },
                            modifier = Modifier.padding(16.dp),
                            backgroundColor = redLicicat,
                            text = { Text("") },
                            shape = CircleShape
                        )
                    }
                }



                LazyColumn {
                    when (selectedTabIndex.value) {
                        0 -> items(items = usuaris) { u ->
                            CardUsuari(
                                icon = Icons.Filled.AccountCircle,
                                title = u.empresa,
                                correu = u.email,
                                telefon = u.telefon
                            )
                        }

                        1 -> items(items = licitacions) { licitacio ->
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
                                tipus_contracte = licitacio.tipus_contracte
                            )
                        }

                        2 -> items(items = similars) { licitacio ->
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
    }

    LaunchedEffect(true) {
        // Inicia una coroutine para obtener los datos desde la API
        val repository = LicitacionsRepository()
        val licitacionsData = MutableLiveData<List<Licitacio>>()
        licitacionsData.value = emptyList()
        licitacionsData.value = repository.getLicitacions()
        originalLicitacions.value = licitacionsData.value ?: emptyList()
        licitacions = originalLicitacions.value


        getUsuaris { listaActual ->
            originalUsuaris.value = listaActual
            usuaris = originalUsuaris.value

        }


        RecommendationScreen(navController, originalLicitacions.value) { result ->
            // Aquí puedes usar el resultado devuelto, por ejemplo, asignarlo a una variable
            originalLicitacionsSimilars.value = result
            similars = originalLicitacionsSimilars.value
            println("aaaaaaaaaaaaaaaaa")
            println(similars.size)

        }




        isLoading.value = false
    }
}











