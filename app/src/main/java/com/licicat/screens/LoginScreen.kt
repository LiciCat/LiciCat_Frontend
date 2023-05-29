package com.licicat.screens

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
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
import com.licicat.AppType
import com.licicat.R
import com.licicat.UserType
import com.google.firebase.auth.FirebaseAuth;



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    ) {
   Scaffold {
       Box(
           Modifier
               .fillMaxSize()
               .padding(16.dp)
       ) {
           val context = LocalContext.current
           UserForm(
               Modifier.align(Alignment.Center),
               navController
           ){
               email, password ->
               viewModel.signInWithEmailAndPassword(email, password, context){
                   /*val userType = AppType.getUserType()
                   if ((userType == UserType.EMPRESA) /*|| (userType == UserType.UNKNOWN)*/) {
                       navController.navigate(route = AppScreens.HomeScreen.route)
                   } else if (userType == UserType.ENTITAT) {
                       navController.navigate(route = AppScreens.ChatScreen.route)
                   }*/
                   navController.navigate(route = AppScreens.InicioScreen.route)
               }
           }
       }
   }
}

@Composable
fun UserForm(
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
    val valido = remember(email.value, password.value){
        email.value.trim().isNotEmpty() &&
                password.value.trim().isNotEmpty()
    }
    Column(modifier = modifier){
        HeaderImage(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(16.dp))
        EmailInput(
            emailState = email
        )
        Spacer(modifier = Modifier.padding(4.dp))
        PasswordInput(
            passwordState = password,
            labelId = "Contrasenya",
            passwordVisible = passwordVisible
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CreaCompteEmpresa(modifier = Modifier.weight(1f), navController)
            ForgotPassword(LocalContext.current, email.value,modifier = Modifier.align(Alignment.CenterVertically) )
        }
        CreaCompteEntitatPublica(Modifier.align(Alignment.Start), navController)
        Spacer(modifier = Modifier.padding(20.dp))
        LoginButton(
            inputValido = valido
        ){
            onDone(email.value.trim(), password.value.trim())
        }

    }

}

@Composable
fun PasswordInput(
    passwordState: MutableState<String>,
    labelId: String,
    passwordVisible: MutableState<Boolean>
) {
    val visualTransformation = if (passwordVisible.value)
        VisualTransformation.None
    else PasswordVisualTransformation()

    OutlinedTextField(
        value = passwordState.value,
        onValueChange = {passwordState.value = it},
        label = {Text(text = labelId)},
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        visualTransformation = visualTransformation,
        /*trailingIcon = {
            if (passwordState.value.isNotBlank()){
                PasswordVisibleIcon(passwordVisible)
            }
            else null

        }*/
    )
}
/*
@Composable
fun PasswordVisibleIcon(
    passwordVisible: MutableState<Boolean>
) {
    val image =
        if(passwordVisible.value)
            Icons.Default.VisibilityOff
        else
            Icons.Default.Visibility
    IconButton(onClick = { /*TODO*/ }) {
        Icon(
            imageVector = image,
            contentDescription = "" )
    }
}*/

@Composable
fun EmailInput(
    emailState: MutableState<String>,
    labelId : String = "Email",
    isSingleLine: Boolean = true
) {
    OutlinedTextField(
        value = emailState.value,
        onValueChange = {emailState.value = it},
        label = {Text(text = labelId)},
        singleLine = isSingleLine,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        )
    )
}


@Composable
fun LoginButton(
    inputValido: Boolean,
    onClic: () -> Unit
) {

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

        ), enabled = inputValido,
        onClick = onClic

    ) {
        Text(text = "Iniciar sessió")
    }
}

@Composable
fun ForgotPassword(context: Context , mail: String, modifier: Modifier) {
    Text(
        text = "Has oblidat la contrasenya?",
        modifier = modifier.clickable {reset_password(context, mail)},
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFFF454A)
    )
}

fun reset_password(context: Context, mail: String){
    var auth: FirebaseAuth
    auth = FirebaseAuth.getInstance();
    if (mail.isNullOrEmpty()) showToast(context ,"Introduixi un mail per poder enviar el correu de reinici de contrasenya.")
    else {
        auth.sendPasswordResetEmail(mail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast(context, "S'ha enviat el reinici de contrasenya al correu.")
                } else {
                    showToast(
                        context,
                        "Introdueixi un correu loguejat per poder enviar el reinici de contrasenya."
                    )
                }
            }
    }
}

fun showToast(context: Context,message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
fun CreaCompteEntitatPublica(modifier: Modifier, navController: NavController) {
    Text(
        text = "Crear compte d'entitat pública",
        modifier = modifier.clickable {navController.navigate(route = AppScreens.SignUpEntitatScreen.route) },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFFF454A)
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

