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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.licicat.R









@Composable
fun SignUpScreenEntitat(
    navController: NavController,
    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        val context = LocalContext.current
        UserSignUpFormEntitat(
            modifier = Modifier,
            navController
        ){
                email, password, nom, entitat, nif, telefon ->
            viewModel.createUserWithEmailAndPasswordEntitat(email, password, nom, entitat, nif, telefon, context) {
                navController.navigate(route = AppScreens.HomeScreen.route)
            }
        }
    }
}

@Composable
fun UserSignUpFormEntitat(
    modifier: Modifier,
    navController: NavController,
    onDone: (String, String, String, String, String, String) -> Unit = {email, pwd, nom, entitat, nif, telefon -> }
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
    var entitat = rememberSaveable {
        mutableStateOf("")
    }
    val nif = rememberSaveable {
        mutableStateOf("")
    }
    val telefon = rememberSaveable {
        mutableStateOf("")
    }
    val valido = remember(email.value, password.value, nom.value, entitat.value, nif.value, telefon.value){
        email.value.trim().isNotEmpty() &&
                password.value.trim().isNotEmpty() &&
                nom.value.trim().isNotEmpty() &&
                entitat.value.trim().isNotEmpty() &&
                nif.value.trim().isNotEmpty() &&
                telefon.value.trim().isNotEmpty()
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(text = "Registre d'entitat",
                style = MaterialTheme.typography.h4,
                color = Color(0xFFFF454A),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(20.dp),
                fontWeight = FontWeight.Bold,

                )
        }
        item { Spacer(modifier = Modifier.padding(8.dp))}
        item {
            EntitatInput(
                entitatState = entitat
            )
        }
        item { Spacer(modifier = Modifier.padding(8.dp))}
        item {
            NomInput(
                NomState = nom
            )
        }
        item { Spacer(modifier = Modifier.padding(8.dp))}
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
        item {
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
        item { Spacer(modifier = Modifier.padding(8.dp))}

        item {
            SignUpButton(
                inputValido = valido
            ){
                onDone(email.value.trim(), password.value.trim(), nom.value.trim(), entitat.value.trim(), nif.value.trim(), telefon.value.trim() )            }
        }

    }
}







@Composable
fun EntitatInput(
    entitatState: MutableState<String>,
    labelId : String = "Entitat",
    isSingleLine: Boolean = true
) {
    OutlinedTextField(
        value = entitatState.value,
        onValueChange = {entitatState.value = it},
        label = {Text(text = labelId)},
        singleLine = isSingleLine,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        )
    )
}












