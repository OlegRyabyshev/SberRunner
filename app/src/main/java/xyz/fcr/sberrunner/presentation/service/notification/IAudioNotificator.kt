package xyz.fcr.sberrunner.presentation.service.notification

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
     * Функция нотификации пользователя при пересечении пограничных значений в дистанициях
     *
     * @param oldDistance [Float] - прошлая обновленная дистанция
     * @param newDistance [Float] - текущаая обновленная дистанция
     */
    fun checkIfVoiceNotificationNeeded(oldDistance: Float, newDistance: Float)
}