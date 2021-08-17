package xyz.fcr.sberrunner.viewmodels.main_viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.repository.FirebaseRepository
import xyz.fcr.sberrunner.utils.SchedulersProvider

class SharedSettingsViewModel(
    private var firebaseRepo: FirebaseRepository,
    private var schedulersProvider: SchedulersProvider
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _signOutLiveData = MutableLiveData<Boolean>()
    private val _deleteLiveData = MutableLiveData<Boolean>()

    private var disReset: Disposable? = null
    private var disSignIn: Disposable? = null

    fun exitAccount() {
        firebaseRepo.signOut()
        _signOutLiveData.postValue(true)
    }

    fun deleteAccount() {
        disReset = Single.fromCallable { firebaseRepo.deleteAccount() }
            .doOnSubscribe { _progressLiveData.postValue(true) }
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe { task ->
                task?.addOnCompleteListener {
                    when {
                        it.isSuccessful -> _deleteLiveData.postValue(true)
                        else -> {
                            _deleteLiveData.postValue(false)
                            Log.d("s", task.exception?.message.toString())
                        }
                    }

                    _progressLiveData.postValue(false)
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
    val signOutLiveData: LiveData<Boolean>
        get() = _signOutLiveData
    val deleteLiveData: LiveData<Boolean>
        get() = _deleteLiveData
}