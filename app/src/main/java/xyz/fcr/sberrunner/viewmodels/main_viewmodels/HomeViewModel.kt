package xyz.fcr.sberrunner.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.room.RunEntity
import xyz.fcr.sberrunner.viewmodels.SingleLiveEvent

class HomeViewModel() : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _successLiveData = MutableLiveData<List<RunEntity>?>()
    private val _errorLiveData = SingleLiveEvent<String>()

    private var disposable: Disposable? = null

    fun loadListOfRuns() {
//        disposable = Single.fromCallable {}
//            .doOnSubscribe { _progressLiveData.postValue(true) }
//            .subscribeOn(schedulersProvider.io())
//            .observeOn(schedulersProvider.ui())
//            .subscribe { task ->
//                task.addOnCompleteListener {
//                    when {
//                        it.isSuccessful -> _successLiveData.postValue(Constants.VALID)
//                        else -> {
//                            _successLiveData.postValue(it.exception?.message.toString())
//                        }
//                    }
//                    _progressLiveData.postValue(false)
//                }
//            }
        _successLiveData.postValue(null)
    }

    override fun onCleared() {
        super.onCleared()

        disposable?.dispose()
        disposable = null
    }

    val progressLiveData: LiveData<Boolean>
        get() = _progressLiveData
    val successLiveData: LiveData<List<RunEntity>?>
        get() = _successLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
}