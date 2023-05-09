package com.licicat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import com.example.licicat.Licitacio


class DetailActivity : AppCompatActivity() {
    private val licitacio: Licitacio? by lazy {
        intent.getSerializableExtra(TITLE_KEY) as? Licitacio
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Text( text = "Hello ${licitacio?.nom_organ}")
        }
    }

    companion object {
        const val TITLE_KEY = "title"
        const val DESCRIPTION_KEY = "description"
        fun newIntent(context: Context, licitacio: Licitacio) =
            Intent(context, DetailActivity::class.java).apply {
                putExtra(TITLE_KEY, licitacio.nom_organ)
            }
    }
}

