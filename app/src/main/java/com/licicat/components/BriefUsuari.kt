package com.licicat.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.licicat.navigation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import kotlin.random.Random


@Composable
fun CardUsuari(
    icon: ImageVector,
    title: String?,
    correu: String?,
    telefon: String?
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable(onClick = { /* Navegació al perfil de l'usuari */ })
            .padding(vertical = 2.dp, horizontal = 16.dp), // Espaiat intern del contingut de la Card
        elevation = 8.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(50.dp) // Modifica el valor de la mida aquí
            )
            Column {
                Text(
                    text = title.orEmpty(), // Utilitza el títol o una cadena buida si és nul
                    style = MaterialTheme.typography.h6, // Estil de text gran
                    fontWeight = FontWeight.Bold // Pes de lletra en negreta
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.6f), // Tonalitat de l'ícone més clara
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = correu.orEmpty(), // Utilitza el correu o una cadena buida si és nul
                        style = MaterialTheme.typography.body2, // Estil de text més petit
                        color = Color.Black.copy(alpha = 0.6f), // Color de text més clar
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    telefon?.let { telefonValue ->
                        Text(
                            text = telefonValue.toString(),
                            style = MaterialTheme.typography.body2,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
