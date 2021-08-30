package xyz.fcr.sberrunner.data.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import xyz.fcr.sberrunner.data.util.BitmapConverter
import xyz.fcr.sberrunner.utils.Constants.DB_NAME

/**
 * Модель бега, содержущая основную информация о забеге.
 *
 * @param distanceInMeters [Int] - дистанция, пройденная юзером за забег (в метрах)
 * @param timestamp [Long] - временная отметка
 * @param timeInMillis [Long] - время забега (в мс)
 * @param avgSpeedInKMH [Float] - средняя скорость за забег
 * @param calories [Int] - потраченные калории за забег
 * @param mapImage [Bitmap] - отображение карты на момент конца забега
 *
 */
@Entity(tableName = DB_NAME)
data class Run(
    var distanceInMeters: Int = 0,
    var timestamp: Long = 0L,
    var timeInMillis: Long = 0,
    var avgSpeedInKMH: Float = 0f,
    var calories: Int = 0,
    @TypeConverters(BitmapConverter::class)
    var mapImage: Bitmap? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}