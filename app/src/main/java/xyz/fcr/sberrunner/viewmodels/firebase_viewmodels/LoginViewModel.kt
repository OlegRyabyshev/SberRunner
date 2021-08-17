package xyz.fcr.sberrunner.viewmodels.firebase_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.repository.FirebaseRepository
import xyz.fcr.sberrunner.utils.Constants.VALID
import xyz.fcr.sberrunner.utils.SchedulersProvider

class LoginViewModel(
    private var firebaseRepo: FirebaseRepository,
    private var schedulersProvider: SchedulersProvider
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _loginLiveData = MutableLiveData<Boolean>()
    private val _resetLiveData = MutableLiveData<Boolean>()

    private val _errorEmail = MutableLiveData<String>()
    private val _errorPass = MutableLiveData<String>()
    private val _errorFirebase = MutableLiveData<String>()

    private var disReset: Disposable? = null
    private var disSignIn: Disposable? = null

    fun initResetEmail(email: String) {

        if (emailIsValid(email)) {
            disReset = Single.fromCallable { firebaseRepo.sendResetEmail(email.trim { it <= ' ' }) }
                .doOnSubscribe { _progressLiveData.postValue(true) }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe { task ->
                    task.addOnCompleteListener {
                        when {
                            it.isSuccessful -> _resetLiveData.postValue(true)
                            else -> _resetLiveData.postValue(false)
                        }

                        _progressLiveData.postValue(false)
                    }
                }
        }
    }

    fun initSignIn(email: String, pass: String) {
        if (emailIsValid(email) and passIsValid(pass)) {

            disSignIn = Single.fromCallable {
                firebaseRepo.login(
                    email.trim { it <= ' ' },
                    pass.trim { it <= ' ' },
                )
            }
                .doOnSubscribe { _progressLiveData.postValue(true) }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe { task ->
                    task.addOnCompleteListener {
                        when {
                            it.isSuccessful -> _loginLiveData.postValue(true)
                            else -> _loginLiveData.postValue(false)
                        }

                        _progressLiveData.postValue(false)
                    }
                }
        }
    }

    private fun emailIsValid(emailToCheck: String): Boolean {
        val email = emailToCheck.trim { it <= ' ' }

        return when {
            email.isBlank() -> {
                _errorEmail.postValue("Email can not be empty")
                false
            }
            !(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) -> {
                _errorEmail.postValue("Wrong email format")
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
                _errorPass.postValue("Password can not be empty")
                false
            }
            pass.length < 6 -> {
                _errorPass.postValue("Password should be at least 6 charters")
                false
            }
            else -> {
                _errorPass.postValue(VALID)
                true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        disReset?.dispose()
        disReset = null

        disSignIn?.dispose()
        disSignIn = null
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