package com.licicat.screens



import android.widget.CalendarView
import android.widget.LinearLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.material.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView



@Composable
fun FilterDropdownMenu(
    opciones: List<String>,
    opcionesSeleccionadas: List<String>,
    onOptionClick: (String) -> Unit,
    onDismissRequest: () -> Unit,
    titol: String
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        ){Text(titol)}
        Spacer(modifier = Modifier.width(16.dp))
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(400.dp)
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
        Text(text = "Precio: ${range.start}€ - ${range.endInclusive}€", modifier = Modifier.padding(16.dp))
        RangeSlider(
            value = range,
            onValueChange = { range = it; onRangeChanged(Pair(range.start, range.endInclusive)) },
            valueRange = precioMinimo..precioMaximo,
            steps = 0,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}




@Composable
fun FilterButtons(
    onApplyFilter: (String?, Pair<Float, Float>?, Int?, Int?, Int?, String?) -> Unit,
    onDismiss: () -> Unit,
    opcionesSeleccionadas: List<String>,
    rangoPrecio: Pair<Float, Float>,
    dia: Int?,
    mes: Int?,
    any: Int?,
    opcionesSeleccionadasTipus: List<String>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier.padding(8.dp),
            onClick = {
                onApplyFilter(opcionesSeleccionadas.joinToString(", "), rangoPrecio, dia, mes, any, opcionesSeleccionadasTipus.joinToString(", "))
                onDismiss()
            }) { Text(text = "Aplicar filtres") }

        Button(
            modifier = Modifier.padding(8.dp),
            onClick = {
                onApplyFilter(null, null, null, null, null, null)
                onDismiss()
            }

        ) {
            Text(text = "Cancel·lar")
        }
    }
}


@Composable
fun DatePickerDialog(onDateSelected: (year: Int, month: Int, day: Int) -> Unit) {
    var selectedYear = 0
    var selectedMonth = 0
    var selectedDay = 0
    var dialogOpen = true

    // Esta es la función que se llama cuando se pulsa el botón de "Seleccionar"
    fun selectDate() {
        onDateSelected(selectedYear, selectedMonth, selectedDay)
        dialogOpen = false
    }

    // Creamos un AlertDialog con un DatePicker
    if (dialogOpen){
        AlertDialog(
            onDismissRequest = { },
            buttons = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = {  dialogOpen = false
                        onDateSelected(0,0,0)} ,
                        modifier = Modifier.padding(8.dp)) {
                        Text(text = "Cancel·lar")
                    }
                    Button(onClick = { selectDate() },
                        modifier = Modifier.padding(8.dp)) {
                        Text(text = "Seleccionar")
                    }
                }
            },
            title = {
                Text(text = "Selecciona un limit de termini de presentació")
            },
            text = {
                AndroidView(
                    { context ->
                        LinearLayout(context).apply {
                            orientation = LinearLayout.VERTICAL
                            val calendarView = CalendarView(context)
                            addView(calendarView)

                            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                                selectedYear = year
                                selectedMonth = month
                                selectedDay = dayOfMonth
                            }
                        }
                    },
                    modifier = Modifier.wrapContentWidth(),
                    update = {}
                )
            }
        )
    }
}



//LLoc_execucio, preu_min & preu_max, dia, mes, any, Tipus_Contracte
@Composable
fun PantallaSeleccion(onApplyFilter: (String?, Pair<Float, Float>?, Int?, Int?, Int?, String?) -> Unit, onDismiss: () -> Unit) {
    val opciones = listOf("Barcelona", "Catalunya", "Tarragona", "Espanya", "Girona")
    val opcionesTipus = listOf("Serveis", "Subministraments", "Obres", "Altra legislació sectorial", "Privat d'Administració Pública", "Concessió de serveis", "Administratiu especial", "Gestió de Serveis Públics", "Privat", "Concessió d'obra pública", "Col·laboració Públic-Privat", )
    val opcionesSeleccionadas = remember { mutableStateOf(emptyList<String>()) }
    val opcionesSeleccionadasTipus = remember { mutableStateOf(emptyList<String>()) }
    val rangoPrecios = remember { mutableStateOf(Pair(0f, 5000000f)) }
    val isDatePickerOpen = remember { mutableStateOf(false) }
    val dia = remember { mutableStateOf<Int?>(null) }
    val mes = remember { mutableStateOf<Int?>(null) }
    val any = remember { mutableStateOf<Int?>(null) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)){
            Text(text = "Filtres", fontSize = 36.sp, fontWeight = FontWeight.Bold)
        }

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
            onDismissRequest = { },
            titol = "Selecciona lloc d'execució"
        )

        FilterDropdownMenu(
            opciones = opcionesTipus,
            opcionesSeleccionadas = opcionesSeleccionadasTipus.value,
            onOptionClick = {
                if (opcionesSeleccionadasTipus.value.contains(it)) {
                    opcionesSeleccionadasTipus.value = opcionesSeleccionadasTipus.value.filter { selected -> selected != it }
                } else {
                    opcionesSeleccionadasTipus.value = opcionesSeleccionadasTipus.value + it
                }
            },
            onDismissRequest = { },
            titol = "Selecciona tipus de contracte"
        )

        Spacer(modifier = Modifier.height(8.dp))
        // Botón para abrir el DatePicker
        Button(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            onClick = {
                isDatePickerOpen.value = true
            }) { Text(text = "Seleccionar data presentació") }

        // DatePickerDialog
        if (isDatePickerOpen.value) {
            DatePickerDialog(onDateSelected = { year, month, day -> dia.value = day; mes.value = month; any.value = year;
                isDatePickerOpen.value = false // Cerramos el diálogo al seleccionar la fecha
            })
        }



        FilterButtons(
            onApplyFilter = onApplyFilter,
            onDismiss = onDismiss,
            opcionesSeleccionadas = opcionesSeleccionadas.value,
            rangoPrecio = rangoPrecios.value,
            dia = dia.value,
            mes = mes.value,
            any = any.value,
            opcionesSeleccionadasTipus = opcionesSeleccionadasTipus.value
        )
    }
}



