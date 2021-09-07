package xyz.fcr.sberrunner.data.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

/**
 * Класс конвертации изображений для Room
 */
class BitmapConverter : IBitmapConverter {

    /**
     * Конвертация ByteArray в Bitmap
     *
     * @return Bitmap
     */
    @TypeConverter
    override fun toBitmap(bytes: ByteArray?): Bitmap? {
        return if (bytes != null) {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            null
        }
    }

    /**
     * Конвертация Bitmap в ByteArray
     *
     * @return ByteArray
     */
    @TypeConverter
    override fun fromBitmap(bmp: Bitmap?): ByteArray? {
        return if (bmp != null) {
            val outputStream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            return outputStream.toByteArray()
        } else {
            null
        }
    }
}