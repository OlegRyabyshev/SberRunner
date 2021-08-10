package xyz.fcr.sberrunner.viewmodel.firebase_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import xyz.fcr.sberrunner.data.firebase.FirebaseConnector
import xyz.fcr.sberrunner.data.model.RunModel

class RegistrationViewModel(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : ViewModel() {

    private val firebaseConnector = FirebaseConnector(firebaseAuth, fireStore)
    private val mProgressLiveData = MutableLiveData<Boolean>()
    private val mErrorLiveData = MutableLiveData<Throwable>()
    private val mWeatherLiveData = MutableLiveData<List<RunModel>>()


    fun proceedRegistration(name:String, email: String, password: String, weight: String) {
        firebaseConnector.firebaseRegistration(name, email, password, weight)
    }

    val progressLiveData: LiveData<Boolean>
        get() = mProgressLiveData
    val errorLiveData: LiveData<Throwable>
        get() = mErrorLiveData
    val weatherLiveData: LiveData<List<RunModel>>
        get() = mWeatherLiveData
}