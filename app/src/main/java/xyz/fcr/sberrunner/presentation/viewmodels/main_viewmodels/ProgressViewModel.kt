package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.fcr.sberrunner.data.repository.db.IDatabaseRepository
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import javax.inject.Inject

class ProgressViewModel @Inject constructor(
    databaseRepository: IDatabaseRepository,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper
) : ViewModel() {
    private val _unitsLiveData = MutableLiveData<Boolean>()

    val listOfRuns = databaseRepository.getAllRuns()

    fun setUnits() {
       _unitsLiveData.postValue(sharedPreferenceWrapper.isMetric())
    }

    val unitsLiveData: LiveData<Boolean>
        get() = _unitsLiveData
}