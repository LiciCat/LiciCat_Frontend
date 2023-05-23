package com.licicat.screens

import android.location.Address
import android.location.Geocoder
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

import com.google.android.gms.maps.model.LatLng
import androidx.compose.runtime.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun MapScreen(navController: NavController, adress: String?, title: String?) {
    // Esta función @Composable llama a la función OpenMap()
    val licitacio_location = get_licitacio(adress)
    val parking_locations = get_parkings(adress)
    OpenMap(licitacio_location,parking_locations, title)
}

@Composable
fun OpenMap(licitacio_location: LatLng, parking_locations: LatLng, title: String?) {
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true, zoomGesturesEnabled = true)) }
    val context = LocalContext.current
    val geocoder = Geocoder(context)
    val results = adress?.let { geocoder.getFromLocationName(it, 1) }
    lateinit var latLng: LatLng
    var locationResult: Address? = null
    if (results != null && results.isNotEmpty()) {
        val location = results[0]
        locationResult = location
        latLng = LatLng(location.latitude, location.longitude)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(latLng, 13f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState
        ) {
            // Agrega un marcador en la ubicación encontrada
            Marker(
                state = MarkerState(position = latLng),
                title= title
            )
        }
    }
    return locationResult
}

suspend fun request_api(): String? {

    //val appToken = "KFNNxhcpu1suN4cFZSbodeiX1"
    val client = OkHttpClient()
    val url = "https://us-central1-smartcharge-81d07.cloudfunctions.net/app/aparcament?geo_epgs_4326_x=41.35&geo_epgs_4326_y=2.2&distance=4
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










