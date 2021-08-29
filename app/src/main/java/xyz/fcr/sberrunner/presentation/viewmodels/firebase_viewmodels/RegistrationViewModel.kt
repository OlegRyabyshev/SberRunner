package xyz.fcr.sberrunner.presentation.viewmodels.firebase_viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.repository.FirebaseRepository
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import xyz.fcr.sberrunner.utils.Constants.NAME_KEY
import xyz.fcr.sberrunner.utils.Constants.VALID
import xyz.fcr.sberrunner.utils.Constants.WEIGHT_KEY
import xyz.fcr.sberrunner.utils.SchedulersProviderInterface
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepository,
    private val schedulersProvider: SchedulersProviderInterface,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _successLiveData = MutableLiveData<String>()
    private val _errorFirebase = SingleLiveEvent<String>()

    private val _errorName = SingleLiveEvent<String>()
    private val _errorEmail = SingleLiveEvent<String>()
    private val _errorPass = SingleLiveEvent<String>()
    private val _errorWeight = SingleLiveEvent<String>()


    private var disposable: Disposable? = null

    fun initRegistration(name: String, email: String, pass: String, weight: String) {
        if (nameIsValid(name) and emailIsValid(email) and passIsValid(pass) and weightIsValid(weight)) {

            disposable = Single.fromCallable {
                firebaseRepo.registration(
                    name.trim { it <= ' ' },
                    email.trim { it <= ' ' },
                    pass.trim { it <= ' ' },
                    weight
                )
            }
                .doOnSubscribe { _progressLiveData.postValue(true) }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe { task ->
                    task.addOnCompleteListener {
                        when {
                            it.isSuccessful -> {
                                _successLiveData.postValue(VALID)
                                saveToSharedPrefs(name, weight)
                            }
                            else -> {
                                _successLiveData.postValue(it.exception?.message.toString())
                            }
                        }

                        _progressLiveData.postValue(false)
                    }
                }
        }
    }

    private fun saveToSharedPrefs(name: String, weight: String) {
        sharedPreferences.edit().apply{
            putString(NAME_KEY, name)
            putString(WEIGHT_KEY, weight)
            apply()
        }
    }

    private fun nameIsValid(nameToCheck: String): Boolean {
        val name = nameToCheck.trim { it <= ' ' }

        return when {
            name.isBlank() -> {
                _errorName.postValue("Name can not be empty")
                false
            }
            else -> {
                _errorName.postValue(VALID)
                true
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

    private fun weightIsValid(weightToCheck: String): Boolean {

        val weight = weightToCheck.toIntOrNull()

        return when {
            weight == null || weight > 350 || weight <= 0 || weightToCheck.startsWith("0") -> {
                _errorWeight.postValue("Weight is not valid")
                false
            }
            else -> {
                _errorWeight.postValue(VALID)
                true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        disposable?.dispose()
        disposable = null
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