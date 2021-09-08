package xyz.fcr.sberrunner.di.modules

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.presentation.view.activities.MainActivity
import xyz.fcr.sberrunner.utils.Constants
import javax.inject.Singleton

/**
 * Модуль приложения, предоставляет контекст и другие Android зависимости
 *
 * @param application [Application] - базовый класс для поддержания глобального состояния приложения
 */
@Module
class AppModule(private val application: Application) {

    /**
     * Предоставление Application
     *
     * @return [Application] - базовый класс для поддержания глобального состояния приложения
     */
    @Provides
    @Singleton
    fun providesApplication(): Application = application

    /**
     * Предоставление Context
     *
     * @return [Application] - базовый класс для поддержания глобального состояния приложения
     */
    @Provides
    @Singleton
    fun providesApplicationContext(): Context = application

    /**
     * Предоставление FusedLocationProviderClient - для взимодействия с fused провайдером местоположения.
     *
     * @return [FusedLocationProviderClient] - основная точка входа для
     * взаимодействия со fused провайдером местоположения.
     */
    @Provides
    @Singleton
    fun providesFusedLocationProviderClient(): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(application)
    }

    /**
     * Предоставление PendingIntent - намерения на вывод нужного экрана
     *
     * @param context [Context] - application контекст приложения
     *
     * @return [PendingIntent] - intent на вывод экрана приложения
     */
    @Provides
    fun provideActivityPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
        }

        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * Предоставление NotificationCompat.Builder для вывода уведомлений сервиса
     *
     * @param context [Context] - application контекст приложения
     * @param pendingIntent [PendingIntent] - описание намерения и целевого действия, которое нужно выполнить с ним
     *
     * @return [NotificationCompat.Builder] - построенное уведомление
     */
    @Provides
    fun providesBaseNotificationBuilder(context: Context, pendingIntent: PendingIntent): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_map)
            .setContentTitle(context.getString(R.string.running_app))
            .setContentText(context.getString(R.string.default_time))
            .setContentIntent(pendingIntent)
    }
}