package xyz.fcr.sberrunner.presentation

import android.app.Application
import xyz.fcr.sberrunner.di.AppComponent
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.di.DaggerAppComponent
import xyz.fcr.sberrunner.di.modules.AppModule
import xyz.fcr.sberrunner.utils.Constants.CHANNEL_ID

/**
 * Application-класс приложения, используется для инициализации Dagger и создания уведомления сервиса.
 */
class App : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeDagger()
    }

    /**
     * Создание канала уведомлений сервиса
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.service_channel),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    /**
     * Инициализация Dagger для внедрения зависимостей в приложение
     */
    private fun initializeDagger() {
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }
}