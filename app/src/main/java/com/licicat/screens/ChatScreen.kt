package com.licicat.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController

import com.example.licicat.Licitacio
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.licicat.LicitacionsRepository
import com.licicat.R
import com.licicat.components.BottomBarNavigation
import com.licicat.components.CardChat
import com.licicat.components.CardLicitacio
import com.licicat.model.Chat

import com.licicat.AppType
import com.licicat.UserType
import com.licicat.components.BottomBarNavigation
import com.licicat.components.BottomBarNavigationEntitat

import com.licicat.navigation.AppScreens




@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ChatScreen(navController: NavController) {

    var textFieldValue by remember { mutableStateOf("") }
    var chats by remember { mutableStateOf(emptyList<Chat>()) }

    val db = Firebase.firestore
    val current_user = FirebaseAuth.getInstance().currentUser
    val chatList = mutableListOf<Chat>()

        db.collection("usersEmpresa")
            .whereEqualTo("user_id", current_user?.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userId = document.id
                    db.collection("usersEmpresa").document(userId).collection("chats")
                        .get()
                        .addOnSuccessListener { chatDocuments ->
                            for (chatDocument in chatDocuments) {
                                val chatData = chatDocument.data
                                val chat = Chat(
                                    id = chatData["id"] as? String ?: "",
                                    name = chatData["name"] as? String ?: "",
                                    info = chatData["info"] as? String ?: "",
                                    id_Empresa = chatData["id_Empresa"] as? String ?: "",
                                    id_docEntitat = chatData["id_docEntitat"] as? String ?: ""
                                )
                                chatList.add(chat)
                            }
                            if (chatList.isNotEmpty()) chats = chatList
                        }
                }
            }
        db.collection("usersEntitat")
            .whereEqualTo("user_id", current_user?.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userId = document.id
                    db.collection("usersEntitat").document(userId).collection("chats")
                        .get()
                        .addOnSuccessListener { chatDocuments ->
                            for (chatDocument in chatDocuments) {
                                val chatData = chatDocument.data
                                val chat = Chat(
                                    id = chatData["id"] as? String ?: "",
                                    name = chatData["name"] as? String ?: "",
                                    info = chatData["info"] as? String ?: "",
                                    id_Empresa = chatData["id_Empresa"] as? String ?: "",
                                    id_docEntitat = chatData["id_docEntitat"] as? String ?: ""
                                )
                                chatList.add(chat)
                            }
                            if (chatList.isNotEmpty()) chats = chatList

                }
            }
        }
    Scaffold(
        bottomBar = {
            if (AppType.getUserType() == UserType.EMPRESA) BottomBarNavigation(navController)
            //else if (AppType.getUserType() == UserType.UNKNOWN) BottomBarNavigation(navController)
            else if (AppType.getUserType() == UserType.ENTITAT) BottomBarNavigationEntitat(navController)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Xats",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = 0.1.em
                    ),
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Start)
                )

                TextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        textFieldValue = newValue
                        chats_buscados(textFieldValue) { updatedChats ->
                            chats = updatedChats
                        }
                    },
                    placeholder = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.background(Color.White, RoundedCornerShape(16.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                modifier = Modifier.padding(start = 0.dp, end = 8.dp)
                            )
                            Text(
                                text = "Buscar",
                                color = Color.Gray,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                LazyColumn {
                    items(items = chats) { chat ->
                        CardChat(
                            chat_id = chat.id,
                            name = chat.name,
                            info = chat.info,
                            id_Empresa = chat.id_Empresa,
                            id_docEntitat = chat.id_docEntitat,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

fun chats_buscados(buscador: String, onSuccess: (List<Chat>) -> Unit) {
    val db = Firebase.firestore
    val current_user = FirebaseAuth.getInstance().currentUser
    val chatList = mutableListOf<Chat>()

    if (buscador.isEmpty()) {
        db.collection("usersEmpresa")
            .whereEqualTo("user_id", current_user?.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userId = document.id
                    db.collection("usersEmpresa").document(userId).collection("chats")
                        .get()
                        .addOnSuccessListener { chatDocuments ->
                            for (chatDocument in chatDocuments) {
                                val chatData = chatDocument.data
                                val chat = Chat(
                                    id = chatData["id"] as? String ?: "",
                                    name = chatData["name"] as? String ?: "",
                                    info = chatData["info"] as? String ?: "",
                                    id_Empresa = chatData["id_Empresa"] as? String ?: "",
                                    id_docEntitat = chatData["id_docEntitat"] as? String ?: ""
                                )
                                chatList.add(chat)
                            }
                                onSuccess(chatList)
                        }
                }
            }
        db.collection("usersEntitat")
            .whereEqualTo("user_id", current_user?.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userId = document.id
                    db.collection("usersEntitat").document(userId).collection("chats")
                        .get()
                        .addOnSuccessListener { chatDocuments ->
                            for (chatDocument in chatDocuments) {
                                val chatData = chatDocument.data
                                val chat = Chat(
                                    id = chatData["id"] as? String ?: "",
                                    name = chatData["name"] as? String ?: "",
                                    info = chatData["info"] as? String ?: "",
                                    id_Empresa = chatData["id_Empresa"] as? String ?: "",
                                    id_docEntitat = chatData["id_docEntitat"] as? String ?: ""
                                )
                                chatList.add(chat)
                            }
                                onSuccess(chatList)
                        }
                }
            }
    } else {
        db.collection("usersEmpresa")
            .whereEqualTo("user_id", current_user?.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userId = document.id
                    db.collection("usersEmpresa").document(userId).collection("chats")
                        .get()
                        .addOnSuccessListener { chatDocuments ->
                            for (chatDocument in chatDocuments) {
                                val chatData = chatDocument.data
                                val name = chatData["name"] as? String ?: ""

                                // Verificar si el nombre contiene las letras del buscador (ignorando mayúsculas y minúsculas)
                                if (name.contains(buscador, ignoreCase = true)) {
                                    val chat = Chat(
                                        id = chatData["id"] as? String ?: "",
                                        name = name,
                                        info = chatData["info"] as? String ?: "",
                                        id_Empresa = chatData["id_Empresa"] as? String ?: "",
                                        id_docEntitat = chatData["id_docEntitat"] as? String ?: ""
                                    )
                                    chatList.add(chat)
                                }
                            }
                            onSuccess(chatList)
                        }
                }
            }
        db.collection("usersEntitat")
            .whereEqualTo("user_id", current_user?.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userId = document.id
                    db.collection("usersEntitat").document(userId).collection("chats")
                        .get()
                        .addOnSuccessListener { chatDocuments ->
                            for (chatDocument in chatDocuments) {
                                val id_Empresa = chatDocument["id_Empresa"] as? String ?: ""

                                db.collection("usersEmpresa")
                                    .whereEqualTo("user_id", id_Empresa)
                                    .get()
                                    .addOnSuccessListener { empresaDocuments ->
                                        for (empresaDocument in empresaDocuments) {
                                            val DocUserID = empresaDocument.id
                                            val name_empresa = empresaDocument.get("empresa") as String
                                            if (name_empresa.contains(
                                                    buscador,
                                                    ignoreCase = true
                                                )
                                            ) {
                                                db.collection("usersEmpresa").document(DocUserID)
                                                    .collection("chats")
                                                    .get()
                                                    .addOnSuccessListener { Docs ->
                                                        for (Doc in Docs) {
                                                            val chat = Chat(
                                                                id = Doc["id"] as? String
                                                                    ?: "",
                                                                name = Doc["name"] as? String
                                                                    ?: "",
                                                                info = Doc["info"] as? String
                                                                    ?: "",
                                                                id_Empresa = Doc["id_Empresa"] as? String
                                                                    ?: "",
                                                                id_docEntitat = Doc["id_docEntitat"] as? String
                                                                    ?: ""
                                                            )
                                                            chatList.add(chat)
                                                        }
                                                        onSuccess(chatList)
                                                    }
                                            }
                                            else onSuccess(chatList)
                                        }
                                    }
                            }
                        }
                }
            }
    }
}





