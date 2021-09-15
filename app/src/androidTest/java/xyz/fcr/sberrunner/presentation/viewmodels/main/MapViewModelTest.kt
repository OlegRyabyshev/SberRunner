package xyz.fcr.sberrunner.presentation.viewmodels.main

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import xyz.fcr.sberrunner.data.repository.shared.SharedPreferenceWrapper

@RunWith(JUnit4::class)
class MapViewModelTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    private val fusedLocationProvider: FusedLocationProviderClient = mockk(relaxed = true)
    private val sharedPreferenceWrapper: SharedPreferenceWrapper = mockk(relaxed = true)

    private val _progressLiveData: Observer<Boolean> = mockk()
    private val _locationLiveData: Observer<Location> = mockk()
    private val _historyLiveData: Observer<LatLng> = mockk()
    private val _errorLiveData: Observer<String> = mockk()


    private lateinit var mapViewModel: MapViewModel
    @Before
    fun setUp() {
        mapViewModel = MapViewModel(fusedLocationProvider, sharedPreferenceWrapper)

        mapViewModel.progressLiveData.observeForever(_progressLiveData)
        mapViewModel.locationLiveData.observeForever(_locationLiveData)
        mapViewModel.historyLiveData.observeForever(_historyLiveData)
        mapViewModel.errorLiveData.observeForever(_errorLiveData)

        every { _progressLiveData.onChanged(any()) } just Runs
        every { _locationLiveData.onChanged(any()) } just Runs
        every { _historyLiveData.onChanged(any()) } just Runs
        every { _errorLiveData.onChanged(any()) } just Runs
    }

    @Test
    fun assertNameEmailPasswordWeightIsValid() {

    }

    private companion object {

    }
}