
package com.licicat

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.licicat.navigation.AppNavigation
import com.licicat.ui.theme.LiciCatTheme
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import com.example.licicat.Licitacio
import com.google.firebase.messaging.FirebaseMessaging
import com.licicat.navigation.AppScreens


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiciCatTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {



                    notificacio()
                    onNewIntent(intent)
                    manejarDatosDeIntent(intent)
                    AppNavigation(intent)

                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        // Actualizar la referencia al último Intent
        setIntent(intent)

        manejarDatosDeIntent(intent)
    }

    override fun onResume() {
        super.onResume()

        manejarDatosDeIntent(intent)
    }

    private fun manejarDatosDeIntent(intent: Intent?) {
        intent?.let {
            if (it.hasExtra("destinacion")) {
                val destination = it.getStringExtra("destinacion")
                println("MAIN destinacion new: $destination")
                val entitat = it.getStringExtra("nom_entitat")
                println("MAIN entitat new: $entitat")
                // Aquí puedes realizar las operaciones necesarias con los datos del intent
            }
        }
    }




}




private fun notificacio() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val token = task.result
            token?.let {
                println("Este es mi token del dispositivo: $it")
            }
        } else {
            println("Error al obtener el token del dispositivo: ${task.exception}")
        }
    }
}

