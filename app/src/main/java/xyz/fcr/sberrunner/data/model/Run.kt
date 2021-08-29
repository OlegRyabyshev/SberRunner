package xyz.fcr.sberrunner.data.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import xyz.fcr.sberrunner.data.util.Converters
import xyz.fcr.sberrunner.utils.Constants.DB_NAME

@Entity(tableName = DB_NAME)
data class Run(
    var distanceInMeters: Int = 0,
    var timestamp: Long = 0L,
    var timeInMillis: Long = 0,
    var avgSpeedInKMH: Float = 0f,
    var calories: Int = 0,
    @TypeConverters(Converters::class)
    var mapImage: Bitmap? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}