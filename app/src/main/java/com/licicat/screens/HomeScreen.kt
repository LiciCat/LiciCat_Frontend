package com.licicat.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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


var licitacions: List<Licitacio>? = null

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    val licitacions = remember { mutableStateOf(emptyList<Licitacio>()) }
    val isLoading = remember { mutableStateOf(true) }
    val expanded = remember { mutableStateOf(false) }
    var filtroSeleccionado by remember { mutableStateOf<String?>(null) }
    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController)
        }
    ) {
        if (isLoading.value) {
            // Muestra un indicador de carga mientras se obtienen los datos
            CircularProgressIndicator()
        } else {
            Column (modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){
                FloatingActionButton(
                    onClick = { expanded.value = true },
                    content = { Icon(Icons.Filled.AccountCircle, contentDescription = "Filtro") },
                    modifier = Modifier.padding(16.dp)
                )
                if (expanded.value) {
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    DropdownMenuItem(onClick = { filtroSeleccionado = "Barcelona" }) {
                        Text("Barcelona")
                    }
                    DropdownMenuItem(onClick = { filtroSeleccionado = "Catalunya" }) {
                        Text("Catalunya")
                    }
                    DropdownMenuItem(onClick = { /* Filtro por Tarragona */ }) {
                        Text("Tarragona")
                    }
                    DropdownMenuItem(onClick = { /* Filtro por Espanya */ }) {
                        Text("Espanya")
                    }
                    DropdownMenuItem(onClick = { filtroSeleccionado = "Girona" }) {
                        Text("Girona")
                    }
                }
            }

                val licitacionesFiltradas = licitacions.value.filter { licitacion ->
                filtroSeleccionado == null || licitacion.lloc_execucio == filtroSeleccionado
            }



                LazyColumn {
                    items(items = licitacionesFiltradas) { licitacio ->
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










