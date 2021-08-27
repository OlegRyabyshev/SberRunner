package xyz.fcr.sberrunner.utils

import android.content.Context
import android.location.Location
import pub.devrel.easypermissions.EasyPermissions
import xyz.fcr.sberrunner.data.service.Polyline
import xyz.fcr.sberrunner.utils.Constants.RUN_PERMISSIONS
import java.util.concurrent.TimeUnit

class TrackingUtility {

    companion object {

        fun getFormattedStopWatchTime(ms: Long): String {
            var milliseconds = ms
            val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)

            milliseconds -= TimeUnit.HOURS.toMillis(hours)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)

            milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

            return "${if (hours < 10) "0" else ""}$hours:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds"
        }

        fun calculatePolylineLength(polyline: Polyline): Float {
            var distance = 0f
            for (i in 0..polyline.size - 2) {
                val pos1 = polyline[i]
                val pos2 = polyline[i + 1]
                val result = FloatArray(1)
                Location.distanceBetween(
                    pos1.latitude,
                    pos1.longitude,
                    pos2.latitude,
                    pos2.longitude,
                    result
                )
                distance += result[0]
            }
            return distance
        }

        fun hasLocationPermissions(context: Context): Boolean {
            return EasyPermissions.hasPermissions(context, *RUN_PERMISSIONS)
        }
    }
}