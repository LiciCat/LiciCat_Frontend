package com.licicat.screens

import android.annotation.SuppressLint
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ConfigurationScreen(navController: NavController){
    val auth = FirebaseAuth.getInstance()

    Button(onClick = {
        auth.signOut() // Cierra la sesión actual
        navController.navigate("login_screen") // Navega a la pantalla de inicio de sesión
    }) {
        Text(text = "Tancar sessió")
    }
}