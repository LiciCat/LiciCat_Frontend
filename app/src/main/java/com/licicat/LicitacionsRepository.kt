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

        //Nom organització licitació
        val no = json.optString("nom_organ", "no disponible")

        //Descripció general Licitació
        val denc = json.optString("denominacio", "no disponible")

        //Tipus contracte licitació
        val tc = json.optString("tipus_contracte", "no disponible")

        //Pressupost licitació string
        val pl = json.optInt("pressupost_licitacio", 0)
        val formattedPl = String.format(Locale.getDefault(), "%,d", pl).replace(",", ".")

        //Pressupost licitació en int
        val plI = json.optInt("pressupost_licitacio", 0)

        //Ubicació licitació
        val lle = json.optString("lloc_execucio", "no disponible")

        //Descripció del contracte
        val desc = json.optString("objecte_contracte", "no disponible")

        //Enllaç de la licitació
        val enllac_publicacio = json.optString("enllac_publicacio", "no disponible")

        //Transformacions dates
        val formatoOrigen = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val formatoDestino = SimpleDateFormat("dd/MM/yyyy")

        //Data inici licitació
        val il = json.optString("data_publicacio_anunci", "no disponible");
        var data_inici = il;
        if (!il.equals("no disponible")) {
            val il_2 = formatoOrigen.parse(il)
            data_inici = formatoDestino.format(il_2)
        }

        //Data termini licitació
        val dt = json.optString("termini_presentacio_ofertes", "no disponible");
        var data_fi = dt;
        if (!dt.equals("no disponible")) {
            val dt_2 = formatoOrigen.parse(dt)
            data_fi = formatoDestino.format(dt_2)
        }

        //Data adjudicació
        val da = json.optString("data_publicacio_adjudicacio", "no disponible");
        var data_adjudicacio = da;
        if (!da.equals("no disponible")) {
            val da_2 = formatoOrigen.parse(da)
            data_adjudicacio = formatoDestino.format(da_2)
        }


        var licitacio = Licitacio()
        licitacio.nom_organ = no;
        licitacio.denominacio = denc;
        licitacio.objecte_contracte= desc;
        licitacio.pressupost_licitacio_asString = formattedPl;
        licitacio.pressupost_licitacio = plI;
        licitacio.lloc_execucio = lle;
        licitacio.data_publicacio_anunci = data_inici;
        licitacio.termini_presentacio_ofertes = data_fi;
        licitacio.data_publicacio_adjudicacio = data_adjudicacio;
        licitacio.tipus_contracte = tc;
        licitacio.enllac_publicacio = enllac_publicacio;
        return licitacio
    }





    suspend fun request_api(): String? {

        val appToken = "KFNNxhcpu1suN4cFZSbodeiX1"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        val currentDate = Date()
        var fecha: String = dateFormat.format(currentDate)
        println(fecha)


        //fecha = "2023-04-28T13:40:00.000"
        val url = "https://analisi.transparenciacatalunya.cat/resource/a23c-d6vp.json?\$where=termini_presentacio_ofertes%3E%27$fecha%27&\$limit=60"

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

        val jsonArray = JSONTokener(request_api()).nextValue() as JSONArray

        val licitacions = mutableListOf<Licitacio>()
        val uniqueCombos = HashSet<String>()

        for (i in 0 until jsonArray.length()) {
            val licitacio = creallista(jsonArray.getJSONObject(i))
            val combo = "${licitacio.nom_organ}-${licitacio.denominacio}"
            if (combo !in uniqueCombos) {
                uniqueCombos.add(combo)
                licitacions.add(licitacio)
            }
        }

        for (elem in licitacions) {
            println("Licitacio-------------")
            println(elem.nom_organ)
            println(elem.denominacio)
            println(elem?.objecte_contracte)
            println(elem?.pressupost_licitacio_asString)
            println(elem?.lloc_execucio)
            println(elem?.data_publicacio_anunci)
            println(elem?.termini_presentacio_ofertes)
            println(elem?.data_publicacio_adjudicacio)
            println(elem?.tipus_contracte)
        }
        return licitacions
    }
}