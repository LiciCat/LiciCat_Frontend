
package com.licicat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.licicat.navigation.AppNavigation
import com.licicat.ui.theme.LiciCatTheme
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import com.example.licicat.Licitacio
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiciCatTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    notificacio()
                    AppNavigation()
                }
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

