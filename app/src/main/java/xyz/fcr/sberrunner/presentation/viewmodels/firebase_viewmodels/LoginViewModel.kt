package xyz.fcr.sberrunner.presentation.viewmodels.firebase_viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.DocumentSnapshot
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.repository.FirebaseRepository
import xyz.fcr.sberrunner.utils.Constants.VALID
import xyz.fcr.sberrunner.utils.SchedulersProviderInterface
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepository,
    private val schedulersProvider: SchedulersProviderInterface,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _loginLiveData = MutableLiveData<Boolean>()
    private val _resetLiveData = SingleLiveEvent<Boolean>()

    private val _errorEmail = SingleLiveEvent<String>()
    private val _errorPass = SingleLiveEvent<String>()
    private val _errorFirebase = SingleLiveEvent<String>()

    private var disReset: Disposable? = null
    private var disSignIn: Disposable? = null
    private var disNameAndWeightLoader: Disposable? = null

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
                            it.isSuccessful -> loadNameAndWeightFromFireStore()
                            else -> {
                                _loginLiveData.postValue(false)
                                _progressLiveData.postValue(false)
                            }
                        }
                    }
                }
        }
    }

    private fun loadNameAndWeightFromFireStore() {
        disSignIn = Single.fromCallable { firebaseRepo.getDocumentFirestore() }
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe { task ->
                task?.addOnCompleteListener {
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

                if (task == null) {
                    _progressLiveData.postValue(false)
                }
            }
    }

    private fun saveToSharedPrefs(result: DocumentSnapshot) {
        sharedPreferences.edit().apply {
            putString("name_key", result.getString("name"))
            putString("weight_key", result.getString("weight"))
            apply()
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

        disNameAndWeightLoader?.dispose()
        disNameAndWeightLoader = null
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