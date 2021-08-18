package xyz.fcr.sberrunner.di

import android.content.Context
import dagger.Component
import xyz.fcr.sberrunner.di.modules.AppModule
import xyz.fcr.sberrunner.di.modules.StoreModule
import xyz.fcr.sberrunner.viewmodels.main_viewmodels.MapViewModel
import javax.inject.Singleton

@Component(modules = [AppModule::class, StoreModule::class])
@Singleton
interface AppComponent {
    fun context(): Context
    fun inject(mapViewModel: MapViewModel)
}