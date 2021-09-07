package xyz.fcr.sberrunner.data.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

/**
 * Класс конвертации изображений для Room
 */
class BitmapConverter {

    /**
     * Конвертация ByteArray в Bitmap
     *
     * @param bytes [ByteArray] - байтовый массив изображения забега
     *
     * @return [Bitmap] - изображение забега
     */
    @TypeConverter
    fun toBitmap(bytes: ByteArray?): Bitmap? {
        return if (bytes != null) {
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            null
        }
    }

    /**
     * Конвертация Bitmap в ByteArray
     *
     * @param bmp [Bitmap] - изображение забега
     *
     * @return [ByteArray] - байтовый массив изображения забега
     */
    @TypeConverter
    fun fromBitmap(bmp: Bitmap?): ByteArray? {
        return if (bmp != null) {
            val outputStream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            return outputStream.toByteArray()
        } else {
            null
        }
    }
}