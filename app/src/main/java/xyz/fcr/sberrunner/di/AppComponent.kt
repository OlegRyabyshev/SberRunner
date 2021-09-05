package xyz.fcr.sberrunner.di

import android.content.Context
import dagger.Component
import xyz.fcr.sberrunner.data.service.notification.AudioNotificator
import xyz.fcr.sberrunner.data.service.RunningService
import xyz.fcr.sberrunner.di.modules.*
import xyz.fcr.sberrunner.presentation.view.activities.MainActivity
import xyz.fcr.sberrunner.presentation.view.activities.SplashScreenActivity
import xyz.fcr.sberrunner.presentation.view.fragments.firebase.LoginFragment
import xyz.fcr.sberrunner.presentation.view.fragments.firebase.RegistrationFragment
import xyz.fcr.sberrunner.presentation.view.fragments.main.*
import xyz.fcr.sberrunner.presentation.view.fragments.main.adapters.RunRecyclerAdapter
import xyz.fcr.sberrunner.presentation.viewmodels.firebase.LoginViewModel
import xyz.fcr.sberrunner.presentation.viewmodels.firebase.RegistrationViewModel
import xyz.fcr.sberrunner.presentation.viewmodels.main.MapViewModel
import xyz.fcr.sberrunner.presentation.viewmodels.main.SharedSettingsViewModel
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        StoreModule::class,
        DatabaseModule::class,
        FirebaseModule::class,
        ViewModelFactoryModule::class
    ]
)
@Singleton
interface AppComponent {
    fun context(): Context

    // Activity
    fun inject(activity: MainActivity)
    fun inject(activity: SplashScreenActivity)

    // Adapter
    fun inject(adapter: RunRecyclerAdapter)

    // Fragments
    fun inject(fragment: LoginFragment)
    fun inject(fragment: RegistrationFragment)

    fun inject(fragment: DetailedRunFragment)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: MapFragment)
    fun inject(fragment: RunFragment)
    fun inject(fragment: SettingsFragment)
    fun inject(fragment: SettingsPreferenceFragment)
    fun inject(fragment: ProgressFragment)

    // ViewModels
    fun inject(viewModel: LoginViewModel)
    fun inject(viewModel: RegistrationViewModel)
    fun inject(viewModel: SharedSettingsViewModel)
    fun inject(viewModel: MapViewModel)

    // Services
    fun inject(runningService: RunningService)
    fun inject(audioNotificator: AudioNotificator)
}