package com.licicat.navigation

import com.licicat.R

//classe en la cual se guardan todas las pantallas a las quales se puede navegar (ruta,titulo,icono)

sealed class AppScreens(val route: String, val title: String, val icon: Int) {
    object HomeScreen: AppScreens("home_screen","Home", R.drawable.baseline_home_24)
    object FavouritesScreen: AppScreens("fav_screen","Favourites", R.drawable.baseline_favorite_24)
    object ChatScreen: AppScreens("chat_screen", "Chats", R.drawable.baseline_chat_bubble_24)
    object ProfileScreen: AppScreens("profile_screen", "Profile", R.drawable.baseline_person_24)
    object LoginScreen: AppScreens("login_screen", "Login", R.drawable.baseline_person_24)
}
