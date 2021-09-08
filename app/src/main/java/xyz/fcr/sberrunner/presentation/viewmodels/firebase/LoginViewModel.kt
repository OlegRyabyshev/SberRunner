package xyz.fcr.sberrunner.presentation.viewmodels.firebase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import io.reactivex.rxjava3.disposables.CompositeDisposable
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.domain.firebase.IFirebaseInteractor
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import xyz.fcr.sberrunner.utils.Constants.VALID
import xyz.fcr.sberrunner.utils.ISchedulersProvider
import javax.inject.Inject

/**
 * ViewModel экрана аутентификации.
 *
 * @param firebaseInteractor [IFirebaseInteractor] - интерфейс взаимодействия с firebase
 * @param schedulersProvider [ISchedulersProvider] - провайдер объектов Scheduler
 * @param sharedPreferenceWrapper [ISharedPreferenceWrapper] - интерфейс упрощенного взаимодействия с SharedPreference
 */
class LoginViewModel @Inject constructor(
    private val firebaseInteractor: IFirebaseInteractor,
    private val schedulersProvider: ISchedulersProvider,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _loginLiveData = MutableLiveData<Boolean>()
    private val _resetLiveData = SingleLiveEvent<Boolean>()

    private val _errorEmail = SingleLiveEvent<String>()
    private val _errorPass = SingleLiveEvent<String>()
    private val _errorFirebase = SingleLiveEvent<String>()

    private var compositeDisposable = CompositeDisposable()

    fun initResetEmail(email: String) {
        if (emailIsValid(email)) {
            compositeDisposable.add(
                firebaseInteractor.sendResetEmail(email.trim { it <= ' ' })
                    .doOnSubscribe { _progressLiveData.postValue(true) }
                    .subscribeOn(schedulersProvider.io())
                    .observeOn(schedulersProvider.ui())
                    .subscribe({
                        _resetLiveData.postValue(true)
                    }, {
                        _resetLiveData.postValue(false)
                    })
            )
        }
    }

    fun initSignIn(email: String, pass: String) {
        if (emailIsValid(email) and passIsValid(pass)) {
            compositeDisposable.add(
                firebaseInteractor.login(email.trim { it <= ' ' }, pass.trim { it <= ' ' })
                    .doOnSubscribe { _progressLiveData.postValue(true) }
                    .subscribeOn(schedulersProvider.io())
                    .observeOn(schedulersProvider.ui())
                    .subscribe { task ->
                        task.addOnCompleteListener {
                            when {
                                it.isSuccessful -> loadNameAndWeightFromFireStore()
                                else -> {
                                    _loginLiveData.postValue(false)
                                    _progressLiveData.postValue(false)
                                }
                            }
                        }
                    }
            )
        }
    }

    private fun loadNameAndWeightFromFireStore() {
        compositeDisposable.add(
            firebaseInteractor.getDocumentFirestore()
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe { task ->
                    task.addOnCompleteListener {
                        when {
                            it.isSuccessful -> {
                                saveToSharedPrefs(it.result)
                                _loginLiveData.postValue(true)
                                _progressLiveData.postValue(false)
                            }
                            else -> {
                                _loginLiveData.postValue(false)
                                _progressLiveData.postValue(false)
                            }
                        }
                    }
                }
        )
    }

    private fun saveToSharedPrefs(result: DocumentSnapshot) {
        val name = result.getString("name")
        val weight = result.getString("weight")

        if (name != null && weight != null) {
            sharedPreferenceWrapper.saveName(name)
            sharedPreferenceWrapper.saveWeight(weight)
        }
    }

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
     * Обнуление disposable
     */
    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    val progressLiveData: LiveData<Boolean>
        get() = _progressLiveData
    val loginLiveData: LiveData<Boolean>
        get() = _loginLiveData
    val resetLiveData: LiveData<Boolean>
        get() = _resetLiveData
    val errorEmail: LiveData<String>
        get() = _errorEmail
    val errorPass: LiveData<String>
        get() = _errorPass
    val errorFirebase: LiveData<String>
        get() = _errorFirebase
}