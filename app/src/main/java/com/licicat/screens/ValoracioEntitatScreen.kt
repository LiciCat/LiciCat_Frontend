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
import android.widget.RatingBar
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import com.google.firebase.messaging.RemoteMessage.Notification.*

import com.licicat.components.BottomBarNavigationEntitat

import com.licicat.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ValoracioEntitatScreen(navController: NavController, intent: Intent) {
    var rating by remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }
    var destination = ""
    var nom_entitat = ""
    if (intent.hasExtra("destinacion")) {
         destination = intent.getStringExtra("destinacion").toString()
        println("ara estem a la vista : $destination")
        nom_entitat = intent.getStringExtra("nom_entitat").toString()
        println("Nom entitat: ${intent.getStringExtra("nom_entitat")}")
    }

    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController)
        }
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = nom_entitat,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Calificación
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text("Calificación:")
            }

            // Comentario
            TextField(
                value = comment,
                onValueChange = { newComment -> comment = newComment },
                label = { Text("Comentario") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Botón de enviar
            Button(
                onClick = { },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Enviar")
            }
        }

    }


}
