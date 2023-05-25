package com.licicat.screens


import android.annotation.SuppressLint
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.licicat.components.BottomBarNavigation
import com.licicat.model.Chat
import com.licicat.navigation.AppScreens
import java.util.*


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LicitacioScreen(navController: NavController, location:String?, title:String?, description:String?, price:String?) {

    description?.let {
        AppDescription.description = it
    }

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
                        onClick = {
                            Log.d("App","Entro a clicar")
                            crea_chat(navController){
                                Log.d("App","Entro a navegar")
                                navController.navigate(route = AppScreens.ChatScreen.route)
                                Log.d("App","Salgo de navegar")
                            }
                        }
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

object AppDescription {
    var description: String = ""
}

fun crea_chat(navController: NavController,onComplete: () -> Unit) {
    val db = Firebase.firestore
    val current_user = FirebaseAuth.getInstance().currentUser
    val nomEntitat = "Generalitat de Catalunya"
    val info = AppDescription.description
        db.collection("usersEmpresa")
            .whereEqualTo("user_id", current_user?.uid)
            .get()
            .addOnSuccessListener { empresaDocuments ->
                for (empresaDocument in empresaDocuments) {
                    val id_doc_user = empresaDocument.id

                    db.collection("usersEntitat")
                        .whereEqualTo("entitat", nomEntitat)
                        .get()
                        .addOnSuccessListener { entitatDocuments ->
                            for (entitatDocument in entitatDocuments) {
                                val id_doc_entitat = entitatDocument.id

                                // Verificar si ya existe un chat entre los mismos usuarios
                                db.collection("chats")
                                    .whereEqualTo("id_docEmpresa", id_doc_user)
                                    .whereEqualTo("id_docEntitat", id_doc_entitat)
                                    .whereEqualTo("info", info)
                                    .get()
                                    .addOnSuccessListener { chatDocuments ->
                                        if (chatDocuments.isEmpty) {
                                            val chatId = UUID.randomUUID().toString()
                                            val chat = Chat(
                                                id = chatId,
                                                name = "Xat amb $nomEntitat",
                                                info = info,
                                                id_docEmpresa = id_doc_user,
                                                id_docEntitat = id_doc_entitat
                                            )

                                            db.collection("chats").document(chatId).set(chat)
                                            db.collection("usersEmpresa").document(id_doc_user)
                                                .collection("chats").document(chatId).set(chat)
                                            db.collection("usersEntitat").document(id_doc_entitat)
                                                .collection("chats").document(chatId).set(chat)
                                            onComplete()
                                        } else onComplete()

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