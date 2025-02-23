package com.licicat.screens


import android.Manifest
import android.annotation.SuppressLint

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build


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

import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlin.random.Random


import com.google.firebase.messaging.RemoteMessage.Notification.*

import com.licicat.components.BottomBarNavigationEntitat
import com.licicat.*

import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme

import com.licicat.R



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LicitacioScreen(navController: NavController, location:String?, title:String?, description:String?, price:String?, denomination:String?, enllac_publicacio:String?, date:String?) {

    description?.let {
        AppDescription.description = it
    }

    title?.let {
        AppDescription_title.title = it
    }

    createChannel(navController)
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
                        text = stringResource(R.string.label_entitat_no_registrada),
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }

            if ((denomination != null) && (denomination != "null")) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DescripcionCard(
                            title = stringResource(R.string.label_objecte_contracte_licitacio),
                            description = denomination
                        )
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
                            title = stringResource(R.string.label_descripcio_licitacio),
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
                    if (price != null) {
                        smallCard(
                            title = stringResource(R.string.label_pressupost_licitacio),
                            description = price,
                        )
                    }
                    if (location != null) {
                        smallCard(
                            title = stringResource(R.string.label_lloc_execucio),
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
                            text = stringResource(R.string.label_error_crear_xat),
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
                    if (AppType.getUserType() == UserType.EMPRESA) {
                        ObrirChatButton(
                            navController = navController,
                            existeix_entitat = existeix_entitat
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    if (AppType.getUserType() == UserType.EMPRESA){
                        MyButton(enllac_publicacio, navController, title, description, price, location, date, denomination)
                    }
                    Spacer(modifier = Modifier.width(16.dp)) // Espacio horizontal entre botones
                    MyShareButton(enllac_publicacio)
                    Spacer(modifier = Modifier.width(13.dp))
                    DescarregarPdfBoto(location, title, description, price, denomination, date)
                }
            }

        }
    }
}


fun createChannel(navController: NavController) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "ASWAC",
            "MySuperChannel",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "SUSCRIBETE"
        }

        val notificationManager: NotificationManager =
            navController.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}


private fun enviarSolicitutValoracio(navController: NavController, title: String?) {

    val intent = Intent(navController.context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
       putExtra("destinacion", "type_valoracio_entitat")
        putExtra("nom_entitat", title)
    }

    val destination = intent.getStringExtra("destinacion")
    println("Abans de enviar destinacion new: $destination")
    val entitat = intent.getStringExtra("nom_entitat")
    println("Abans de enviar entitat new: $entitat")

    val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        navController.context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or flag
    )

    var builder = NotificationCompat.Builder(navController.context, "ASWAC")
        .setSmallIcon(android.R.drawable.alert_light_frame)
        .setContentTitle(navController.context.getString(R.string.titol_notificacio_valoracio))
        .setContentIntent(pendingIntent)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(navController.context.getString(R.string.cos1_notificacio_valoracio) + "$title"+ navController.context.getString(
                                    R.string.co2_notificacio_valoracio))
        )

    with(NotificationManagerCompat.from(navController.context)) {
        if (ActivityCompat.checkSelfPermission(
                navController.context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notify(1, builder.build())
    }
}


@Composable
fun DescarregarPdfBoto(
    location: String?,
    title: String?,
    description: String?,
    price: String?,
    denomination: String?,
    date: String?
) {
    IconButton(onClick = {
        //Crida api per descarregar pdf
        Log.d("Descarregar","DOWNLOADING")
        val downloader = DownloadDPF()
        downloader.download(location, title, description, price, denomination, date)
    }) {
        Icon(imageVector = Icons.Filled.Download, contentDescription = "Descarregar", tint = Color.DarkGray)
    }
}




@Composable
fun MyButton(
    enllac_publicacio: String?, navController: NavController, title: String?,
    description: String?, price: String?, location: String?, date: String?, denomination: String?
) {
    val context = LocalContext.current
    val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(enllac_publicacio)) }
    val db = Firebase.firestore
    val current_user = FirebaseAuth.getInstance().currentUser
    var id_lic: Int = 0
    var isOptat by remember { mutableStateOf(false) }
    println("1")
    db.collection("licitacionsOptades")
        .whereEqualTo("title", title)
        .whereEqualTo("description", description)
        .whereEqualTo("date", date)
        .whereEqualTo("price", price)
        .whereArrayContains("users_ids", current_user?.uid.toString())
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                isOptat = true
            }
            for (document in documents) {
                id_lic = document.getLong("lic_id")?.toInt() ?: 0
            }
        }
        .addOnFailureListener { exception ->
            Log.w("app", "Error getting documentsMYBUTTON: ", exception)
        }
    println("2")

    Button(onClick = {
            if (!isOptat) {
                db.collection("licitacionsOptades")
                    .whereEqualTo("title", title)
                    .whereEqualTo("description", description)
                    .whereEqualTo("date", date)
                    .whereEqualTo("price", price)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            for (document in documents) {
                                val usersIds =
                                    document.get("users_ids") as MutableList<String>
                                usersIds.add(current_user?.uid.toString())
                                document.reference.update("users_ids", usersIds)
                                id_lic = document.getLong("lic_id")?.toInt() ?: 0
                            }
                        } else {
                            val users = listOf(current_user?.uid.toString())
                            id_lic = Random.nextLong(1, 2147483647).toInt()
                            val data = hashMapOf(
                                "title" to title,
                                "description" to description,
                                "denomination" to denomination,
                                "date" to date,
                                "price" to price,
                                "lic_id" to id_lic,
                                "location" to location,
                                "users_ids" to users,
                                "enllac_publicacio" to enllac_publicacio,
                            )
                            db.collection("licitacionsOptades").add(data)
                        }
                        db.collection("usersEmpresa")
                            .whereEqualTo("user_id", current_user?.uid)
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    val optatsIds =
                                        document.get("optats") as MutableList<Int>
                                    optatsIds.add(id_lic)
                                    document.reference.update(
                                        "optats",
                                        optatsIds
                                    )
                                }
                            }
                        }
            }
        context.startActivity(intent)
        enviarSolicitutValoracio(navController, title) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.DarkGray,
            contentColor = Color.White)
    ) {
        Text(text = stringResource(R.string.boto_optar))
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
                maxLines = 2,
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



