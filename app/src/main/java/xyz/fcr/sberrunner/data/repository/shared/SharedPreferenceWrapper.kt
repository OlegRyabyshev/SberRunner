package xyz.fcr.sberrunner.data.repository.shared

import android.content.SharedPreferences
import xyz.fcr.sberrunner.utils.Constants
import xyz.fcr.sberrunner.utils.Constants.MAP_LAT_KEY
import xyz.fcr.sberrunner.utils.Constants.MAP_LON_KEY
import xyz.fcr.sberrunner.utils.Constants.RUN_LAT_KEY
import xyz.fcr.sberrunner.utils.Constants.RUN_LON_KEY
import xyz.fcr.sberrunner.utils.Constants.VOICE_KEY

class SharedPreferenceWrapper(
    private val sharedPreferences: SharedPreferences
) : ISharedPreferenceWrapper {

    override fun getName(): String {
        return sharedPreferences.getString(Constants.NAME_KEY, "Guest")!!
    }

    override fun getWeight(): String {
        return sharedPreferences.getString(Constants.WEIGHT_KEY, "70")!!
    }

    override fun getIntWeight(): Int {
        val weight = sharedPreferences.getString(Constants.WEIGHT_KEY, "70")!!
        return weight.toIntOrNull() ?: 70
    }

    override fun isMetric(): Boolean {
        val unit = sharedPreferences.getString(Constants.UNITS_KEY, Constants.METRIC_UNIT)

        if (unit == Constants.METRIC_UNIT) return true
        return false
    }

    override fun saveName(name: String) {
        sharedPreferences.edit().apply {
            putString("name_key", name)
            apply()
        }
    }

    override fun saveWeight(weight: String) {
        sharedPreferences.edit().apply {
            putString("weight_key", weight)
            apply()
        }
    }

    override fun saveMapLatitude(latitude: Float) {
        sharedPreferences.edit().apply {
            putFloat(MAP_LAT_KEY, latitude)
            apply()
        }
    }

    override fun saveMapLongitude(longitude: Float) {
        sharedPreferences.edit().apply {
            putFloat(MAP_LON_KEY, longitude)
            apply()
        }
    }

    override fun saveRunLatitude(latitude: Float) {
        sharedPreferences.edit().apply {
            putFloat(RUN_LAT_KEY, latitude)
            apply()
        }
    }

    override fun saveRunLongitude(longitude: Float) {
        sharedPreferences.edit().apply {
            putFloat(RUN_LON_KEY, longitude)
            apply()
        }
    }

    override fun getMapLatitude(): Float {
        return sharedPreferences.getFloat(MAP_LAT_KEY, Constants.MOSCOW_LAT)
    }

    override fun getMapLongitude(): Float {
        return sharedPreferences.getFloat(MAP_LON_KEY, Constants.MOSCOW_LON)
    }

    override fun getRunLatitude(): Float {
        return sharedPreferences.getFloat(RUN_LAT_KEY, Constants.MOSCOW_LAT)
    }

    override fun getRunLongitude(): Float {
        return sharedPreferences.getFloat(RUN_LON_KEY, Constants.MOSCOW_LON)
    }

    override fun getVoiceNotificationStatus(): Boolean {
        return sharedPreferences.getBoolean(VOICE_KEY, true)
    }
}