package xyz.fcr.sberrunner.presentation.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.domain.db.IDatabaseInteractor
import xyz.fcr.sberrunner.domain.firebase.IFirebaseInteractor
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import xyz.fcr.sberrunner.utils.schedulers.ISchedulersProvider
import javax.inject.Inject

/**
 * ViewModel экрана "Настройки"
 *
 * @param firebaseInteractor [IFirebaseInteractor] - интерфейс взаимодействия с firebase
 * @param schedulersProvider [ISchedulersProvider] - провайдер объектов Scheduler
 * @param sharedPreferenceWrapper [ISharedPreferenceWrapper] - интерфейс упрощенного взаимодействия с SharedPreference
 */
class SharedSettingsViewModel @Inject constructor(
    private val firebaseInteractor: IFirebaseInteractor,
    private val databaseInteractor: IDatabaseInteractor,
    private val schedulersProvider: ISchedulersProvider,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _signOutLiveData = MutableLiveData<Boolean>()
    private val _errorLiveData = SingleLiveEvent<String>()

    private val _nameSummaryLiveData = MutableLiveData<String>()
    private val _weightSummaryLiveData = MutableLiveData<String>()

    private var compositeDisposable = CompositeDisposable()

    /**
     * Выставляет имя и вес пользователя в summary настроек
     */
    fun displayNameAndWeightInSummary() {
        _nameSummaryLiveData.postValue(sharedPreferenceWrapper.getName())
        _weightSummaryLiveData.postValue(sharedPreferenceWrapper.getWeight())
    }

    /**
     * Выход из аккаунта
     */
    fun exitAccount() {
        compositeDisposable.add(
            databaseInteractor.clearRuns()
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({
                    signOut()
                }, {
                    _errorLiveData.postValue(it.message)
                })
        )
    }

    private fun signOut() {
        compositeDisposable.add(
            firebaseInteractor.signOut()
                .doOnSubscribe { _progressLiveData.postValue(true) }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({
                    _signOutLiveData.postValue(true)
                }, {
                    _errorLiveData.postValue(it.message)
                })
        )
    }

    /**
     * Удаление аккаунта пользователя
     */
    fun deleteAccount() {
        compositeDisposable.add(
            firebaseInteractor.deleteAccount()
                .doOnSubscribe { _progressLiveData.postValue(true) }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({ task ->
                    task.addOnCompleteListener {
                        when {
                            task.isSuccessful -> signOut()
                            else -> _progressLiveData.postValue(false)
                        }
                    }
                }, {
                    _progressLiveData.postValue(false)
                })
        )
    }

    /**
     * Обновление имени пользователя
     *
     * @param newName [String] - новое имя пользователя
     */
    fun updateName(newName: String) {
        if (nameIsValid(newName)) {
            compositeDisposable.add(
                firebaseInteractor.updateName(newName)
                    .doOnSubscribe { _progressLiveData.postValue(true) }
                    .subscribeOn(schedulersProvider.io())
                    .observeOn(schedulersProvider.ui())
                    .subscribe({ task ->
                        task.addOnCompleteListener {
                            when {
                                task.isSuccessful -> {
                                    sharedPreferenceWrapper.saveName(newName)
                                    _nameSummaryLiveData.postValue(newName)
                                    _progressLiveData.postValue(false)
                                }
                                else -> {
                                    _progressLiveData.postValue(false)
                                }
                            }
                        }
                    }, {
                        _progressLiveData.postValue(false)
                    })
            )
        }
    }

    /**
     * Обновление веса пользователя
     *
     * @param newWeight [String] - новый вес пользователя
     */
    fun updateWeight(newWeight: String) {
        if (weightIsValid(newWeight)) {
            compositeDisposable.add(
                firebaseInteractor.updateWeight(newWeight)
                    .doOnSubscribe { _progressLiveData.postValue(true) }
                    .subscribeOn(schedulersProvider.io())
                    .observeOn(schedulersProvider.ui())
                    .subscribe({ task ->
                        task.addOnCompleteListener {
                            when {
                                it.isSuccessful -> {
                                    sharedPreferenceWrapper.saveWeight(newWeight)
                                    _weightSummaryLiveData.postValue(newWeight)
                                    _progressLiveData.postValue(false)
                                }

                                else -> _progressLiveData.postValue(false)
                            }
                        }
                    }, {
                        _progressLiveData.postValue(false)
                    })
            )
        }
    }

    /**
     * Проверка корректности нового имени пользователя
     *
     * @param nameToCheck [String] - имя пользователя
     * @return [Boolean] - корректеный ввод (true) / некорректный ввод (false)
     */
    private fun nameIsValid(nameToCheck: String): Boolean {
        val name = nameToCheck.trim { it <= ' ' }

        return when {
            name.isBlank() -> {
                _errorLiveData.postValue(App.appComponent.context().getString(R.string.name_cant_be_empty))
                false
            }
            else -> true
        }
    }

    /**
     * Проверка корректности нового веса пользователя
     *
     * @param weightToCheck [String] - вес пользователя
     * @return [Boolean] - корректеный ввод (true) / некорректный ввод (false)
     */
    private fun weightIsValid(weightToCheck: String): Boolean {
        val weight = weightToCheck.toIntOrNull()

        return when {
            weight == null || weight > 350 || weight <= 0 || weightToCheck.startsWith("0") -> {
                _errorLiveData.postValue(App.appComponent.context().getString(R.string.weight_not_valid))
                false
            }
            else -> true
        }
    }

    /**
     * Обнуление disposable
     */
    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    val progressLiveData: LiveData<Boolean>
        get() = _progressLiveData
    val signOutLiveData: LiveData<Boolean>
        get() = _signOutLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
    val nameSummaryLiveData: LiveData<String>
        get() = _nameSummaryLiveData
    val weightSummaryLiveData: LiveData<String>
        get() = _weightSummaryLiveData
}