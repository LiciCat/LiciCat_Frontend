package com.licicat.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material.Scaffold
import androidx.navigation.NavController
import com.licicat.navigation.AppScreens
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.licicat.AppType
import com.licicat.UserType
import com.licicat.components.*




@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun EditPerfilScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            if (AppType.getUserType() == UserType.EMPRESA) BottomBarNavigation(navController)
            //else if (AppType.getUserType() == UserType.UNKNOWN) BottomBarNavigation(navController)
            else if (AppType.getUserType() == UserType.ENTITAT) BottomBarNavigationEntitat(navController)
        }
    ) {

        var descripcio by remember { mutableStateOf("") }
        var nomCognoms by remember { mutableStateOf("") }
        var telefon by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            OutlinedTextField(
                value = descripcio,
                onValueChange = { descripcio = it },
                label = { Text("Descripció") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nomCognoms,
                onValueChange = { nomCognoms = it },
                label = { Text("Nom i Cognoms") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefon,
                onValueChange = { telefon = it },
                label = { Text("Telèfon") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (AppType.getUserType() == UserType.EMPRESA) {
                        val db = Firebase.firestore
                        val current_user = FirebaseAuth.getInstance().currentUser
                        val id_del_user = current_user?.uid
                        println(id_del_user)
                        db.collection("usersEmpresa")
                            .whereEqualTo("user_id", id_del_user)
                            .get()
                            .addOnSuccessListener {documents ->
                                for (document in documents) {
                                    if(descripcio.isNotEmpty() && descripcio != null) {
                                        document.reference.update("descripcio", descripcio)
                                    }
                                    if(nomCognoms.isNotEmpty() && nomCognoms != null) {
                                        document.reference.update("nom_cognoms", nomCognoms)
                                    }
                                    if(telefon.isNotEmpty() && telefon != null) {
                                        document.reference.update("telefon", telefon)
                                    }
                                }
                                navController.navigate(AppScreens.ProfileScreen.route)
                            }
                            .addOnFailureListener {
                                Log.e("Firestore", "Error al editar perfil")
                            }
                    }
                    else if (AppType.getUserType() == UserType.ENTITAT) {
                        val db = Firebase.firestore
                        val current_user = FirebaseAuth.getInstance().currentUser
                        val id_del_user = current_user?.uid
                        println(id_del_user)
                        db.collection("usersEntitat")
                            .whereEqualTo("user_id", id_del_user)
                            .get()
                            .addOnSuccessListener {documents ->
                                for (document in documents) {
                                    if(descripcio.isNotEmpty() && descripcio != null) {
                                        document.reference.update("descripcio", descripcio)
                                    }
                                    if(nomCognoms.isNotEmpty() && nomCognoms != null) {
                                        document.reference.update("nom_cognoms", nomCognoms)
                                    }
                                    if(telefon.isNotEmpty() && telefon != null) {
                                        document.reference.update("telefon", telefon)
                                    }
                                }
                                navController.navigate(AppScreens.ProfileScreen.route)
                            }
                            .addOnFailureListener {
                                Log.e("Firestore", "Error al editar perfil")
                            }
                    }

                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Editar")
            }
        }
    }
}


