package com.licicat.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.licicat.navigation.AppScreens
import androidx.compose.material.icons.*
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.storage.ktx.storage
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.licicat.AppType
import com.licicat.R
import com.licicat.UserType
import com.licicat.components.BottomBarNavigationEntitat

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun WhatsScreen(navController: NavController, chat_id: String, id_Empresa: String, id_docEntitat: String, info: String) {
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        val storage = Firebase.storage
        val imageRef = storage.reference.child("images/${System.currentTimeMillis()}.jpg") // Ruta del archivo en Firebase Storage
        val selectedImageUri: Uri? = uri
        val uploadTask = imageRef.putFile(selectedImageUri!!)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            // Continuar con la tarea para obtener la URL de descarga
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                // Guardar la URL de descarga en Firestore
                guardarUrlDeDescargaEnFirestore(downloadUri.toString())
            } else {
                // Manejar el error en la carga de la imagen
            }
        }
    }


    var missatges by remember { mutableStateOf(emptyList<Message>()) }
    var messagesObtained by remember { mutableStateOf(false) }

    val db = Firebase.firestore
    chat_id?.let {
        AppDescription2.chat_id = it
    }
    val current_user = FirebaseAuth.getInstance().currentUser
    val user = current_user?.uid.toString()
    val chatRef = db.collection("chats").document(chat_id)
    var name by remember { mutableStateOf("") }
    if (user == id_Empresa) {
    LaunchedEffect(Unit) {
        chatRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    name = document.getString("name") as? String ?: ""
                }
            }
        }
    }
    else {
    LaunchedEffect(Unit) {
        db.collection("usersEmpresa")
            .whereEqualTo("user_id", id_Empresa)
            .get()
            .addOnSuccessListener { empresaDocuments ->
                for (empresaDocument in empresaDocuments) {
                    name = empresaDocument.get("empresa") as String
                }
            }
        }
    }


    LaunchedEffect(Unit) {
        if (!messagesObtained) {
            chatRef.collection("messages").orderBy("dob", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        // Manejar el error en caso de que ocurra
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val newMessages = mutableListOf<Message>()
                        for (message in snapshot.documents) {
                            val dob = message.get("dob") as? Timestamp
                            var date = Date()
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            dateFormat.timeZone = TimeZone.getTimeZone("Europe/Madrid")
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
    }

    Scaffold(
        bottomBar = {
            if (AppType.getUserType() == UserType.EMPRESA) BottomBarNavigation(navController)
            //else if (AppType.getUserType() == UserType.UNKNOWN) BottomBarNavigation(navController)
            else if (AppType.getUserType() == UserType.ENTITAT) BottomBarNavigationEntitat(navController)
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Parte superior: Nombre de la persona con la que se está hablando
            TopAppBar(
                title = {
                    Text(
                        text = name, // Aquí deberías obtener el nombre de la entidad a partir de id_docEntitat
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                },
                modifier = Modifier.padding(16.dp),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigate(route = AppScreens.ChatScreen.route) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Botón de retroceso"
                        )
                    }
                }
            )

            // Lista de mensajes

            LazyColumn(
                modifier = Modifier.weight(1f),
                reverseLayout = true,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                val current_user = FirebaseAuth.getInstance().currentUser
                val user = current_user?.uid.toString()

                items(missatges) { missatge ->
                    val isCurrentUser = user == missatge.from

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val textAlign = if (isCurrentUser) TextAlign.End else TextAlign.Start
                        val backgroundColor =
                            if (isCurrentUser) Color.hsv(0f, 0.4f, 1f, alpha = 0.5f) else Color.hsv(
                                40f,
                                0.05f,
                                0.96f,
                                alpha = 0.8f
                            )

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm") // Formato deseado
                        dateFormat.timeZone = TimeZone.getTimeZone("Europe/Madrid")
                        val dateString = dateFormat.format(missatge.dob)

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Column {
                                if (missatge.message.startsWith("https:")) {
                                    ImageWithCoil(missatge.message)
                                }
                                else {
                                    Text(
                                        text = missatge.message,
                                        textAlign = textAlign,
                                        fontSize = 18.sp // Tamaño del mensaje (puedes ajustar el valor según tus preferencias)
                                    )
                                }
                                Text(
                                    text = dateString,
                                    color = Color.Gray,
                                    textAlign = TextAlign.End,
                                    fontSize = 12.sp, // Tamaño de la fecha (puedes ajustar el valor según tus preferencias)
                                    modifier = Modifier.align(Alignment.End) // Alinear la fecha en la parte inferior derecha
                                )
                            }
                        }
                    }
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
                            .weight(4f)
                            .padding(end = 8.dp),
                        placeholder = {
                            Text(text = stringResource(R.string.placeholder_escribir_msg))
                        }
                    )
                    Button(
                        onClick = { sendMessage(textFieldValue.value); textFieldValue.value = ""; },
                        modifier = Modifier.padding(start = 4.dp).weight(1f)
                    ) {
                        Text(text = "➤")
                    }
                    CameraButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .padding(start = 1.dp)
                            .weight(1f)
                    ) {
                        Text(text = "\uD83D\uDCF7")
                    }
                }
            }
        }
    }
}

@Composable
fun ImageWithCoil(url: String) {
    var isExpanded by remember { mutableStateOf(false) }

    val toggleExpansion: () -> Unit = {
        isExpanded = !isExpanded
    }

    val modifier = if (isExpanded) {
        Modifier.fillMaxSize()
    } else {
        Modifier.size(200.dp)
    }

    Box(
        modifier = Modifier.clickable(onClick = toggleExpansion)
    ) {
        Image(
            painter = rememberImagePainter(
                data = url,
                builder = {

                }
            ),
            contentDescription = "Foto",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}

@Composable
fun CameraButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Button(onClick = onClick, modifier = modifier) {
        content()
    }
}

fun guardarUrlDeDescargaEnFirestore(downloadUrl: String) {
    val db = Firebase.firestore
    val current_user = FirebaseAuth.getInstance().currentUser
    val user = current_user?.uid.toString()

    val message = Message(
        message = downloadUrl, // Aquí guardas la URL de descarga de la imagen
        from = user
    )
    db.collection("chats").document(AppDescription2.chat_id).collection("messages").document()
        .set(message)
}

object AppDescription2 {
    var chat_id: String = ""
}

fun sendMessage(message_chat: String) {
    if (message_chat != "") {
        val db = Firebase.firestore
        val current_user = FirebaseAuth.getInstance().currentUser
        val user = current_user?.uid.toString()
        val message = Message(
            message = message_chat,
            from = user
        )
        db.collection("chats").document(AppDescription2.chat_id).collection("messages").document()
            .set(message)
    }
}

