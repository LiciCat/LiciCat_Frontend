package com.licicat.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.layout.*
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.licicat.AppType
import com.licicat.R
import com.licicat.UserType
import com.licicat.components.BottomBarNavigation
import com.licicat.components.BottomBarNavigationEntitat
import com.licicat.ui.theme.redLicicat


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ConfigurationScreen(navController: NavController) {

    Scaffold(
        bottomBar = {
            if (AppType.getUserType() == UserType.EMPRESA) BottomBarNavigation(navController)
            //else if (AppType.getUserType() == UserType.UNKNOWN) BottomBarNavigation(navController)
            else if (AppType.getUserType() == UserType.ENTITAT) BottomBarNavigationEntitat(navController)
        }
    ) {
        val auth = FirebaseAuth.getInstance()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    auth.signOut() // Cierra la sesión actual
                    navController.navigate("login_screen") // Navega a la pantalla de inicio de sesión
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = redLicicat),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(text = stringResource(R.string.boto_tancar_sessio), color = Color.White)
            }

            Button(
                onClick = {
                    navController.navigate("profile_edit_screen") // Navega a la pantalla de edición de perfil
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = redLicicat),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(text = stringResource(R.string.boto_editar_perfil), color = Color.White)
            }
        }
    }
}

