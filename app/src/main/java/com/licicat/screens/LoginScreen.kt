package com.licicat.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.licicat.navigation.AppScreens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import com.licicat.R


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LoginScreen(navController: NavController) {
   Scaffold {
       Box(
           Modifier
               .fillMaxSize()
               .padding(16.dp)
       ) {
           LoginContent(Modifier.align(Alignment.Center), navController)
       }
   }
}

@Composable
fun LoginContent(modifier: Modifier, navController: NavController) {
    Column(modifier = modifier) {
        HeaderImage(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(16.dp))
        EmailField("email")
        Spacer(modifier = Modifier.padding(4.dp))
        PasswordField("password");
        Spacer(modifier = Modifier.padding(8.dp))
        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CreaCompteEmpresa(modifier = Modifier.weight(1f), navController)
            ForgotPassword(modifier = Modifier.align(Alignment.CenterVertically) )
        }
        CreaCompteEntitatPublica(Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.padding(20.dp))
        LoginButton(true,navController);
    }
}

@Composable
fun LoginButton(loginEnable: Boolean, navController: NavController) {
    Button(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFFFF454A),
            disabledBackgroundColor = Color(0xFFF78058),
            contentColor = Color.White,
            disabledContentColor = Color.White

        ), enabled = loginEnable,
        onClick = {
            navController.navigate(route = AppScreens.HomeScreen.route)
        }
    ) {
        Text(text = "Iniciar sessió")
    }
}

@Composable
fun ForgotPassword(modifier: Modifier) {
    Text(
        text = "Has oblidat la contrasenya?",
        modifier = modifier.clickable {},
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFFF454A)
    )
}

@Composable
fun CreaCompteEmpresa(modifier: Modifier, navController: NavController) {
    Text(
        text = "Crear compte d'empresa",
        modifier = modifier.clickable {navController.navigate(route = AppScreens.SignUpCompanyScreen.route)  },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFFF454A)
    )
}

@Composable
fun CreaCompteEntitatPublica(modifier: Modifier) {
    Text(
        text = "Crear compte d'entitat pública",
        modifier = modifier.clickable { },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFFF454A)
    )
}

@Composable
fun PasswordField(password: String) {
    TextField(
        value = password, onValueChange = { password -> "*" },
        placeholder = { Text(text = "Contraseña") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFF636262),
            backgroundColor = Color(0xFFDEDDDD),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun EmailField(email: String) {
    TextField(
        value = email,onValueChange = { email },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Email") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color(0xFF636262),
            backgroundColor = Color(0xFFDEDDDD),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.licicat),
        contentDescription = "Header",
        modifier = modifier
    )
}

