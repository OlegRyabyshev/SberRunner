package xyz.fcr.sberrunner.data.room

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "sber_runner_table")
data class RunEntity(
    var timestamp: Long = 0L,
    var avgSpeedInKMH: Float = 0f,
    var distanceInMeters: Int = 0,
    var timeInMillis: Long = 0,
    var calories: Int = 0,
    @TypeConverters(Converters::class)
    var mapImage: Bitmap? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}