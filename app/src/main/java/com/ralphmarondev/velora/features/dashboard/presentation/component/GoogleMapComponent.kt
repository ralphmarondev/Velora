package com.ralphmarondev.velora.features.dashboard.presentation.component

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.ralphmarondev.velora.BuildConfig
import com.ralphmarondev.velora.core.common.DirectionUtils

@SuppressLint("UnrememberedMutableState")
@Composable
fun GoogleMapComponent(
    isTraffic: Boolean = false,
    isUnderConstruction: Boolean = false
) {
    val cathedralCorner = remember { LatLng(17.6134, 121.7298) }
    val mallOfTheValley = remember { LatLng(17.6162, 121.7285) }

    var routeToMov by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    LaunchedEffect(Unit) {
        Log.d(
            "Dashboard",
            "Fetching road path... Traffic: $isTraffic | Construction: $isUnderConstruction"
        )

        val points = DirectionUtils.getRoutePoints(
            origin = cathedralCorner,
            destination = mallOfTheValley,
            apiKey = BuildConfig.MAPS_API_KEY
        )

        if (points.isNotEmpty()) {
            routeToMov = points
            Log.d("Dashboard", "Road route loaded: ${points.size} points")
        } else {
            Log.e("Dashboard", "Failed to fetch road points. Using fallback.")
        }
    }

    val routeColor = when {
        isUnderConstruction -> Color(0xFFE65100) // Orange
        isTraffic -> Color(0xFFD32F2F)            // Red
        else -> Color(0xFF2E7D32)                 // Green
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cathedralCorner, 16f)
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = cathedralCorner),
            title = "St. Peter Metropolitan Cathedral (Corner)",
            snippet = "Rizal St / Corner, Tuguegarao City"
        )
        Marker(
            state = MarkerState(position = mallOfTheValley),
            title = "Mall of the Valley (MOV)",
            snippet = "Tuguegarao City, Cagayan"
        )
        if (routeToMov.isNotEmpty()) {
            Polyline(
                points = routeToMov,
                color = routeColor,
                width = 12f
            )
        }
    }
}