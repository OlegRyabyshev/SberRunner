package xyz.fcr.sberrunner.di

import android.content.Context
import dagger.Component
import xyz.fcr.sberrunner.di.modules.*
import xyz.fcr.sberrunner.presentation.view.fragments.main_fragments.*
import xyz.fcr.sberrunner.presentation.viewmodels.firebase_viewmodels.LoginViewModel
import xyz.fcr.sberrunner.presentation.viewmodels.firebase_viewmodels.RegistrationViewModel
import xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels.MapViewModel
import xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels.SharedSettingsViewModel
import javax.inject.Singleton

@Component(
    modules = [
        AppModule::class,
        StoreModule::class,
        RoomModule::class,
        FirebaseModule::class,
        ViewModelFactoryModule::class
    ]
)
@Singleton
interface AppComponent {
    fun context(): Context

    // Fragments
    fun inject(fragment: HomeFragment)
    fun inject(fragment: MapFragment)
    fun inject(fragment: RunFragment)
    fun inject(fragment: SettingsFragment)
    fun inject(fragment: YouFragment)

    // ViewModels
    fun inject(viewModel: LoginViewModel)
    fun inject(viewModel: RegistrationViewModel)
    fun inject(viewModel: SharedSettingsViewModel)
    fun inject(viewModel: MapViewModel)

}