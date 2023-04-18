package com.licicat.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.example.licicat.Licitacio
import com.licicat.LicitacionsRepository
import com.licicat.components.BottomBarNavigation
import com.licicat.components.CardLicitacio


var licitacions: List<Licitacio>? = null

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    val licitacions = remember { mutableStateOf(emptyList<Licitacio>()) }
    val isLoading = remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController)
        }
    ) {
        if (isLoading.value) {
            // Muestra un indicador de carga mientras se obtienen los datos
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(items = licitacions.value) { licitacio ->
                        CardLicitacio(
                            icon = Icons.Filled.AccountCircle,
                            title = licitacio.nom_organ,
                            description = licitacio.objecte_contracte,
                            date = licitacio.termini_presentacio_ofertes.toString(),
                            price = licitacio.pressupost_licitacio_asString+"€"
                        )
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






