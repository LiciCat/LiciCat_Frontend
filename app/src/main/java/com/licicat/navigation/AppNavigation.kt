package com.licicat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.licicat.screens.FavouritesScreen
import com.licicat.screens.HomeScreen
import com.licicat.screens.ChatScreen
import com.licicat.screens.ProfileScreen
import com.licicat.screens.LoginScreen

//elemento composable encargado de dirigir la navegacion correcta entre las pantallas
@Preview
@Composable
fun AppNavigation() {
    val navController = rememberNavController() //controla estado de navegacion actual entre pantallas (propagar entre pantallas)
    NavHost(navController= navController, startDestination = AppScreens.LoginScreen.route) {
        //tantos composables como diferentes pantallas accesibles
        composable(route = AppScreens.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(route = AppScreens.FavouritesScreen.route) {
            FavouritesScreen(navController)
        }
        composable(route = AppScreens.ChatScreen.route) {
            ChatScreen(navController)
        }
        composable(route = AppScreens.ProfileScreen.route) {
            ProfileScreen(navController)
        }
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(navController)
        }
    }
}