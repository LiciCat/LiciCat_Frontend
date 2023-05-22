package com.licicat.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.licicat.R
import com.licicat.navigation.AppScreens
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")


@Composable
fun InicioScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    LaunchedEffect(key1 = currentUser) {
        delay(1000) // Agregamos un pequeño retraso antes de navegar.
        if (currentUser == null || currentUser.email.isNullOrEmpty()) {
            navController.navigate(AppScreens.LoginScreen.route)
        } else {
            navController.navigate(AppScreens.HomeScreen.route)
        }
    }

    Scaffold(

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            HeaderImageInicio(Modifier)
        }
    }
}



@Composable
fun HeaderImageInicio(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.licicat),
        contentDescription = "Header",
        modifier = modifier
    )
}