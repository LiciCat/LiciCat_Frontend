package com.licicat.screens

import android.graphics.Color
import android.location.Geocoder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.*

import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException


@Composable
fun MapScreen(navController: NavController, adress: String?, title: String?) {
    // Esta función @Composable llama a la función OpenMap()
    val context = LocalContext.current
    val geocoder = Geocoder(context)
    val results = adress?.let { geocoder.getFromLocationName(it, 1) }
    lateinit var latLng: LatLng
    if (results != null && results.isNotEmpty()) {
        val location = results[0]
        latLng = LatLng(location.latitude, location.longitude)
    }

    val parkingLocationsState = remember { mutableStateOf<List<LatLng>>(emptyList()) }

    LaunchedEffect(Unit) {
        val parkings = getParkingsAsync(latLng)
        parkingLocationsState.value = parkings
    }

    val parkingLocations = parkingLocationsState.value.toMutableList()

    OpenMap(latLng, parkingLocations, title)
}


@Composable
fun OpenMap(licitacio_location: LatLng, parking_locations: MutableList<LatLng>, title: String?) {
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true, zoomGesturesEnabled = true)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(licitacio_location, 15f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        uiSettings = uiSettings,
        cameraPositionState = cameraPositionState
    ) {
        // Agrega un marcador en la ubicación encontrada
        Marker(
            state = MarkerState(position = licitacio_location),
            title = title
        )

        // Agrega marcadores para cada ubicación de estacionamiento
        parking_locations.forEachIndexed { index, location ->
            Marker(
                state = MarkerState(position = location),
                title = "Parking ${index + 1}",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
            )
        }
    }
}

suspend fun getParkingsAsync(licitacio_location: LatLng): MutableList<LatLng> {
    if ((licitacio_location.latitude in 41.336257..41.439032) && (licitacio_location.longitude in 2.096813..2.242154)) {
        val responseBody = request_api(licitacio_location)
        val json = JSONTokener(responseBody).nextValue() as JSONObject
        val result = json.getJSONObject("result")
        val recordsArray: JSONArray = result.getJSONArray("records")
        val parkings = mutableListOf<LatLng>()

        for (i in 0 until recordsArray.length()) {
            val jsonRecord = recordsArray.getJSONObject(i)
            val latitudeStr = jsonRecord.optString("geo_epgs_4326_x", "no disponible")
            val longitudeStr = jsonRecord.optString("geo_epgs_4326_y", "no disponible")

            val latitude = latitudeStr.toDoubleOrNull()
            val longitude = longitudeStr.toDoubleOrNull()

            if (latitude != null && longitude != null) {
                val latLng = LatLng(latitude, longitude)
                parkings.add(latLng)
            }
        }
        return parkings
    }
    return emptyList<LatLng>().toMutableList()
}

suspend fun request_api(licitacio_location: LatLng): String? {
    val client = OkHttpClient()
    val url = "https://us-central1-smartcharge-81d07.cloudfunctions.net/app/aparcament?geo_epgs_4326_x=${licitacio_location.latitude}&geo_epgs_4326_y=${licitacio_location.longitude}&distance=1"
    val request = Request.Builder()
        .url(url)
        .build()
    val call = client.newCall(request)

    var responseBody: String? = null
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




