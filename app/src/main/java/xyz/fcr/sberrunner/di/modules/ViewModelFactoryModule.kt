package xyz.fcr.sberrunner.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import xyz.fcr.sberrunner.di.ViewModelKey
import xyz.fcr.sberrunner.presentation.viewmodels.ViewModelFactory
import xyz.fcr.sberrunner.presentation.viewmodels.firebase_viewmodels.LoginViewModel
import xyz.fcr.sberrunner.presentation.viewmodels.firebase_viewmodels.RegistrationViewModel
import xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels.*

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    internal abstract fun bindRegistrationViewModel(viewModel: RegistrationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailedRunViewModel::class)
    internal abstract fun bindDetailedRunViewModel(viewModel: DetailedRunViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    internal abstract fun bindMapViewModel(viewModel: MapViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RunViewModel::class)
    internal abstract fun bindRunViewModel(viewModel: RunViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProgressViewModel::class)
    internal abstract fun bindProgressViewModel(viewModel: ProgressViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SharedSettingsViewModel::class)
    internal abstract fun bindSharedSettingsViewModel(viewModel: SharedSettingsViewModel): ViewModel
}