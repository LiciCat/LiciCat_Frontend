package com.licicat.screens

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.licicat.components.BottomBarNavigation
import com.licicat.navigation.AppScreens

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavController) {
    val bottomNavigationItems = listOf(
        AppScreens.HomeScreen,
        AppScreens.FavouritesScreen,
        AppScreens.ChatScreen,
        AppScreens.ProfileScreen
    )

    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController, bottomNavigationItems)
        }
    ) {

    }
}

