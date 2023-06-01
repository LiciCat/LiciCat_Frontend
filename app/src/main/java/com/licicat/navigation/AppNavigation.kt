package com.licicat.navigation

import android.content.Intent
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
@Composable
fun AppNavigation(intent: Intent) {
    val navController = rememberNavController() //controla estado de navegacion actual entre pantallas (propagar entre pantallas)
    var aux = if(intent.hasExtra("destinacion")) AppScreens.ValoracioEntitatScreen.route else AppScreens.InicioScreen.route
    NavHost(navController= navController, startDestination = aux) {
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
        composable(route = AppScreens.EditPerfilScreen.route) {
            EditPerfilScreen(navController)
        }

        composable(route = AppScreens.ValoracioEntitatScreen.route) {
            ValoracioEntitatScreen(navController, intent)
        }

        composable(
            route = AppScreens.withArgs2("{chat_id}","{id_Empresa}","{id_docEntitat}", "{info}"),
            arguments = listOf(
                navArgument("chat_id") { type = NavType.StringType },
                navArgument("id_Empresa") { type = NavType.StringType },
                navArgument("id_docEntitat") { type = NavType.StringType },
                navArgument("info") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chat_id = backStackEntry.arguments?.getString("chat_id") ?: ""
            val id_Empresa = backStackEntry.arguments?.getString("id_Empresa") ?: ""
            val id_docEntitat = backStackEntry.arguments?.getString("id_docEntitat") ?: ""
            val info = backStackEntry.arguments?.getString("info") ?: ""
            WhatsScreen(navController = navController, chat_id = chat_id, id_Empresa = id_Empresa, id_docEntitat = id_docEntitat, info = info)
        }

        composable(
            route = AppScreens.withArgs("{location}","{title}", "{description}","{price}","{denomination}", "{enllac_publicacio}", "{date}"),
            arguments = listOf(
                navArgument("location") { type = NavType.StringType },
                navArgument("title") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType },
                navArgument("price") { type = NavType.StringType },
                navArgument("denomination") { type = NavType.StringType },
                navArgument("enllac_publicacio") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType }
                )
        ) { backStackEntry ->
            val location = backStackEntry.arguments?.getString("location") ?: ""
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""
            val price = backStackEntry.arguments?.getString("price") ?: ""
            val denomination = backStackEntry.arguments?.getString("denomination") ?: ""
            val enllac_publicacio = backStackEntry.arguments?.getString("enllac_publicacio") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            LicitacioScreen(
                navController = navController,
                location = location,
                title = title,
                description = description,
                price = price,
                denomination = denomination,
                enllac_publicacio = enllac_publicacio,
                date = date
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
        composable(
            route = AppScreens.Args2("{title}"),
            arguments = listOf(
                navArgument("title") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            ProfileEntitatScreen(navController = navController, entitat = title)
        }
    }
}