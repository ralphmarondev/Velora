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
    // Start at MOV, Destination is St. Peter Metropolitan Cathedral
    val mallOfTheValley = remember { LatLng(17.614390109528323, 121.7277353658289) }
    val cathedralCorner = remember { LatLng(17.6134, 121.7298) }

    // Waypoint Landmark Coordinates
    val jomarsWaypoint = remember { LatLng(17.6131, 121.7295) }    // Jomar's Batil Patung area
    val hotelRomaWaypoint = remember { LatLng(17.6128, 121.7278) } // Hotel Roma area (Luna St)

    var routeToCathedral by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    LaunchedEffect(isTraffic, isUnderConstruction) {
        Log.d(
            "Dashboard",
            "Fetching route from MOV to Cathedral... Traffic: $isTraffic | Construction: $isUnderConstruction"
        )

        // Select intermediate driving waypoint based on road condition toggles
        val selectedWaypoint = if (isTraffic || isUnderConstruction) {
            listOf(hotelRomaWaypoint) // Detour via Hotel Roma
        } else {
            listOf(jomarsWaypoint)    // Direct/default route via Jomar's
        }

        val points = DirectionUtils.getRoutePoints(
            origin = mallOfTheValley,
            destination = cathedralCorner,
            apiKey = BuildConfig.MAPS_API_KEY,
            waypoints = selectedWaypoint,
            mode = "driving"
        )

        if (points.isNotEmpty()) {
            routeToCathedral = points
            Log.d("Dashboard", "Driving route loaded: ${points.size} points")
        } else {
            Log.e("Dashboard", "Failed to fetch road points. Using fallback.")
            routeToCathedral = listOf(mallOfTheValley) + selectedWaypoint + listOf(cathedralCorner)
        }
    }

    val routeColor = Color(0xFF2E7D32)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(mallOfTheValley, 16f)
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = mallOfTheValley),
            title = "Mall of the Valley (MOV)",
            snippet = "Starting Point"
        )
        Marker(
            state = MarkerState(position = cathedralCorner),
            title = "St. Peter Metropolitan Cathedral",
            snippet = "Target Destination"
        )

        if (routeToCathedral.isNotEmpty()) {
            Polyline(
                points = routeToCathedral,
                color = routeColor,
                width = 12f
            )
        }
    }
}