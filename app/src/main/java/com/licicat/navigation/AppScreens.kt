package com.licicat.navigation

import com.licicat.R
import com.licicat.navigation.AppScreens.ChatScreen.route

//classe en la cual se guardan todas las pantallas a las quales se puede navegar (ruta,titulo,icono)

sealed class AppScreens(val route: String, val title: String, val icon: Int) {
    object HomeScreen: AppScreens("home_screen","Home", R.drawable.baseline_home_24)
    object FavouritesScreen: AppScreens("fav_screen","Favourites", R.drawable.baseline_favorite_24)
    object ChatScreen: AppScreens("chat_screen", "Chats", R.drawable.baseline_chat_bubble_24)
    object ProfileScreen: AppScreens("profile_screen", "Profile", R.drawable.baseline_person_24)
    object LoginScreen: AppScreens("login_screen", "Login", R.drawable.baseline_person_24)
    object SignUpCompanyScreen: AppScreens("signup_screen", "SignUp", R.drawable.baseline_person_24)
    object SignUpEntitatScreen: AppScreens("signup_screen_entitat", "SignUpEntitat", R.drawable.baseline_person_24)
    object MapScreen: AppScreens("map_screen", "See on map", R.drawable.baseline_fmd_good_24)
    object ConfigurationScreen: AppScreens("configuration_screen", "Configuration", R.drawable.baseline_person_24)
    object InicioScreen: AppScreens("inicio_screen", "Inicio", R.drawable.baseline_person_24)
    object LicitacioScreen: AppScreens("licitacio_screen", "Licitacio", R.drawable.baseline_fmd_good_24)
    object WhatsScreen: AppScreens("whats_screen", "Whats", R.drawable.baseline_fmd_good_24)
    companion object {
        fun withArgs(location: String?, title:String?, description:String?, price:String?): String {
            return buildString {
                append(LicitacioScreen.route)
                append("/$location")
                append("/$title")
                append("/$description")
                append("/$price")
            }
        }
        fun Args(location: String?, title:String?): String {
            return buildString {
                append(MapScreen.route)
                append("/$location")
                append("/$title")
            }
        }

        fun withArgs2(chat_id: String?, id_Empresa: String?, id_docEntitat:String?, info:String?): String {
            return buildString {
                append(WhatsScreen.route)
                append("/$chat_id")
                append("/$id_Empresa")
                append("/$id_docEntitat")
                append("/$info")
            }
        }

    }
}


