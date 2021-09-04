package xyz.fcr.sberrunner.data.repository.shared

import android.content.SharedPreferences
import xyz.fcr.sberrunner.utils.Constants
import xyz.fcr.sberrunner.utils.Constants.MAP_LAT_KEY
import xyz.fcr.sberrunner.utils.Constants.MAP_LON_KEY
import xyz.fcr.sberrunner.utils.Constants.RUN_LAT_KEY
import xyz.fcr.sberrunner.utils.Constants.RUN_LON_KEY
import xyz.fcr.sberrunner.utils.Constants.VOICE_KEY

/**
 * Имплементация интерфейса [ISharedPreferenceWrapper]
 * Служит для упрощенного взаимодействия с SharedPreference
 *
 * @param sharedPreferences [SharedPreferences] - интерфейс для чтения и модификции значений preference
 */
class SharedPreferenceWrapper(
    private val sharedPreferences: SharedPreferences
) : ISharedPreferenceWrapper {

    /**
     * Получение текущего значения имени пользователя
     *
     * @return [String] - имя пользователя
     */
    override fun getName(): String {
        return sharedPreferences.getString(Constants.NAME_KEY, "Guest")!!
    }

    /**
     * Получение текущего значения веса пользователя
     *
     * @return [String] - вес пользователя
     */
    override fun getWeight(): String {
        return sharedPreferences.getString(Constants.WEIGHT_KEY, "70")!!
    }

    /**
     * Получение текущего значения веса пользователя
     *
     * @return [Int] - вес пользователя
     */
    override fun getIntWeight(): Int {
        val weight = sharedPreferences.getString(Constants.WEIGHT_KEY, "70")!!
        return weight.toIntOrNull() ?: 70
    }

    /**
     * Получение текущего значения заданной системы единиц
     *
     * @return [Boolean] - метрическая система: да (true) / нет (false)
     */
    override fun isMetric(): Boolean {
        val unit = sharedPreferences.getString(Constants.UNITS_KEY, Constants.METRIC_UNIT)

        if (unit == Constants.METRIC_UNIT) return true
        return false
    }

    /**
     * Сохранение имени пользователя
     *
     * @param name [String] - новое имя пользователя
     */
    override fun saveName(name: String) {
        sharedPreferences.edit().apply {
            putString("name_key", name)
            apply()
        }
    }

    /**
     * Сохранение веса пользователя
     *
     * @param weight [String] - новый вес пользователя
     */
    override fun saveWeight(weight: String) {
        sharedPreferences.edit().apply {
            putString("weight_key", weight)
            apply()
        }
    }

    /**
     * Сохранение координат долготы (для вкладки "Карта")
     *
     * @param latitude [Float] - координаты долготы
     */
    override fun saveMapLatitude(latitude: Float) {
        sharedPreferences.edit().apply {
            putFloat(MAP_LAT_KEY, latitude)
            apply()
        }
    }

    /**
     * Сохранение координат широты (для вкладки "Карта")
     *
     * @param longitude [Float] - координаты широты
     */
    override fun saveMapLongitude(longitude: Float) {
        sharedPreferences.edit().apply {
            putFloat(MAP_LON_KEY, longitude)
            apply()
        }
    }

    /**
     * Сохранение координат долготы (для вкладки "Бег")
     *
     * @param latitude [Float] - координаты долготы
     */
    override fun saveRunLatitude(latitude: Float) {
        sharedPreferences.edit().apply {
            putFloat(RUN_LAT_KEY, latitude)
            apply()
        }
    }

    /**
     * Сохранение координат широты (для вкладки "Бег")
     *
     * @param longitude [Float] - координаты широты
     */
    override fun saveRunLongitude(longitude: Float) {
        sharedPreferences.edit().apply {
            putFloat(RUN_LON_KEY, longitude)
            apply()
        }
    }

    /**
     * Получение координат долготы (для вкладки "Карта")
     *
     * @return [Float] - координаты долготы
     */
    override fun getMapLatitude(): Float {
        return sharedPreferences.getFloat(MAP_LAT_KEY, Constants.MOSCOW_LAT)
    }

    /**
     * Получение координат широты (для вкладки "Карта")
     *
     * @return [Float] - координаты широты
     */
    override fun getMapLongitude(): Float {
        return sharedPreferences.getFloat(MAP_LON_KEY, Constants.MOSCOW_LON)
    }

    /**
     * Получение координат долготы (для вкладки "Бег")
     *
     * @return [Float] - координаты долготы
     */
    override fun getRunLatitude(): Float {
        return sharedPreferences.getFloat(RUN_LAT_KEY, Constants.MOSCOW_LAT)
    }

    /**
     * Получение координат широты (для вкладки "Бег")
     *
     * @return [Float] - координаты широты
     */
    override fun getRunLongitude(): Float {
        return sharedPreferences.getFloat(RUN_LON_KEY, Constants.MOSCOW_LON)
    }

    /**
     * Получение значения голосового нотификатора
     *
     * @return [Boolean] - нотификатор работает (true) / нотификатор выключен (false)
     */
    override fun getVoiceNotificationStatus(): Boolean {
        return sharedPreferences.getBoolean(VOICE_KEY, true)
    }
}