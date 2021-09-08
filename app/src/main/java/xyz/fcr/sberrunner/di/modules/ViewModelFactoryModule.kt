package xyz.fcr.sberrunner.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import xyz.fcr.sberrunner.di.ViewModelKey
import xyz.fcr.sberrunner.presentation.viewmodels.ViewModelFactory
import xyz.fcr.sberrunner.presentation.viewmodels.firebase.LoginViewModel
import xyz.fcr.sberrunner.presentation.viewmodels.firebase.RegistrationViewModel
import xyz.fcr.sberrunner.presentation.viewmodels.main.*

/**
 * Модуль предоставления зависимостей во ViewModel
 */
@Module
abstract class ViewModelFactoryModule {

    /**
     * Предоставление интерфейса создания экземпляров ViewModel
     *
     * @param factory [ViewModelFactory] - фабрика ViewModel
     *
     * @return [ViewModelProvider.Factory] - реализация Factory интерфейса,
     * отвечающая за создание экземпляров ViewModels
     */
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    /**
     * Предоставление LoginViewModel
     *
     * @param viewModel [LoginViewModel] - представление данных для слоя View
     *
     * @return [ViewModel] - представление данных для слоя View
     */
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel

    /**
     * Предоставление RegistrationViewModel
     *
     * @param viewModel [RegistrationViewModel] - представление данных для слоя View
     *
     * @return [ViewModel] - представление данных для слоя View
     */
    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    internal abstract fun bindRegistrationViewModel(viewModel: RegistrationViewModel): ViewModel

    /**
     * Предоставление HomeViewModel
     *
     * @param viewModel [HomeViewModel] - представление данных для слоя View
     *
     * @return [ViewModel] - представление данных для слоя View
     */
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    /**
     * Предоставление DetailedRunViewModel
     *
     * @param viewModel [DetailedRunViewModel] - представление данных для слоя View
     *
     * @return [ViewModel] - представление данных для слоя View
     */
    @Binds
    @IntoMap
    @ViewModelKey(DetailedRunViewModel::class)
    internal abstract fun bindDetailedRunViewModel(viewModel: DetailedRunViewModel): ViewModel

    /**
     * Предоставление MapViewModel
     *
     * @param viewModel [MapViewModel] - представление данных для слоя View
     *
     * @return [ViewModel] - представление данных для слоя View
     */
    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    internal abstract fun bindMapViewModel(viewModel: MapViewModel): ViewModel

    /**
     * Предоставление RunViewModel
     *
     * @param viewModel [RunViewModel] - представление данных для слоя View
     *
     * @return [ViewModel] - представление данных для слоя View
     */
    @Binds
    @IntoMap
    @ViewModelKey(RunViewModel::class)
    internal abstract fun bindRunViewModel(viewModel: RunViewModel): ViewModel

    /**
     * Предоставление ProgressViewModel
     *
     * @param viewModel [ProgressViewModel] - представление данных для слоя View
     *
     * @return [ViewModel] - представление данных для слоя View
     */
    @Binds
    @IntoMap
    @ViewModelKey(ProgressViewModel::class)
    internal abstract fun bindProgressViewModel(viewModel: ProgressViewModel): ViewModel

    /**
     * Предоставление SharedSettingsViewModel
     *
     * @param viewModel [SharedSettingsViewModel] - представление данных для слоя View
     *
     * @return [ViewModel] - представление данных для слоя View
     */
    @Binds
    @IntoMap
    @ViewModelKey(SharedSettingsViewModel::class)
    internal abstract fun bindSharedSettingsViewModel(viewModel: SharedSettingsViewModel): ViewModel
}