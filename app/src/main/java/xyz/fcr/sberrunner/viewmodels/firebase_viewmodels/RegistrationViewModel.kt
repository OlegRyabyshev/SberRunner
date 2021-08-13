package xyz.fcr.sberrunner.viewmodels.firebase_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.repository.FirebaseRepository
import xyz.fcr.sberrunner.utils.Constants.VALID
import xyz.fcr.sberrunner.utils.SchedulersProvider

class RegistrationViewModel(
    private var firebaseRepo: FirebaseRepository,
    private var schedulersProvider: SchedulersProvider
) : ViewModel() {

    private val mProgressLiveData = MutableLiveData<Boolean>()
    private val mErrorLiveData = MutableLiveData<Throwable>()
    private val mSuccessLiveData = MutableLiveData<Boolean>()

    private val mErrorName = MutableLiveData<String>()
    private val mErrorEmail = MutableLiveData<String>()
    private val mErrorPass = MutableLiveData<String>()
    private val mErrorWeight = MutableLiveData<String>()

    private val mErrorFirebase = MutableLiveData<Throwable>()

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
                .doOnSubscribe { mProgressLiveData.postValue(true) }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe { task ->
                    task.addOnCompleteListener {
                        when {
                            it.isSuccessful -> mSuccessLiveData.postValue(true)
                            else -> mSuccessLiveData.postValue(false)
                        }

                        mProgressLiveData.postValue(false)
                    }
                }
        }
    }

    private fun nameIsValid(nameToCheck: String): Boolean {
        val name = nameToCheck.trim { it <= ' ' }

        return when {
            name.isBlank() -> {
                mErrorName.postValue("Name can not be empty")
                false
            }
            else -> {
                mErrorName.postValue(VALID)
                true
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

    private fun weightIsValid(weightToCheck: String): Boolean {
        val weight = weightToCheck.toIntOrNull()

        return when {
            weight == null || weight > 350 || weight <= 0 -> {
                mErrorWeight.postValue("Weight is not valid")
                false
            }
            else -> {
                mErrorWeight.postValue(VALID)
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
        get() = mProgressLiveData
    val errorLiveData: LiveData<Throwable>
        get() = mErrorLiveData
    val successLiveData: LiveData<Boolean>
        get() = mSuccessLiveData
    val errorName: LiveData<String>
        get() = mErrorName
    val errorEmail: LiveData<String>
        get() = mErrorEmail
    val errorPass: LiveData<String>
        get() = mErrorPass
    val errorWeight: LiveData<String>
        get() = mErrorWeight
    val errorFirebase: LiveData<Throwable>
        get() = mErrorFirebase
}