package com.licicat.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.licicat.Licitacio
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.licicat.components.BottomBarNavigation
import com.licicat.model.Chat
import com.licicat.model.Message
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import androidx.compose.foundation.lazy.items

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun WhatsScreen(navController: NavController, chat_id: String, id_docEmpresa: String, id_docEntitat: String, info: String) {
    var missatges by remember { mutableStateOf(emptyList<Message>()) }
    var messagesObtained by remember { mutableStateOf(false) }

    val db = Firebase.firestore
    chat_id?.let {
        AppDescription2.chat_id = it
    }

    val chatRef = db.collection("chats").document(chat_id)



    LaunchedEffect(Unit) {
        if (!messagesObtained) {
            chatRef.collection("messages").orderBy("dob", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { messages ->
                    val newMessages = mutableListOf<Message>()
                    for (message in messages) {
                        val dob = message.get("dob") as? Timestamp
                        var date = Date();
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val dobString = dob?.let { timestamp ->
                            date = Date(timestamp.seconds * 1000)
                            dateFormat.format(date)
                        } ?: ""
                        val from = message.get("from") as? String ?: ""
                        val messageText = message.get("message") as? String ?: ""

                        val missatge = Message(messageText, from, date)
                        newMessages.add(missatge)
                    }
                    missatges = newMessages
                    messagesObtained = true
                }
        }
    }

    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController)
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Parte superior: Nombre de la persona con la que se está hablando
            TopAppBar(
                title = {
                    Text(
                        text = "Nombre de la persona", // Aquí deberías obtener el nombre de la entidad a partir de id_docEntitat
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                },
                modifier = Modifier.padding(16.dp)
            )

            // Lista de mensajes
            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = true,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {items(missatges) { missatge ->
                // Aquí puedes definir el diseño y la representación de cada mensaje en pantalla
                Text(text = missatge.message)
                Text(text = missatge.from)
                }
            }


            // Campo de texto para escribir mensajes y botón de enviar
            val textFieldValue = remember { mutableStateOf("") }

            fun onTextFieldValueChanged(newValue: String) {
                textFieldValue.value = newValue
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 60.dp, top = 8.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = textFieldValue.value,
                        onValueChange = { onTextFieldValueChanged(it) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        placeholder = {
                            Text(text = "Escribe un mensaje...")
                        }
                    )
                    Button(
                        onClick = { sendMessage(textFieldValue.value); textFieldValue.value = "" },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(text = "Enviar")
                    }
                }
            }
        }
    }
}

object AppDescription2 {
    var chat_id: String = ""
}

fun sendMessage(message_chat: String) {
    val db = Firebase.firestore
    val current_user = FirebaseAuth.getInstance().currentUser
    val user = current_user?.uid.toString()
    val message = Message(
        message = message_chat,
        from = user
    )
    db.collection("chats").document(AppDescription2.chat_id).collection("messages").document().set(message)

}