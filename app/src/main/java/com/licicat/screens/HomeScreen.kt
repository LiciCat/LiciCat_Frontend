package com.licicat.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.example.licicat.Licitacio
import com.google.maps.android.compose.GoogleMap
import com.licicat.LicitacionsRepository
import com.licicat.components.BottomBarNavigation
import com.licicat.components.CardLicitacio

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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




var licitacions: List<Licitacio>? = null

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    // Crea una còpia addicional de la llista de licitacions original
    val originalLicitacions = remember { mutableStateOf(emptyList<Licitacio>()) }
    var licitacions by remember { mutableStateOf(emptyList<Licitacio>()) }
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

            Column() {

                var searchText by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Cerca") },
                    modifier = Modifier
                        .fillMaxWidth()
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
                            licitacions = originalLicitacions.value.filter {
                                it.nom_organ?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false ||
                                it.nom_ambit?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false ||
                                it.nom_departament_ens?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false ||
                                it.nom_unitat?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false ||
                                it.tipus_contracte?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false ||
                                it.subtipus_contracte?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false ||
                                it.denominacio?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false ||
                                it.objecte_contracte?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false ||
                                it.lloc_execucio?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false ||
                                it.descripcio_lot?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false ||
                                it.denominacio_adjudicatari?.toLowerCase()?.contains(searchText.toLowerCase()) ?: false
                            }
                        }
                    ),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        cursorColor = Color.Black
                    )
                )

                LazyColumn {
                    items(items = licitacions) { licitacio ->
                        CardLicitacio(
                            icon = Icons.Filled.AccountCircle,
                            title = licitacio.nom_organ,
                            description = licitacio.objecte_contracte,
                            date = licitacio.termini_presentacio_ofertes.toString(),
                            price = licitacio.pressupost_licitacio_asString+"€",
                            navController = navController, // Nuevo parámetro agregado
                            location = licitacio.lloc_execucio // ubicación de la licitación
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
        originalLicitacions.value = licitacionsData.value ?: emptyList()
        licitacions = originalLicitacions.value
        isLoading.value = false
    }
}










