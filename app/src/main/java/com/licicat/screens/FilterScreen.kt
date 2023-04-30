package com.licicat.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.licicat.components.BottomBarNavigation
import com.licicat.navigation.AppScreens



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PantallaSeleccion(onApplyFilter: (String?) -> Unit, onDismiss: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val opciones = listOf("Barcelona", "Catalunya", "Tarragona", "Espanya", "Girona")
    val opcionesSeleccionadas = remember { mutableStateOf(emptyList<String>()) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Text(text = "Filtros", fontSize = 20.dp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "Selecciona una ubicación:",
                modifier = Modifier.clickable(onClick = { expanded = true })
            )
            Spacer(modifier = Modifier.width(16.dp))
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(200.dp)
            ) {
                opciones.forEach { opcion ->
                    val isChecked = opcionesSeleccionadas.value.contains(opcion)
                    DropdownMenuItem(onClick = {
                        if (isChecked) {
                            opcionesSeleccionadas.value =
                                opcionesSeleccionadas.value.filter { it != opcion }
                        } else {
                            opcionesSeleccionadas.value += opcion
                        }
                    }) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(opcion)
                            Checkbox(
                                checked = isChecked,
                                modifier = Modifier.padding(end = 16.dp),
                                colors = CheckboxDefaults.colors(Color.Black),
                                onCheckedChange = {
                                    if (isChecked) {
                                        opcionesSeleccionadas.value =
                                            opcionesSeleccionadas.value.filter { it != opcion }
                                    } else {
                                        opcionesSeleccionadas.value += opcion
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row( modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    onApplyFilter(opcionesSeleccionadas.value.joinToString(", "))
                    onDismiss()
                }

            ) {
                Text(text = "Aplicar filtre")
            }

        Button(
            modifier = Modifier.padding(8.dp),
            onClick = {
                onApplyFilter(null)
                onDismiss()
            }

        ) {
            Text(text = "Cancel·lar")
        }
    }
    }
}



