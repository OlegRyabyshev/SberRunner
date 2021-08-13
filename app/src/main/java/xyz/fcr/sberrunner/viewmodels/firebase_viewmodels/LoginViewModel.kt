package xyz.fcr.sberrunner.viewmodels.firebase_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.repository.FirebaseRepository
import xyz.fcr.sberrunner.utils.Constants.VALID
import xyz.fcr.sberrunner.utils.SchedulersProvider
import java.util.concurrent.TimeUnit

class LoginViewModel(
    private var firebaseRepo: FirebaseRepository,
    private var schedulersProvider: SchedulersProvider
) : ViewModel() {

    private val mProgressLiveData = MutableLiveData<Boolean>()
    private val mErrorLiveData = MutableLiveData<Throwable>()
    private val mLoginLiveData = MutableLiveData<Boolean>()
    private val mResetLiveData = MutableLiveData<Boolean>()

    private val mErrorEmail = MutableLiveData<String>()
    private val mErrorPass = MutableLiveData<String>()

    private val mErrorFirebase = MutableLiveData<Throwable>()

    private var disReset: Disposable? = null
    private var disSignIn: Disposable? = null

    fun initResetEmail(email: String) {

        if (emailIsValid(email)) {
            firebaseRepo.sendResetEmail(email)

            disReset = Single.fromCallable { firebaseRepo.sendResetEmail(email.trim { it <= ' ' }) }
                .doOnSubscribe { mProgressLiveData.postValue(true) }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe { task ->
                    task.addOnCompleteListener {
                        when {
                            it.isSuccessful -> mResetLiveData.postValue(true)
                            else -> mResetLiveData.postValue(false)
                        }

                        mProgressLiveData.postValue(false)
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
                .doOnSubscribe { mProgressLiveData.postValue(true) }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe { task ->
                    task.addOnCompleteListener {
                        when {
                            it.isSuccessful -> mLoginLiveData.postValue(true)
                            else -> mLoginLiveData.postValue(false)
                        }

                        mProgressLiveData.postValue(false)
                    }
                }
        }
    }

    private fun emailIsValid(emailToCheck: String): Boolean {
        val email = emailToCheck.trim { it <= ' ' }

        return when {
            email.isBlank() -> {
                mErrorEmail.postValue("Email can not be empty")
                false
            }
            !(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) -> {
                mErrorEmail.postValue("Wrong email format")
                false
            }
            else -> {
                mErrorEmail.postValue(VALID)
                true
            }
        }
    }

    private fun passIsValid(passToCheck: String): Boolean {
        val pass = passToCheck.trim { it <= ' ' }

        return when {
            pass.isBlank() -> {
                mErrorPass.postValue("Password can not be empty")
                false
            }
            pass.length < 6 -> {
                mErrorPass.postValue("Password should be at least 6 charters")
                false
            }
            else -> {
                mErrorPass.postValue(VALID)
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
        get() = mProgressLiveData
    val errorLiveData: LiveData<Throwable>
        get() = mErrorLiveData
    val loginLiveData: LiveData<Boolean>
        get() = mLoginLiveData
    val resetLiveData: LiveData<Boolean>
        get() = mResetLiveData
    val errorEmail: LiveData<String>
        get() = mErrorEmail
    val errorPass: LiveData<String>
        get() = mErrorPass
    val errorFirebase: LiveData<Throwable>
        get() = mErrorFirebase
}