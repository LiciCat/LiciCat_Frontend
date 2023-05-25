package com.licicat.screens

import android.annotation.SuppressLint
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
import com.licicat.navigation.AppScreens


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ChatScreen(navController: NavController) {

    var chats by remember { mutableStateOf(emptyList<Chat>()) }


    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController)
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
                    value = "",
                    onValueChange = {},
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
                            id_docEmpresa = chat.id_docEmpresa,
                            id_docEntitat = chat.id_docEntitat,
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(true) {
        val db = Firebase.firestore
        val current_user = FirebaseAuth.getInstance().currentUser
        db.collection("usersEmpresa")
            .whereEqualTo("user_id", current_user?.uid)
            .get()
            .addOnSuccessListener { documents ->
                val chatList = mutableListOf<Chat>()
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
                                    id_docEmpresa = chatData["id_docEmpresa"] as? String ?: "",
                                    id_docEntitat = chatData["id_docEntitat"] as? String ?: ""
                                )
                                chatList.add(chat)
                            }
                            chats = chatList
                        }
                }
            }
    }
}





