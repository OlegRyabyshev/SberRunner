package xyz.fcr.sberrunner.domain.model

import android.graphics.Bitmap

/**
 * Модель бега уровня Domain, содержащая основную информация о забеге.
 *
 * @param avgSpeedInKMH [String] - средняя скорость за забег
 * @param calories [Int] - потраченные калории за забег
 * @param distanceInMeters [Long] - дистанция, пройденная юзером за забег (в метрах)
 * @param timeInMillis [Long] - время забега (в мс)
 * @param timestamp [Long] - временная отметка
 * @param toDeleteFlag [Boolean] - отметка пользователя о том, что забег будет удалён при синхронизации
 * @param mapImage [Bitmap] - отображение карты на момент конца забега
 */
data class Run(
    var avgSpeedInKMH: String = "0.0",
    var calories: Long = 0,
    var distanceInMeters: Long = 0,
    var timeInMillis: Long = 0,
    var timestamp: Long = 0L,
    var toDeleteFlag: Boolean = false,
    var mapImage: Bitmap? = null
)