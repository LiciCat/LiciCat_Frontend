
package com.licicat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.licicat.navigation.AppNavigation
import com.licicat.screens.*
import com.licicat.ui.theme.LiciCatTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiciCatTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    AppNavigation()
                }
            }
        }
        setup()
    }

    private fun setup(){
        var email: String = getStringEmail()
        var nom: String = getStringNom()
        var password: String = getStringPassword()
        var empresa: String = getStringEmpresa()
        var nif: String = getStringNif()
        var telefon: String = getStringTelefon()
    }
}
