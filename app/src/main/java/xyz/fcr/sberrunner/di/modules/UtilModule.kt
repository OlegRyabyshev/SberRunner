package xyz.fcr.sberrunner.di.modules

import android.media.MediaPlayer
import dagger.Module
import dagger.Provides
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.data.service.notification.AudioNotificator
import xyz.fcr.sberrunner.data.service.notification.IAudioNotificator
import xyz.fcr.sberrunner.utils.ISchedulersProvider
import xyz.fcr.sberrunner.utils.SchedulersProvider
import javax.inject.Singleton

/**
 * Модуль Util, предоставляющий различные зависимости
 */
@Module
object UtilModule {

    /**
     * Предоставление ISchedulersProvider
     *
     * @return [ISchedulersProvider] - интерфейс выполенения задач на разных потоках
     */
    @Singleton
    @Provides
    fun provideSchedulersProvider(): ISchedulersProvider {
        return SchedulersProvider()
    }

    /**
     * Предоставление объекта AudioNotificator
     *
     * @param mediaPlayer [MediaPlayer] - объект воспроизведения звуков в сервисе
     * @param sharedPreferences [ISharedPreferenceWrapper] - дефолтный объект SharedPreference
     *
     * @return [AudioNotificator] - объект воспроизведения уведомлений бега
     */
    @Singleton
    @Provides
    fun provideAudioNotificator(
        mediaPlayer: MediaPlayer,
        sharedPreferences: ISharedPreferenceWrapper
    ): IAudioNotificator {
        return AudioNotificator(mediaPlayer, sharedPreferences)
    }

    /**
     * Предоставление объекта MediaPlayer
     *
     * @return [MediaPlayer] - объект воспроизведения звуков в сервисе
     */
    @Singleton
    @Provides
    fun provideMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }
}