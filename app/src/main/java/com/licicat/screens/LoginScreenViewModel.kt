package com.licicat.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.licicat.model.usersEmpresa
import kotlinx.coroutines.launch

class LoginScreenViewModel: ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)

    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        context: Context,
        home: () -> Unit
    )
    = viewModelScope.launch {
       try {

           auth.signInWithEmailAndPassword(email, password)
               .addOnCompleteListener { task->
                   try {
                       if (task.isSuccessful){
                           Log.d("Licicat", "Logeado")
                           home()
                       }
                       else{
                           Log.d("Licicat", "Error: ${task.result.toString()}")

                           Toast.makeText(context, "Inici de sessió incorrecte.",
                               Toast.LENGTH_SHORT).show()
                       }
                   } catch (e: Exception) {
                       Log.d("Licicat", "Excepcion-log: ${e.message}")
                       Toast.makeText(context, "Inici de sessió incorrecte.",
                           Toast.LENGTH_SHORT).show()
                   }
               }

       }
        catch (e:Exception){
            Log.d("Licicat", "Excepcion-log: ${e.message}")
            Toast.makeText(context, "Inici de sessió incorrecte.",
                Toast.LENGTH_SHORT).show()
        }

    }

    fun createUserWithEmailAndPassword(email: String, password: String,  nom: String, empresa: String, nif: String, telefon: String, context: Context, home: () -> Unit){
        if(_loading.value == false){
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task->
                    try{
                        if (task.isSuccessful){
                            crateUser(email, nom, empresa, nif, telefon)
                            home()
                        }
                        else{
                            Log.d("Licicat", "Error: ${task.result.toString()}")
                            Toast.makeText(context, "Registre incorrecte.",
                                Toast.LENGTH_SHORT).show()
                        }
                        _loading.value = false
                    }
                    catch (e: Exception) {
                        Log.d("Licicat", "Excepcion-log: ${e.message}")
                       Toast.makeText(context, "Registre incorrecte.",
                            Toast.LENGTH_SHORT).show()
                    }

                }
        }
    }

    private fun crateUser(
        email: String,
        nom: String,
        empresa: String,
        nif: String,
        telefon: String
    ) {
        val userId = auth.currentUser?.uid
        val userEmpresa = usersEmpresa(
            userId = userId.toString(),
            Empresa = empresa,
            Nom = nom,
            email = email,
            avatarUrl = "",
            NIF =  nif,
            Telefon = telefon,
            id = null
        ).toMap()

        FirebaseFirestore.getInstance().collection("usersEmpresa")
            .add(userEmpresa)
            .addOnSuccessListener {
                Log.d("LiciCat", "Creat ${it.id}")
            }.addOnFailureListener{
                Log.d("LiciCat", "Error ${it}")
            }
    }


}