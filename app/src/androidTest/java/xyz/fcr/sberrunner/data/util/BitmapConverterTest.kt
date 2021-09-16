package xyz.fcr.sberrunner.data.util

import android.graphics.BitmapFactory
import org.junit.Assert.*

import org.junit.Test
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.presentation.App

class BitmapConverterTest {

    private val bitmapConverter = BitmapConverter()

    @Test
    fun toByteArrayAndBack() {
        val byteArray = bitmapConverter.fromBitmap(bitmap)
        val image = bitmapConverter.toBitmap(byteArray)

        assertEquals(bitmap, image)
    }

    @Test
    fun fromNullByteArray() {
        val byteArray = bitmapConverter.fromBitmap(null)
        val image = bitmapConverter.toBitmap(byteArray)

        assertNull(image)
    }

    @Test
    fun fromNullBitmap() {
        val image = bitmapConverter.toBitmap(null)
        val byteArray = bitmapConverter.fromBitmap(image)

        assertNull(byteArray)
    }

    private companion object {
        private val bitmap = BitmapFactory.decodeResource(
            App.appComponent.context().resources,
            R.drawable.ic_map
        )
    }
}