package com.licicat.screens


import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.licicat.components.BottomBarNavigation



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable


fun LicitacioScreen(navController: NavController, location:String?, title:String?, description:String?, price:String?) {

    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController)
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (title != null) {
                        PerfilCard(navController = navController, text = title)
                    }
                }
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (description != null) {
                        DescripcionCard(
                            title = "Descripció:",
                            description = description
                        )
                    }
                }
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DescripcionCard(
                        title = "Objecte del Contracte:",
                        description = "L'objecte del contracte són els serveis de suport a la gestió..."
                    )
                }
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (price != null) {
                        smallCard(
                            title = "Pressupost:",
                            description = price,
                        )
                    }
                    if (location != null) {
                        smallCard(
                            title = "Lloc execució:",
                            description = location,
                        )
                    }
                }
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(70.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("chat_screen")
                        },
                    ) {
                        Text(text = "Obrir Chat")
                    }
                    Button(
                        onClick = { /* Acción del segundo botón */ },
                    ) {
                        Text(text = "Optar")
                    }
                }
            }
        }
    }
}


@Composable
fun PerfilCard(navController: NavController ,text: String) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                navController.navigate("profile_screen")
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
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

            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(start = 8.dp)
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
                style = MaterialTheme.typography.body1
            )
            Text(
                text = description,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.body2
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
                style = MaterialTheme.typography.body1
            )
            Text(
                text = description,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.body2
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



