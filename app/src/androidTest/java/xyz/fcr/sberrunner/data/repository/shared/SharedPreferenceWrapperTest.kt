package xyz.fcr.sberrunner.data.repository.shared

import android.content.Context
import android.content.Context.MODE_PRIVATE
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import xyz.fcr.sberrunner.presentation.App

class SharedPreferenceWrapperTest {
    private lateinit var sharedPrefs: SharedPreferenceWrapper
    private lateinit var context: Context

    @Before
    fun setup() {
        context = App.appComponent.context()

        val sharedPreferences = context.getSharedPreferences(
            "prefs_test",
            MODE_PRIVATE
        )

        sharedPreferences
            .edit()
            .clear()
            .commit()

        sharedPrefs = SharedPreferenceWrapper(sharedPreferences)
    }

    @Test
    fun getDefaultName() {
        val actual = sharedPrefs.getName()

        assertEquals(DEFAULT_NAME, actual)
    }

    @Test
    fun saveAndGetName() {
        sharedPrefs.saveName(NAME)

        val actual = sharedPrefs.getName()

        assertEquals(NAME, actual)
    }

    @Test
    fun getDefaultWeight() {
        val actual = sharedPrefs.getWeight()

        assertEquals(DEFAULT_WEIGHT, actual)
    }

    @Test
    fun saveAndGetWeight() {
        sharedPrefs.saveWeight(WEIGHT)

        val actual = sharedPrefs.getWeight()

        assertEquals(WEIGHT, actual)
    }

    @Test
    fun saveAndGetIntWeight() {
        sharedPrefs.saveWeight(WEIGHT)

        val actual = sharedPrefs.getIntWeight()

        assertEquals(WEIGHT, actual.toString())
    }

    @Test
    fun getDefaultIntWeight() {
        val actual = sharedPrefs.getIntWeight()

        assertEquals(DEFAULT_WEIGHT_INT, actual)
    }

    @Test
    fun saveAndGetIsMetric() {
        val actual = sharedPrefs.isMetric()

        assertEquals(IS_METRIC, actual)
    }

    @Test
    fun saveAndGetMapLatitude() {
        sharedPrefs.saveMapLatitude(COORDINATE)

        val actual = sharedPrefs.getMapLatitude()

        assertEquals(COORDINATE, actual)
    }

    @Test
    fun saveAndGetMapLongitude() {
        sharedPrefs.saveMapLongitude(COORDINATE)

        val actual = sharedPrefs.getMapLongitude()

        assertEquals(COORDINATE, actual)
    }

    @Test
    fun saveAndGetRunLatitude() {
        sharedPrefs.saveRunLatitude(COORDINATE)

        val actual = sharedPrefs.getRunLatitude()

        assertEquals(COORDINATE, actual)
    }

    @Test
    fun saveAndGetRunLongitude() {
        sharedPrefs.saveRunLongitude(COORDINATE)

        val actual = sharedPrefs.getRunLongitude()

        assertEquals(COORDINATE, actual)
    }

    @Test
    fun getVoiceNotificationStatus() {
        val actual = sharedPrefs.getVoiceNotificationStatus()

        assertEquals(NOTIFICATION_ENABLED, actual)
    }

    @Test
    fun getDefaultErrorTheme() {
        val actual = sharedPrefs.getTheme()

        assertEquals(ERROR_THEME, actual)
    }

    @Test
    fun getUnits() {
        val actual = sharedPrefs.getUnits()

        assertEquals(ERROR_UNITS, actual)
    }

    private companion object {
        private const val NAME = "Bob"
        private const val WEIGHT = "140"
        private const val COORDINATE = 50f

        private const val DEFAULT_NAME = "Guest"
        private const val DEFAULT_WEIGHT = "70"
        private const val DEFAULT_WEIGHT_INT = 70

        private const val NOTIFICATION_ENABLED = true
        private const val IS_METRIC = true
        private const val ERROR_THEME = "Error in theme"
        private const val ERROR_UNITS = "Error in units"
    }
}