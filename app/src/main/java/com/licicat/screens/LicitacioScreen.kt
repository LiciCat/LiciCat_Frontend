package com.licicat.screens


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.licicat.R
import com.licicat.components.BottomBarNavigation
import com.licicat.model.Chat
import com.licicat.navigation.AppScreens
import java.util.*

import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay

import com.licicat.AppType
import com.licicat.UserType
import com.licicat.components.BottomBarNavigation
import com.licicat.components.BottomBarNavigationEntitat



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LicitacioScreen(navController: NavController, location:String?, title:String?, description:String?, price:String?, denomination:String?, enllac_publicacio:String?) {

    description?.let {
        AppDescription.description = it
    }

    title?.let {
        AppDescription_title.title = it
    }


    val db = Firebase.firestore
    var existeix_entitat by remember { mutableStateOf(false) }
    db.collection("usersEntitat")
        .whereEqualTo("entitat", title)
        .get()
        .addOnSuccessListener { entitatDocuments ->
            for (entitatDocument in entitatDocuments) {
                existeix_entitat = true;
            }
        }
    Scaffold(
        bottomBar = {
            if (AppType.getUserType() == UserType.EMPRESA) BottomBarNavigation(navController)
            //else if (AppType.getUserType() == UserType.UNKNOWN) BottomBarNavigation(navController)
            else if (AppType.getUserType() == UserType.ENTITAT) BottomBarNavigationEntitat(navController)
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (title != null) {
                        PerfilCard(navController = navController, text = title, existeix = existeix_entitat)
                    }

                }
            }
            if (!existeix_entitat) {
                item{
                    Text(
                        text = "Entitat no registrada",
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 16.dp)
                    )
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
            item    {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(align = Alignment.Bottom)
                        .fillMaxWidth()
                        .wrapContentWidth(align = Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!existeix_entitat) {
                        Text(
                            text = "No es pot crear xat. No està registrada l'entitat",
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 70.dp)
                ) {
                    ObrirChatButton(navController = navController, existeix_entitat = existeix_entitat)
                    Spacer(modifier = Modifier.width(16.dp))
                    if (AppType.getUserType() == UserType.EMPRESA){
                        MyButton(enllac_publicacio)
                    }
                    Spacer(modifier = Modifier.width(16.dp)) // Espacio horizontal entre botones
                    MyShareButton(enllac_publicacio)

                }
            }

        }
    }
}

@Composable
fun MyButton(enllac_publicacio: String?) {
    val context = LocalContext.current
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(enllac_publicacio)) }

    Button(onClick = { context.startActivity(intent) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.DarkGray,
            contentColor = Color.White)
    ) {
        Text(text = "Optar")
    }
}

@Composable
fun ObrirChatButton(navController: NavController, existeix_entitat: Boolean) {
    val context = LocalContext.current

    IconButton(onClick = {
        if (existeix_entitat) {
            crea_chat(navController) {
                navController.navigate(route = AppScreens.ChatScreen.route)
            }
        }
        else {
        }
    }
    ) {
        Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = "Abrir Chat",
            tint = Color.DarkGray
        )
    }
}

@Composable
fun MyShareButton(enlacePublicacion: String?) {
    val context = LocalContext.current
    val intent = remember {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, enlacePublicacion)
        }
    }

    IconButton(onClick = { context.startActivity(Intent.createChooser(intent, "Compartir enlace")) }) {
        Icon(imageVector = Icons.Filled.Share, contentDescription = "Compartir", tint = Color.DarkGray)
    }
}

object AppDescription {
    var description: String = ""
}

object AppDescription_title {
    var title: String = ""
}

fun crea_chat(navController: NavController,onComplete: () -> Unit) {
    val db = Firebase.firestore
    val current_user = FirebaseAuth.getInstance().currentUser
    val nomEntitat = AppDescription_title.title
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
                                .whereEqualTo("id_Empresa", current_user?.uid)
                                .whereEqualTo("id_docEntitat", id_doc_entitat)
                                .whereEqualTo("info", info)
                                .get()
                                .addOnSuccessListener { chatDocuments ->
                                    if (chatDocuments.isEmpty) {
                                        val chatId = UUID.randomUUID().toString()
                                        val chat = Chat(
                                            id = chatId,
                                            name = "$nomEntitat",
                                            info = info,
                                            id_Empresa = current_user?.uid.toString(),
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
fun PerfilCard(navController: NavController ,text: String, existeix: Boolean) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = existeix) {
                if (existeix) {
                    navController.navigate(AppScreens.Args2(text))
                }
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



