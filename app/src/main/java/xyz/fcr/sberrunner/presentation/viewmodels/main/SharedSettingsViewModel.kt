package xyz.fcr.sberrunner.presentation.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import xyz.fcr.sberrunner.data.datastore.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.domain.interactor.db.IDatabaseInteractor
import xyz.fcr.sberrunner.domain.interactor.firebase.ICloudInteractor
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import xyz.fcr.sberrunner.utils.schedulers.ISchedulersProvider
import javax.inject.Inject

/**
 * ViewModel экрана "Настройки"
 *
 * @param firebaseInteractor [ICloudInteractor] - интерфейс взаимодействия с firebase
 * @param schedulersProvider [ISchedulersProvider] - провайдер объектов Scheduler
 * @param sharedPreferenceWrapper [ISharedPreferenceWrapper] - интерфейс упрощенного взаимодействия с SharedPreference
 */
class SharedSettingsViewModel @Inject constructor(
    private val firebaseInteractor: ICloudInteractor,
    private val databaseInteractor: IDatabaseInteractor,
    private val schedulersProvider: ISchedulersProvider,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _signOutLiveData = MutableLiveData<Boolean>()
    private val _errorLiveData = SingleLiveEvent<String>()

    private var compositeDisposable = CompositeDisposable()

    /**
     * Выход из аккаунта
     */
    fun signOut() {
        compositeDisposable.add(
            firebaseInteractor.signOut()
                .doOnSubscribe { _progressLiveData.postValue(true) }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({
                    clearRuns()
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
     * Удаление данных в БД
     */
    private fun clearRuns() {
        compositeDisposable.add(
            databaseInteractor.clearRuns()
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({
                    _signOutLiveData.postValue(true)
                    _progressLiveData.postValue(false)
                }, {
                    _errorLiveData.postValue(it.message)
                })
        )
    }

    /**
     * Обновление имени пользователя
     *
     * @param newName [String] - новое имя пользователя
     */
    fun updateName(newName: String) {
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

    /**
     * Обновление веса пользователя
     *
     * @param newWeight [String] - новый вес пользователя
     */
    fun updateWeight(newWeight: String) {
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
}