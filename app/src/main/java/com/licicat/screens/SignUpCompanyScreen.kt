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
fun SignUpScreenEmpresa(
    navController: NavController,
    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        val context = LocalContext.current
        UserSignUpForm(
            modifier = Modifier,
            navController
        ){
            email, password, nom, empresa, nif, telefon ->
            viewModel.createUserWithEmailAndPassword(email, password, nom, empresa, nif, telefon, context) {
                navController.navigate(route = AppScreens.HomeScreen.route)
            }
        }
    }
}

@Composable
fun UserSignUpForm(
    modifier: Modifier,
    navController: NavController,
    onDone: (String, String, String, String, String, String) -> Unit = {email, pwd, nom, empresa, nif, telefon -> }
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
    val valido = remember(email.value, password.value, nom.value, empresa.value, nif.value, telefon.value){
        email.value.trim().isNotEmpty() &&
                password.value.trim().isNotEmpty() &&
                nom.value.trim().isNotEmpty() &&
                empresa.value.trim().isNotEmpty() &&
                nif.value.trim().isNotEmpty() &&
                telefon.value.trim().isNotEmpty()
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
       item {
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
                onDone(email.value.trim(), password.value.trim(), nom.value.trim(), empresa.value.trim(), nif.value.trim(), telefon.value.trim() )            }
        }

    }
}

@Composable
fun TelefonInput(
    TelefonState: MutableState<String>,
    labelId : String = "Telèfon",
    isSingleLine: Boolean = true
) {
    OutlinedTextField(
        value = TelefonState.value,
        onValueChange = {TelefonState.value = it},
        label = {Text(text = labelId)},
        singleLine = isSingleLine,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone
        )
    )
}

@Composable
fun NifInput(
    NifState: MutableState<String>,
    labelId : String = "NIF",
    isSingleLine: Boolean = true
) {
    OutlinedTextField(
        value = NifState.value,
        onValueChange = {NifState.value = it},
        label = {Text(text = labelId)},
        singleLine = isSingleLine,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        )
    )
}

@Composable
fun NomInput(
    NomState: MutableState<String>,
    labelId : String = "Nom i Cognoms",
    isSingleLine: Boolean = true
) {
    OutlinedTextField(
        value = NomState.value,
        onValueChange = {NomState.value = it},
        label = {Text(text = labelId)},
        singleLine = isSingleLine,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        )
    )
}

@Composable
fun EmpresaInput(
    empresaState: MutableState<String>,
    labelId : String = "Empresa",
    isSingleLine: Boolean = true
) {
    OutlinedTextField(
        value = empresaState.value,
        onValueChange = {empresaState.value = it},
        label = {Text(text = labelId)},
        singleLine = isSingleLine,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        )
    )
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








