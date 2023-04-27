package com.licicat.screens

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


@Composable
fun MapScreen(navController: NavController, adress: String?, title: String?) {
    // Esta función @Composable llama a la función OpenMap()
    OpenMap(adress, title)
}

@Composable
fun OpenMap(adress: String?, title: String?) {
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true, zoomGesturesEnabled = true)) }
    val context = LocalContext.current
    val geocoder = Geocoder(context)
    val results = adress?.let { geocoder.getFromLocationName(it, 1) }
    lateinit var latLng: LatLng
    if (results != null && results.isNotEmpty()) {
        val location = results[0]
        latLng = LatLng(location.latitude, location.longitude)
    }
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








