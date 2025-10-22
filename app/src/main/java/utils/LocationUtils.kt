package utils

import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Calculates the distance between two points/pins using Haversine formula
 * Returns the distance in meters
 */

object LocationUtils {

    fun calculateDistance(point1: LatLng, point2: LatLng): Double {
        val earthRadiusKm = 6371.0

        val dLat = Math.toRadians(point2.latitude - point1.latitude)
        val dLon = Math.toRadians(point2.longitude - point1.longitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(point1.latitude)) *
                cos(Math.toRadians(point2.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadiusKm * c * 1000
    }

}