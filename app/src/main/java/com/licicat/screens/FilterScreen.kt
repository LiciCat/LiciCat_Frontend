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



@Composable
fun FilterDropdownMenu(
    opciones: List<String>,
    opcionesSeleccionadas: List<String>,
    onOptionClick: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
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
                val isChecked = opcionesSeleccionadas.contains(opcion)
                DropdownMenuItem(onClick = {
                    onOptionClick(opcion)
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
                                    onOptionClick(opcion)
                                } else {
                                    onOptionClick(opcion)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}




@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SliderPrecio(
    precioMinimo: Float,
    precioMaximo: Float,
    onRangeChanged: (Pair<Float, Float>) -> Unit,
    modifier: Modifier = Modifier
) {
    var range by remember { mutableStateOf(precioMinimo..precioMaximo) }

    Column(modifier = modifier) {
        Text(text = "Precio: ${range.start}€ - ${range.endInclusive}€")
        RangeSlider(
            value = range,
            onValueChange = { range = it; onRangeChanged(Pair(range.start, range.endInclusive)) },
            valueRange = precioMinimo..precioMaximo,
            steps = 100,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun FilterButtons(
    onApplyFilter: (String?, Pair<Float, Float>?) -> Unit,
    onDismiss: () -> Unit,
    opcionesSeleccionadas: List<String>,
    rangoPrecio: Pair<Float, Float>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier.padding(8.dp),
            onClick = {
                onApplyFilter(opcionesSeleccionadas.joinToString(", "), rangoPrecio)
                onDismiss()
            }

        ) {
            Text(text = rangoPrecio.first.toString() + "-" + rangoPrecio.second.toString())
            Text(text = "Aplicar filtre")
        }

        Button(
            modifier = Modifier.padding(8.dp),
            onClick = {
                onApplyFilter(null, null)
                onDismiss()
            }

        ) {
            Text(text = "Cancel·lar")
        }
    }
}

@Composable
fun PantallaSeleccion(onApplyFilter: (String?, Pair<Float, Float>?) -> Unit, onDismiss: () -> Unit) {
    val opciones = listOf("Barcelona", "Catalunya", "Tarragona", "Espanya", "Girona")
    val opcionesSeleccionadas = remember { mutableStateOf(emptyList<String>()) }
    val rangoPrecios = remember { mutableStateOf(Pair(0f, 5000000f)) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        SliderPrecio(precioMinimo = 0f, precioMaximo = 5000000f , onRangeChanged = {
            rangoPrecios.value = it
        })
        FilterDropdownMenu(
            opciones = opciones,
            opcionesSeleccionadas = opcionesSeleccionadas.value,
            onOptionClick = {
                if (opcionesSeleccionadas.value.contains(it)) {
                    opcionesSeleccionadas.value = opcionesSeleccionadas.value.filter { selected -> selected != it }
                } else {
                    opcionesSeleccionadas.value = opcionesSeleccionadas.value + it
                }
            },
            onDismissRequest = { }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FilterButtons(
            onApplyFilter = onApplyFilter,
            onDismiss = onDismiss,
            opcionesSeleccionadas = opcionesSeleccionadas.value,
            rangoPrecio = rangoPrecios.value
        )
    }
}




