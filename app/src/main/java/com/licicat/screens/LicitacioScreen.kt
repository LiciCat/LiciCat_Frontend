package com.licicat.screens

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.example.licicat.Licitacio
import com.licicat.LicitacionsRepository
import com.licicat.components.BottomBarNavigation
import com.licicat.components.CardLicitacio
import com.licicat.navigation.AppScreens
import java.time.LocalDate


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable


fun LicitacioScreen(navController: NavController) {
    val isLoading = remember { mutableStateOf(true) }
    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController)
        }
    ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                PerfilCard(text = "Ajuntament de Girona")
                DescripcionCard(
                    title = "Descripció:",
                    description = "Suport a la gestió del programa Patis d'escola bressol municipal oberts al barri"
                )
                DescripcionCard(
                    title = "Objecte del Contracte:",
                    description = "L'objecte del contracte són els serveis de suport a la gestió del programa Patis d'escola bressol oberts al barri."
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        smallCard(
                            title = "Pressupost:",
                            description = "5.387.782€"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        smallCard(
                            title = "Lloc execució:",
                            description = "Girona"
                        )
                    }
                }
                DescripcionCard(
                    title = "",
                    description = ""
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f)) {
                        RoundedButton(
                            text = "Obrir Chat"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        RoundedButton(
                            text = "Optar"
                        )
                    }
                }
            }
        }
    }


@Composable
fun PerfilCard(text: String) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6
            )
        }
    }
}

@Composable
fun DescripcionCard(title: String, description: String) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = 8.dp

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = description,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun smallCard(title: String, description: String) {
    Card(
        modifier = Modifier
            .padding(16.dp),
        elevation = 8.dp

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = description,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.body1
            )
        }
    }
}
@Composable
fun RoundedButton(text: String) {
val contextForToast = LocalContext.current.applicationContext


Button(
    modifier = Modifier
        .padding(16.dp),
onClick = {
    Toast.makeText(contextForToast, "Clicked!", Toast.LENGTH_SHORT).show()
},
shape = RoundedCornerShape(size = 20.dp),
) {
    Text(text = text)
}
}

@Composable
fun TwoButtonsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { /* Acción del primer botón */ },
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Abrir chat")
        }
        Button(
            onClick = { /* Acción del segundo botón */ },
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Optar")
        }
    }
}


/*
fun DetalleLicitacionScreen(navController: NavController, licitacio: Licitacio) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = licitacio.nom_organ) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Descripción
                Card(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Descripción",
                            style = MaterialTheme.typography.h6
                        )
                        Text(
                            text = licitacio.objecte_contracte,
                            style = MaterialTheme.typography.body1
                        )
                    }
                }

                // Fecha y precio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Fecha",
                                style = MaterialTheme.typography.h6
                            )
                            Text(
                                text = licitacio.termini_presentacio_ofertes.toString(),
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }

                    Card(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Precio",
                                style = MaterialTheme.typography.h6
                            )
                            Text(
                                text = licitacio.pressupost_licitacio_asString+"€",
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                }
            }
        }
    )
}
 */



