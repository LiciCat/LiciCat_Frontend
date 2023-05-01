package com.licicat

import com.example.licicat.Licitacio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class LicitacionsRepository {
    private var licitaciones: List<Licitacio>? = null
    val limit = 5
    private val client = OkHttpClient()

    fun creallista(json: JSONObject): Licitacio {

        val no = json.optString("nom_organ", "no disponible")
        val tc = json.optString("tipus_contracte", "no disponible")
        val pl = json.optInt("pressupost_licitacio", 0)
        val formattedPl = String.format(Locale.getDefault(), "%,d", pl).replace(",", ".")
        val lle = json.optString("lloc_execucio", "no disponible")
        val desc = json.optString("objecte_contracte", "no disponible")
        val tp = json.optString("termini_presentacio_ofertes", "no disponible");

        val formatoOrigen = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val fechaOrigen = formatoOrigen.parse(tp)

        val formatoDestino = SimpleDateFormat("dd/MM/yyyy")
        val fechaDestino = formatoDestino.format(fechaOrigen)

        var licitacio = Licitacio()
        licitacio.nom_organ = no;
        licitacio.termini_presentacio_ofertes = fechaDestino
        licitacio.tipus_contracte = tc;
        licitacio.pressupost_licitacio_asString = formattedPl;
        licitacio.lloc_execucio = lle;
        licitacio.objecte_contracte= desc;

        return licitacio
    }





    suspend fun request_api(): String? {

        val appToken = "KFNNxhcpu1suN4cFZSbodeiX1"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        val currentDate = Date()
        var fecha: String = dateFormat.format(currentDate)
        println(fecha)


        //fecha = "2023-04-28T13:40:00.000"
        val url = "https://analisi.transparenciacatalunya.cat/resource/a23c-d6vp.json?\$where=termini_presentacio_ofertes%3E%27$fecha%27&\$limit=100"

        val request = Request.Builder()
            .url(url)
            .build()
        val call = client.newCall(request)

        var responseBody: String? = ""
        val job = CoroutineScope(Dispatchers.IO).async {
            try {
                val response = call.execute()
                responseBody = response.body()?.string()
                println(response.isSuccessful)
                if (response.isSuccessful/* && responseBody != null*/) {
                    println("SUCCESS")
                    println(responseBody)
                }
            } catch (e: IOException) {
                println("ERROR")
                e.printStackTrace()

            }
        }

        job.join()
        println("SORTIDA")
        println(responseBody)

        return responseBody
    }

    suspend fun getLicitacions(): List<Licitacio> {


        println("---DATA DONE---")

        println("---FINAL API---")






        val jsonArray = JSONTokener(request_api()).nextValue() as JSONArray

        val licitacions = List(jsonArray.length()) { i ->
            creallista(jsonArray.getJSONObject(i))
        }
        println("llista feta")
        for (elem in licitacions) {
            println("Licitacio-------------")
            println(elem.nom_organ)
            println(elem?.objecte_contracte)
            println(elem?.pressupost_licitacio_asString)
            println(elem?.termini_presentacio_ofertes)
            println(elem?.lloc_execucio)
        }
        return licitacions
    }
}