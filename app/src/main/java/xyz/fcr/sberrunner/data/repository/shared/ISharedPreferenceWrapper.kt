package xyz.fcr.sberrunner.data.repository.shared

/**
 * Интерфейс упрощенного взаимодействия с SharedPreference
 */
interface ISharedPreferenceWrapper {

    /**
     * Получение текущего значения имени пользователя
     *
     * @return [String] - имя пользователя
     */
    fun getName(): String

    /**
     * Получение текущего значения веса пользователя
     *
     * @return [String] - вес пользователя
     */
    fun getWeight(): String

    /**
     * Получение текущего значения веса пользователя
     *
     * @return [Int] - вес пользователя
     */
    fun getIntWeight(): Int

    /**
     * Получение текущего значения заданной системы единиц
     *
     * @return [Boolean] - метрическая система: да (true) / нет (false)
     */
    fun isMetric(): Boolean

    /**
     * Сохранение имени пользователя
     *
     * @param name [String] - новое имя пользователя
     */
    fun saveName(name: String)

    /**
     * Сохранение веса пользователя
     *
     * @param weight [String] - новый вес пользователя
     */
    fun saveWeight(weight: String)

    /**
     * Сохранение координат долготы (для вкладки "Карта")
     *
     * @param latitude [Float] - координаты долготы
     */
    fun saveMapLatitude(latitude: Float)

    /**
     * Сохранение координат широты (для вкладки "Карта")
     *
     * @param longitude [Float] - координаты широты
     */
    fun saveMapLongitude(longitude: Float)

    /**
     * Сохранение координат долготы (для вкладки "Бег")
     *
     * @param latitude [Float] - координаты долготы
     */
    fun saveRunLatitude(latitude: Float)

    /**
     * Сохранение координат широты (для вкладки "Бег")
     *
     * @param longitude [Float] - координаты широты
     */
    fun saveRunLongitude(longitude: Float)

    /**
     * Получение координат долготы (для вкладки "Карта")
     *
     * @return [Float] - координаты долготы
     */
    fun getMapLatitude(): Float

    /**
     * Получение координат широты (для вкладки "Карта")
     *
     * @return [Float] - координаты широты
     */
    fun getMapLongitude(): Float

    /**
     * Получение координат долготы (для вкладки "Бег")
     *
     * @return [Float] - координаты долготы
     */
    fun getRunLatitude(): Float

    /**
     * Получение координат широты (для вкладки "Бег")
     *
     * @return [Float] - координаты широты
     */
    fun getRunLongitude(): Float

    /**
     * Получение значения голосового нотификатора
     *
     * @return [Boolean] - нотификатор работает (true) / нотификатор выключен (false)
     */
    fun getVoiceNotificationStatus(): Boolean

    /**
     * Получение значения текущей темы
     *
     * @return [String] - текущая тема
     */
    fun getTheme(): String

    /**
     * Получение значения системы измерений
     *
     * @return [String] - текущая система измерений
     */
    fun getUnits(): String
}