package xyz.fcr.sberrunner.data.service.notification

/**
 * Интерфейс звуковых нотификаций во время бега
 */
interface IAudioNotificator {

    /**
     * Воспроизведение звуков
     *
     * @param action [String] - выбранный звук для воспроизведения
     */
    fun play(action: String)

    /**
     * Обновление громкости нотификатора по значению из SharedPreference
     */
    fun checkIfVoiceNotificationNeeded(oldDistance: Float, newDistance: Float)
}