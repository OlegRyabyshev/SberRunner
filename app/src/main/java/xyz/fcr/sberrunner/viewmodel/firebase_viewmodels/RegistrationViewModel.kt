package xyz.fcr.sberrunner.viewmodel.firebase_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.firebase.FirebaseConnector
import xyz.fcr.sberrunner.data.model.RunModel

class RegistrationViewModel(
    private var firebaseConnector: FirebaseConnector
) : ViewModel() {

    private val mProgressLiveData = MutableLiveData<Boolean>()
    private val mErrorLiveData = MutableLiveData<Throwable>()
    private val mWeatherLiveData = MutableLiveData<List<RunModel>>()

    private var disposable: Disposable? = null

    fun tryToRegister() {
//        firebaseConnector.firebaseRegistration()
    }

    val progressLiveData: LiveData<Boolean>
        get() = mProgressLiveData
    val errorLiveData: LiveData<Throwable>
        get() = mErrorLiveData
    val weatherLiveData: LiveData<List<RunModel>>
        get() = mWeatherLiveData
}