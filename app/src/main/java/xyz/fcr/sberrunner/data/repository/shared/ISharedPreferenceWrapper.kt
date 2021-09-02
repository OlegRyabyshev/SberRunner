package xyz.fcr.sberrunner.data.repository.shared

interface ISharedPreferenceWrapper {
    fun getName(): String
    fun getWeight(): String
    fun getIntWeight(): Int

    fun isMetric(): Boolean

    fun saveName(name: String)
    fun saveWeight(weight: String)

    fun saveMapLatitude(latitude: Float)
    fun saveMapLongitude(longitude: Float)
    fun saveRunLatitude(latitude: Float)
    fun saveRunLongitude(longitude: Float)

    fun getMapLatitude(): Float
    fun getMapLongitude(): Float
    fun getRunLatitude(): Float
    fun getRunLongitude(): Float
    fun getVoiceNotificationStatus(): Boolean
}