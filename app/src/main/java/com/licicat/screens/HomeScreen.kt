package com.licicat.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.licicat.components.BottomBarNavigation
import com.licicat.components.CardLicitacio
import com.licicat.navigation.AppScreens
data class MyLicitacio(val title: String, val description: String, val icon: ImageVector, val price: String, val date: String)

private val licitacions: List<MyLicitacio> = listOf(
    MyLicitacio(title = "Ajuntament de Barcelona", description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam", icon = Icons.Default.Person, date = "25/04/23", price = "198.000€"),
    MyLicitacio(title = "Ajuntament de Girona", description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam", icon = Icons.Default.Person, date = "25/08/23", price = "200.000€"),
    MyLicitacio(title = "Ajuntament de Tarragona", description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam", icon = Icons.Default.Person, date = "25/04/23", price = "198.000€"),
    MyLicitacio(title = "Ajuntament de Barcelona", description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam", icon = Icons.Default.Person, date = "25/04/23", price = "198.000€"),
    MyLicitacio(title = "Ajuntament de Girona", description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam", icon = Icons.Default.Person, date = "25/04/23", price = "198.000€"),
    MyLicitacio(title = "Ajuntament de Tarragona", description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam", icon = Icons.Default.Person, date = "25/04/23", price = "198.000€"),
    MyLicitacio(title = "Ajuntament de Barcelona", description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam", icon = Icons.Default.Person, date = "25/04/23", price = "198.000€")
)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            BottomBarNavigation(navController)
        }
    ) {
        LazyColumn {
            items(licitacions) { licitacio ->
                CardLicitacio(icon = licitacio.icon, title = licitacio.title, description = licitacio.description , date = licitacio.date , price = licitacio.price)
            }
        }
    }
}


