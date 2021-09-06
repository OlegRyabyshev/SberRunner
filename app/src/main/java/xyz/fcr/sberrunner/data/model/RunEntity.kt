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
 * @param distanceInMeters [Long] - дистанция, пройденная юзером за забег (в метрах)
 * @param timestamp [Long] - временная отметка
 * @param timeInMillis [Long] - время забега (в мс)
 * @param avgSpeedInKMH [String] - средняя скорость за забег
 * @param calories [Int] - потраченные калории за забег
 * @param mapImage [Bitmap] - отображение карты на момент конца забега
 */
@Entity(tableName = DB_NAME)
data class RunEntity(
    var avgSpeedInKMH: String = "0.0",
    var calories: Long = 0,
    var distanceInMeters: Long = 0,
    var timeInMillis: Long = 0,
    var timestamp: Long = 0L,

    @TypeConverters(BitmapConverter::class)
    var mapImage: Bitmap? = null,
    var toDeleteFlag: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}