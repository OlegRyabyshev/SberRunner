package xyz.fcr.sberrunner.di

import android.content.Context
import dagger.Component
import xyz.fcr.sberrunner.data.service.RunningService
import xyz.fcr.sberrunner.data.service.notification.AudioNotificator
import xyz.fcr.sberrunner.di.modules.*
import xyz.fcr.sberrunner.presentation.view.activities.MainActivity
import xyz.fcr.sberrunner.presentation.view.activities.SplashScreenActivity
import xyz.fcr.sberrunner.presentation.view.fragments.firebase.LoginFragment
import xyz.fcr.sberrunner.presentation.view.fragments.firebase.RegistrationFragment
import xyz.fcr.sberrunner.presentation.view.fragments.main.*
import xyz.fcr.sberrunner.presentation.viewmodels.firebase.LoginViewModel
import xyz.fcr.sberrunner.presentation.viewmodels.firebase.RegistrationViewModel
import xyz.fcr.sberrunner.presentation.viewmodels.main.MapViewModel
import xyz.fcr.sberrunner.presentation.viewmodels.main.SharedSettingsViewModel
import javax.inject.Singleton

/**
 * Главный интерфейс компонента приложения
 */
@Component(
    modules = [
        AppModule::class,
        SharedPreferenceModule::class,
        DatabaseModule::class,
        FirebaseModule::class,
        ViewModelFactoryModule::class,
        UtilModule::class
    ]
)
@Singleton
interface AppComponent {
    fun context(): Context

    /**
     * Внедрение зависимостей в Activity
     */
    fun inject(activity: MainActivity)
    fun inject(activity: SplashScreenActivity)

    /**
     * Внедрение зависимостей в Fragment
     */
    fun inject(fragment: LoginFragment)
    fun inject(fragment: RegistrationFragment)

    fun inject(fragment: DetailedRunFragment)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: MapFragment)
    fun inject(fragment: RunFragment)
    fun inject(fragment: SettingsFragment)
    fun inject(fragment: SettingsPreferenceFragment)
    fun inject(fragment: ProgressFragment)

    /**
     * Внедрение зависимостей во ViewModel
     */
    fun inject(viewModel: LoginViewModel)
    fun inject(viewModel: RegistrationViewModel)
    fun inject(viewModel: SharedSettingsViewModel)
    fun inject(viewModel: MapViewModel)

    /**
     * Внедрение зависимостей для сервиса
     */
    fun inject(runningService: RunningService)
    fun inject(audioNotificator: AudioNotificator)
}