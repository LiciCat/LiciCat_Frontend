package com.licicat.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.licicat.R
import com.licicat.navigation.AppScreens

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun InicioScreen(navController: NavController) {
    Scaffold {
        Box(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            HeaderImageInicio(Modifier.align(Alignment.Center))
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser?.email.isNullOrEmpty()){
                navController.navigate("login_screen")
            }
            else{
                navController.navigate("home_screen")
            }

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