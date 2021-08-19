package xyz.fcr.sberrunner.di

import android.content.Context
import dagger.Component
import xyz.fcr.sberrunner.di.modules.AppModule
import xyz.fcr.sberrunner.di.modules.RoomModule
import xyz.fcr.sberrunner.di.modules.StoreModule
import xyz.fcr.sberrunner.viewmodels.firebase_viewmodels.LoginViewModel
import xyz.fcr.sberrunner.viewmodels.firebase_viewmodels.RegistrationViewModel
import xyz.fcr.sberrunner.viewmodels.main_viewmodels.MapViewModel
import xyz.fcr.sberrunner.viewmodels.main_viewmodels.SharedSettingsViewModel
import javax.inject.Singleton

@Component(modules = [AppModule::class, StoreModule::class, RoomModule::class])
@Singleton
interface AppComponent {
    fun context(): Context
    fun inject(mapViewModel: MapViewModel)

    // ViewModels
    fun inject(registrationViewModel: RegistrationViewModel)
    fun inject(sharedSettingsViewModel: SharedSettingsViewModel)
    fun inject(loginViewModel: LoginViewModel)
}