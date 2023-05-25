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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.licicat.AppType
import com.licicat.R
import com.licicat.UserType
import com.licicat.navigation.AppScreens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")


@Composable
fun InicioScreen(navController: NavController) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    LaunchedEffect(key1 = currentUser) {
        delay(1000) // Agregamos un pequeño retraso antes de navegar.
        if (currentUser == null || currentUser.email.isNullOrEmpty()) {
            navController.navigate(AppScreens.LoginScreen.route)
        } else {
            val db = Firebase.firestore
            val current_user = FirebaseAuth.getInstance().currentUser
            val id_del_user = current_user?.uid

            // Utilizamos una coroutine para esperar a que se complete la operación de Firestore
            val userType = withContext(Dispatchers.IO) {
                try {
                    val snapshot = db.collection("usersEmpresa")
                        .whereEqualTo("user_id", id_del_user)
                        .get()
                        .await()

                    if (!snapshot.isEmpty) {
                        AppType.setUserType(UserType.EMPRESA)
                        UserType.EMPRESA
                    } else {
                        AppType.setUserType(UserType.ENTITAT)
                        UserType.ENTITAT
                    }
                } catch (e: Exception) {
                    AppType.setUserType(UserType.UNKNOWN)
                    UserType.UNKNOWN
                }
            }

            if (userType == UserType.EMPRESA || userType == UserType.UNKNOWN) {
                navController.navigate(AppScreens.HomeScreen.route)
            } else if (userType == UserType.ENTITAT){
                navController.navigate(AppScreens.ChatScreen.route)
            }
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