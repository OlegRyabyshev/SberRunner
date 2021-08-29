package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.repository.db.IDatabaseRepository
import xyz.fcr.sberrunner.utils.Constants
import javax.inject.Inject

class RunViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _historyLiveData = MutableLiveData<LatLng>()

    fun insertRun(run: Run) {
        databaseRepository.addRun(run)
    }

    fun setToLastKnownLocationIfAny() {
        val lat = sharedPreferences.getFloat(Constants.MAP_LAT_KEY, Constants.MOSCOW_LAT)
        val lon = sharedPreferences.getFloat(Constants.MAP_LON_KEY, Constants.MOSCOW_LON)
        _historyLiveData.postValue(LatLng(lat.toDouble(), lon.toDouble()))
    }

    val historyLiveData: LiveData<LatLng>
        get() = _historyLiveData
}