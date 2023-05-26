package com.licicat.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.licicat.navigation.AppScreens

@Composable
fun CardChat(chat_id: String, name: String, info: String, id_Empresa: String, id_docEntitat: String, navController: NavController) {
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
                text = "Xat amb "+ name,
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