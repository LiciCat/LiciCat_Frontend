package com.licicat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.licicat.navigation.AppScreens
import com.licicat.screens.*

//elemento composable encargado de dirigir la navegacion correcta entre las pantallas
@Preview
@Composable
fun AppNavigation() {
    val navController = rememberNavController() //controla estado de navegacion actual entre pantallas (propagar entre pantallas)
    NavHost(navController= navController, startDestination =AppScreens.InicioScreen.route) {
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
        composable(route = AppScreens.SignUpCompanyScreen.route) {
            SignUpScreenEmpresa(navController)
        }
        composable(route = AppScreens.SignUpEntitatScreen.route) {
            SignUpScreenEntitat(navController)
        }

        composable(route = AppScreens.ConfigurationScreen.route) {
            ConfigurationScreen(navController)
        }
        composable(route = AppScreens.InicioScreen.route) {
            InicioScreen(navController)
        }

        composable(
            route = AppScreens.withArgs("{location}","{title}", "{description}","{price}","{denomination}", "{enllac_publicacio}"),
            arguments = listOf(
                navArgument("location") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType },
                navArgument("price") { type = NavType.StringType },
                navArgument("denomination") { type = NavType.StringType },
                navArgument("enllac_publicacio") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val location = backStackEntry.arguments?.getString("location") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""
            val price = backStackEntry.arguments?.getString("price") ?: ""
            val denomination = backStackEntry.arguments?.getString("denomination") ?: ""
            val enllac_publicacio = backStackEntry.arguments?.getString("enllac_publicacio") ?: ""
            LicitacioScreen(
                navController = navController,
                location = location,
                title = title,
                description = description,
                price = price,
                denomination = denomination,
                enllac_publicacio = enllac_publicacio
            )        }

        composable(
            route = AppScreens.Args("{location}","{title}"),
            arguments = listOf(
                navArgument("location") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val location = backStackEntry.arguments?.getString("location") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            MapScreen(navController = navController, adress = location, title = title)
        }


    }
}