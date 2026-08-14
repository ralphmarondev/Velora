package com.ralphmarondev.velora.core.common

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object DirectionUtils {
    suspend fun getRoutePoints(
        origin: LatLng,
        destination: LatLng,
        apiKey: String
    ): List<LatLng> {
        return withContext(Dispatchers.IO) {
            val url = "https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin=${origin.latitude},${origin.longitude}" +
                    "&destination=${destination.latitude},${destination.longitude}" +
                    "&key=$apiKey"

            try {
                val response = URL(url).readText()
                val json = JSONObject(response)
                val routes = json.getJSONArray("routes")

                if (routes.length() > 0) {
                    val polylinePoints = routes.getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points")
                    Log.d("DirectionUtils", "$polylinePoints")
                    return@withContext decodePolyline(polylinePoints)
                }
            } catch (e: Exception) {
                Log.e("DirectionUtils", "${e.message}")
            }

            emptyList()
        }
    }

    /**
     * Decodes an encoded Google Maps Polyline string into a list of LatLng coordinates.
     */
    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }
}