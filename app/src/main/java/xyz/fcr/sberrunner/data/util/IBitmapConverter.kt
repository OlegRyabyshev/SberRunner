package xyz.fcr.sberrunner.data.util

import android.graphics.Bitmap

/**
 * Класс конвертации изображений для Room
 */
interface IBitmapConverter {

    /**
     * Конвертация ByteArray в Bitmap
     *
     * @return Bitmap
     */
    fun toBitmap(bytes: ByteArray): Bitmap

    /**
     * Конвертация Bitmap в ByteArray
     *
     * @return ByteArray
     */
    fun fromBitmap(bmp: Bitmap): ByteArray
}