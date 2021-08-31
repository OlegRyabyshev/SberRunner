package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.repository.firebase.IFirebaseRepository
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import xyz.fcr.sberrunner.utils.ISchedulersProvider
import javax.inject.Inject

class SharedSettingsViewModel @Inject constructor(
    private val firebaseRepo: IFirebaseRepository,
    private val schedulersProvider: ISchedulersProvider,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _signOutLiveData = MutableLiveData<Boolean>()
    private val _deleteLiveData = MutableLiveData<Boolean>()
    private val _errorLiveData = SingleLiveEvent<String>()

    private val _nameSummaryLiveData = MutableLiveData<String>()
    private val _weightSummaryLiveData = MutableLiveData<String>()

    private var disDeleteAccount: Disposable? = null
    private var disUpdWeight: Disposable? = null
    private var disUpdName: Disposable? = null

    fun displayNameAndWeightInSummary() {
        _nameSummaryLiveData.postValue(sharedPreferenceWrapper.getName())
        _weightSummaryLiveData.postValue(sharedPreferenceWrapper.getWeight())
    }

    fun exitAccount() {
        firebaseRepo.signOut()
        _signOutLiveData.postValue(true)
    }

    fun deleteAccount() {
        disDeleteAccount = Single.fromCallable { firebaseRepo.deleteAccount() }
            .doOnSubscribe { _progressLiveData.postValue(true) }
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe { task ->
                task.addOnCompleteListener {
                    when {
                        it.isSuccessful -> _deleteLiveData.postValue(true)
                        else -> _deleteLiveData.postValue(false)
                    }
                }
            }
    }

    fun updateWeight(newWeight: String) {
        if (weightIsValid(newWeight)) {
            disUpdName = Single.fromCallable { firebaseRepo.updateWeight(newWeight) }
                .doOnSubscribe { _progressLiveData.postValue(true) }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe { task ->
                    task.addOnCompleteListener {
                        when {
                            it.isSuccessful -> {
                                sharedPreferenceWrapper.saveWeight(newWeight)
                                _weightSummaryLiveData.postValue(newWeight)
                                _progressLiveData.postValue(false)
                            }
                            else -> {
                                _progressLiveData.postValue(false)
                            }
                        }
                    }
                }
        }
    }

    fun updateName(newName: String) {
        if (nameIsValid(newName)) {
            disUpdName = Single.fromCallable {
                firebaseRepo.updateName(newName)
            }
                .doOnSubscribe { _progressLiveData.postValue(true) }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe { task ->
                    task.addOnCompleteListener {
                        when {
                            it.isSuccessful -> {
                                sharedPreferenceWrapper.saveName(newName)
                                _nameSummaryLiveData.postValue(newName)
                                _progressLiveData.postValue(false)
                            }
                            else -> {
                                _progressLiveData.postValue(false)
                            }
                        }
                    }
                }
        }
    }

    private fun nameIsValid(nameToCheck: String): Boolean {
        val name = nameToCheck.trim { it <= ' ' }

        return when {
            name.isBlank() -> {
                _errorLiveData.postValue("Name can not be empty")
                false
            }
            else -> true
        }
    }

    private fun weightIsValid(weightToCheck: String): Boolean {
        val weight = weightToCheck.toIntOrNull()

        return when {
            weight == null || weight > 350 || weight <= 0 || weightToCheck.startsWith("0") -> {
                _errorLiveData.postValue("Weight is not valid")
                false
            }
            else -> true
        }
    }

    override fun onCleared() {
        super.onCleared()

        disDeleteAccount?.dispose()
        disDeleteAccount = null

        disUpdName?.dispose()
        disUpdName = null

        disUpdWeight?.dispose()
        disUpdWeight = null
    }

    val progressLiveData: LiveData<Boolean>
        get() = _progressLiveData
    val signOutLiveData: LiveData<Boolean>
        get() = _signOutLiveData
    val deleteLiveData: LiveData<Boolean>
        get() = _deleteLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
    val nameSummaryLiveData: LiveData<String>
        get() = _nameSummaryLiveData
    val weightSummaryLiveData: LiveData<String>
        get() = _weightSummaryLiveData
}