package com.licicat.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Visibility
//import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.licicat.R




val itemsList = listOf("Empresa", "Nom i Cognoms", "Email", "Contrasenya", "NIF", "Telefon")


/*@Composable
fun SignUpCompanyScreen(navController: NavController,
                        viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val itemsList = listOf("Empresa", "Nom i Cognoms", "Email", "Contrasenya", "NIF", "Telefon")

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
                        if(item == "Empresa"){
                            empresaString = us.text
                        }
                        if(item == "Nom i Cognoms"){
                            nomString = us.text
                        }
                        if(item == "Email"){
                            emailString = us.text
                        }
                        if(item == "Contrasenya"){
                            passwordString = us.text
                        }
                        if(item == "NIF"){
                            NifString = us.text
                        }
                        if(item == "Telefon"){
                            telefonString = us.text
                        }
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
*/

@Composable
fun SignUpScreenEmpresa(
    navController: NavController,
    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        UserSignUpForm(
            modifier = Modifier,
            navController
        ){
            email, password ->
            viewModel.createUserWithEmailAndPassword(email, password) {
                navController.navigate(route = AppScreens.HomeScreen.route)
            }
        }
    }
}

@Composable
fun UserSignUpForm(
    modifier: Modifier,
    navController: NavController,
    onDone: (String, String) -> Unit = {email, pwd -> }
) {
    val email = rememberSaveable {
        mutableStateOf("")
    }
    val password = rememberSaveable {
        mutableStateOf("")
    }
    val passwordVisible = rememberSaveable {
        mutableStateOf(false)
    }
    val nom = rememberSaveable {
        mutableStateOf("")
    }
    var empresa = rememberSaveable {
        mutableStateOf("")
    }
    val nif = rememberSaveable {
        mutableStateOf("")
    }
    val telefon = rememberSaveable {
        mutableStateOf("")
    }
    val valido = remember(email.value, password.value){
        email.value.trim().isNotEmpty() &&
                password.value.trim().isNotEmpty()
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
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
        item { Spacer(modifier = Modifier.padding(8.dp))}
       /* item {
            EmpresaInput(
                empresaState = empresa
            )
        }
        item { Spacer(modifier = Modifier.padding(8.dp))}
        item {
            NomInput(
                NomState = nom
            )
        }
        item { Spacer(modifier = Modifier.padding(8.dp))}*/
        item {
            EmailInput(
                emailState = email
            )
        }
        item { Spacer(modifier = Modifier.padding(8.dp))}
        item {
            PasswordInput(
                passwordState = password,
                labelId = "Contrsenya",
                passwordVisible = passwordVisible
            )

        }
        item { Spacer(modifier = Modifier.padding(8.dp))}
        /*item {
            NifInput(
                NifState = nif
            )
        }
        item { Spacer(modifier = Modifier.padding(8.dp))}
        item {
            TelefonInput(
                TelefonState = telefon
            )
        }
        item { Spacer(modifier = Modifier.padding(8.dp))}*/

        item {
            SignUpButton(
                inputValido = valido
            ){
                onDone(email.value.trim(), password.value.trim())            }
        }

    }
}

@Composable
fun SignUpButton(
    inputValido: Boolean,
    onClic: () -> Unit
) {
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

        ), enabled = inputValido,
        onClick = onClic
    ) {
        Text(text = "Registrar-se")
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





