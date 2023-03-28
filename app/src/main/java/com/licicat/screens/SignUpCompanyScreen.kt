package com.licicat.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.licicat.navigation.AppScreens


@Composable
fun SignUpCompanyScreen(navController: NavController) {
    val itemsList = listOf("Empresa", "Nom i Cognoms", "Email", "Contrasenya", "NIF", "Telèfon")

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(text = "Registre d'empresa",
                    style = MaterialTheme.typography.h4,
                    color = Color(0xFFFF454A),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(20.dp),
                    fontWeight = FontWeight.Bold,

                )
            }
            items(itemsList) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.padding(8.dp))

                    /*if (item == "Contrasenya") {
                        PasswordTextField()
                    } else {*/
                        var us by remember { mutableStateOf(TextFieldValue("")) }
                        TextField(
                            value = us,onValueChange = { us = it },
                            label = { Text(text = "$item") },
                            modifier = Modifier.fillMaxWidth()
                        )
                  //  }
                    Spacer(modifier = Modifier.padding(8.dp))
                }
            }
            item{
                Button(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFFF454A),
                        disabledBackgroundColor = Color(0xFFF78058),
                        contentColor = Color.White,
                        disabledContentColor = Color.White

                    ),
                    onClick = {
                        navController.navigate(route = AppScreens.HomeScreen.route)
                    }
                ) {
                    Text(text = "Registrar-se")
                }
            }

        }
    }
}

@Composable
fun PasswordTextField() {
    TextField(
        value = "",
        onValueChange = {},
        label = { Text(text = "Contrasenya") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
}

