package com.licicat.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.example.licicat.Licitacio
import com.google.maps.android.compose.GoogleMap
import com.licicat.LicitacionsRepository
import com.licicat.components.BottomBarNavigation
import com.licicat.components.CardLicitacio

import java.text.SimpleDateFormat
import java.util.*


var licitacions: List<Licitacio>? = null


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
        }
    }.let { filteredList ->
        if (opcionesSeleccionadasTipus.isNotEmpty()) {
            filteredList.filter { it.tipus_contracte in opcionesSeleccionadasTipus }
        } else {
            filteredList
        }
    }
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    val licitacions = remember { mutableStateOf(emptyList<Licitacio>()) }
    val isLoading = remember { mutableStateOf(true) }
    val expanded = remember { mutableStateOf(false) }
    var opcionesSeleccionadas by remember { mutableStateOf(emptyList<String>()) }
    var opcionesSeleccionadasTipus by remember { mutableStateOf(emptyList<String>()) }
    var  rangoPrecios by remember { mutableStateOf(Pair(0f,Float.MAX_VALUE)) }
    var dia by remember { mutableStateOf(0) }
    var mes by remember { mutableStateOf(0) }
    var any by remember { mutableStateOf(0) }
    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController)
        }
    ) {
        if (isLoading.value) {
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
            }
        } else {
            Column (modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally){
                if(!expanded.value) {
                    FloatingActionButton(
                    onClick = { expanded.value = true },
                    content = { Icon(Icons.Filled.FilterList, contentDescription = "Filtro") },
                    modifier = Modifier.padding(16.dp)
                    )
                }
                if (expanded.value) {
                    val onDismiss = { expanded.value = false }
                    PantallaSeleccion(onApplyFilter = { opciones,rango, day, month, year, opcionesTipus->
                        opcionesSeleccionadas = if (opciones?.isNotBlank() == true) opciones.split(", ") else emptyList()
                        rangoPrecios = rango ?: Pair(0f, 5000000f)
                        dia = day ?: 0
                        mes = month ?: 0
                        any = year ?: 0
                        opcionesSeleccionadasTipus = if (opcionesTipus?.isNotBlank() == true) opcionesTipus.split(", ") else emptyList()
                    }, onDismiss = onDismiss)

                }


                val fechaFormateada = formatDate(dia, mes, any)

                val licitacionsFiltradas = filtrarLicitacions(licitacions.value, opcionesSeleccionadas, rangoPrecios, fechaFormateada, opcionesSeleccionadasTipus)


                LazyColumn {
                    items(items = licitacionsFiltradas) { licitacio ->
                        CardLicitacio(
                            icon = Icons.Filled.AccountCircle,
                            title = licitacio.nom_organ,
                            description = licitacio.objecte_contracte,
                            date = licitacio.termini_presentacio_ofertes.toString(),
                            price = licitacio.pressupost_licitacio_asString + "€",
                            navController = navController, // Nuevo parámetro agregado
                            location = licitacio.lloc_execucio // ubicación de la licitación
                        )

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
        licitacions.value = licitacionsData.value ?: emptyList()
        isLoading.value = false
    }
}










