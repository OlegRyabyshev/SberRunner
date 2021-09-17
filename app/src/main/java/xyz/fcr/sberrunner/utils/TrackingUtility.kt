package xyz.fcr.sberrunner.utils

import android.location.Location
import xyz.fcr.sberrunner.presentation.service.Polyline
import java.util.concurrent.TimeUnit

/**
 * Класс для выполнения доп. логики сервиса бега
 */
object TrackingUtility {

    /**
     * Выводит время в отформатированном виде
     *
     * @param timeInMs [Long] - время в миллисекундах
     * @return [String] - отформатированное время вида 02:02:21
     */
    fun getFormattedStopWatchTime(timeInMs: Long): String {
        var milliseconds = timeInMs
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)

        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)

        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }

    /**
     * Производит расчёт длины ломанной линии (polyline)
     *
     * @param polyline [Polyline] - ломаная линия (маршрут бега)
     * @return [Float] - длина ломаной линии
     */
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
}