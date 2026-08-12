package com.ralphmarondev.velora.features.dashboard.presentation.component

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("UnrememberedMutableState")
@Composable
fun GoogleMapComponent(
    isTraffic: Boolean = false,
    isUnderConstruction: Boolean = false
) {
    LaunchedEffect(Unit) {
        Log.d("Dashboard", "Is traffic: $isTraffic")
        Log.d("Dashboard", "Is under construction: $isUnderConstruction")
    }

    val smDowntown = LatLng(17.6137, 121.7267)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            smDowntown, 16f
        )
    }
    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(smDowntown),
            title = "SM Center Tuguegarao Downtown"
        )
    }
}