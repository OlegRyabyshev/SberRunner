package xyz.fcr.sberrunner.presentation.viewmodels.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.domain.firebase.IFirebaseInteractor
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import xyz.fcr.sberrunner.utils.Constants.NON_VALID
import xyz.fcr.sberrunner.utils.Constants.VALID
import xyz.fcr.sberrunner.utils.schedulers.ISchedulersProvider
import javax.inject.Inject

/**
 * ViewModel экрана регистрации.
 *
 * @param firebaseInteractor [IFirebaseInteractor] - интерфейс взаимодействия с firebase
 * @param schedulersProvider [ISchedulersProvider] - провайдер объектов Scheduler
 * @param sharedPreferenceWrapper [ISharedPreferenceWrapper] - интерфейс упрощенного взаимодействия с SharedPreference
 */
class RegistrationViewModel @Inject constructor(
    private val firebaseInteractor: IFirebaseInteractor,
    private val schedulersProvider: ISchedulersProvider,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _successLiveData = MutableLiveData<String>()
    private val _errorFirebase = SingleLiveEvent<String>()

    private val _errorName = SingleLiveEvent<String>()
    private val _errorEmail = SingleLiveEvent<String>()
    private val _errorPass = SingleLiveEvent<String>()
    private val _errorWeight = SingleLiveEvent<String>()

    private var compositeDisposable = CompositeDisposable()

    /**
     * Процесс регистрации пользователя
     *
     * @param name [String] - имя пользователя
     * @param email [String] - почта пользователя
     * @param pass [String] - пароль пользователя
     * @param weight [String] - вес пользователя
     */
    fun initRegistration(name: String, email: String, pass: String, weight: String) {

        if (nameIsValid(name) and emailIsValid(email) and passIsValid(pass) and weightIsValid(weight)) {
            compositeDisposable.add(
                firebaseInteractor.registration(
                    name.trim { it <= ' ' },
                    email.trim { it <= ' ' },
                    pass.trim { it <= ' ' },
                    weight
                )
                    .doOnSubscribe { _progressLiveData.postValue(true) }
                    .subscribeOn(schedulersProvider.io())
                    .observeOn(schedulersProvider.ui())
                    .subscribe { task ->
                        task.addOnCompleteListener {
                            when {
                                it.isSuccessful -> {
                                    initSaveFieldsToCloud(name, weight)
                                }
                                else -> {
                                    _successLiveData.postValue(it.exception?.message.toString())
                                }
                            }

                            _progressLiveData.postValue(false)
                        }
                    }
            )
        }
    }

    /**
     * Сохранение имени и веса пользователя в документ Firestore
     *
     * @param name [String] - имя пользователя
     * @param weight [String] - вес пользователя
     */
    private fun initSaveFieldsToCloud(name: String, weight: String) {
        compositeDisposable.add(
            firebaseInteractor.fillUserDataInFirestore(name, weight)
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({ task ->
                    task.addOnCompleteListener {
                        when {
                            task.isSuccessful -> {
                                _successLiveData.postValue(VALID)
                                saveToSharedPrefs(name, weight)
                            }
                            else -> {
                                _successLiveData.postValue(NON_VALID)
                                saveToSharedPrefs(name, weight)
                            }
                        }
                    }
                }, {
                    _successLiveData.postValue(it.message)
                })
        )
    }

    /**
     * Сохранение имени и веса пользователя в SharedPreference
     *
     * @param name [String] - имя пользователя
     * @param weight [String] - вес пользователя
     */
    private fun saveToSharedPrefs(name: String, weight: String) {
        sharedPreferenceWrapper.saveName(name)
        sharedPreferenceWrapper.saveWeight(weight)
    }

    /**
     * Проверка корректности введенного имени
     *
     * @param nameToCheck [String] - имя пользователя
     * @return [Boolean] - корректеный ввод (true) / некорректный ввод (false)
     */
    private fun nameIsValid(nameToCheck: String): Boolean {
        val name = nameToCheck.trim { it <= ' ' }

        return when {
            name.isBlank() -> {
                _errorName.postValue(App.appComponent.context().getString(R.string.name_cant_be_empty))
                false
            }
            else -> {
                _errorName.postValue(VALID)
                true
            }
        }
    }

    /**
     * Проверка корректности введенной почты
     *
     * @param emailToCheck [String] - почта пользователя
     * @return [Boolean] - корректеный ввод (true) / некорректный ввод (false)
     */
    private fun emailIsValid(emailToCheck: String): Boolean {
        val email = emailToCheck.trim { it <= ' ' }

        return when {
            email.isBlank() -> {
                _errorEmail.postValue(App.appComponent.context().getString(R.string.email_cant_be_empty))
                false
            }
            !(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) -> {
                _errorEmail.postValue(App.appComponent.context().getString(R.string.wrong_email_format))
                false
            }
            else -> {
                _errorEmail.postValue(VALID)
                true
            }
        }
    }

    /**
     * Проверка корректности введенного пароля
     *
     * @param passToCheck [String] - пароль пользователя
     * @return [Boolean] - корректеный ввод (true) / некорректный ввод (false)
     */
    private fun passIsValid(passToCheck: String): Boolean {
        val pass = passToCheck.trim { it <= ' ' }

        return when {
            pass.isBlank() -> {
                _errorPass.postValue(App.appComponent.context().getString(R.string.password_cant_be_empty))
                false
            }
            pass.length < 6 -> {
                _errorPass.postValue(App.appComponent.context().getString(R.string.password_six_characters))
                false
            }
            else -> {
                _errorPass.postValue(VALID)
                true
            }
        }
    }

    /**
     * Проверка корректности введенного веса
     *
     * @param weightToCheck [String] - вес пользователя
     * @return [Boolean] - корректеный ввод (true) / некорректный ввод (false)
     */
    private fun weightIsValid(weightToCheck: String): Boolean {

        val weight = weightToCheck.toIntOrNull()

        return when {
            weight == null || weight > 350 || weight <= 0 || weightToCheck.startsWith("0") -> {
                _errorWeight.postValue(App.appComponent.context().getString(R.string.weight_not_valid))
                false
            }
            else -> {
                _errorWeight.postValue(VALID)
                true
            }
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
    val successLiveData: LiveData<String>
        get() = _successLiveData
    val errorName: LiveData<String>
        get() = _errorName
    val errorEmail: LiveData<String>
        get() = _errorEmail
    val errorPass: LiveData<String>
        get() = _errorPass
    val errorWeight: LiveData<String>
        get() = _errorWeight
    val errorFirebase: LiveData<String>
        get() = _errorFirebase
}