package com.licicat.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.licicat.navigation.AppScreens

@Composable
fun CardChat(chat_id: String, name: String, info: String, id_Empresa: String, id_docEntitat: String, navController: NavController) {
    val db = Firebase.firestore
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


    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable (onClick = {
                navController.navigate(
                    AppScreens.withArgs2(chat_id,id_Empresa,id_docEntitat,info))}
            ),
        elevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = name,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = info,
                maxLines = 2,
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }
}