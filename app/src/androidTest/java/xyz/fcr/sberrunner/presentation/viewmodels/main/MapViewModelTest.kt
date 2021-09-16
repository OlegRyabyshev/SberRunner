package xyz.fcr.sberrunner.presentation.viewmodels.main

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.mockk.*
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
    fun gettingCurrentLocation() {
        val mockedResult = mockk<Location>()
        every { mockedResult.latitude } returns LATITUDE
        every { mockedResult.longitude } returns LONGITUDE

        val mockedTask = mockk<Task<Location>>()
        every { mockedTask.isSuccessful } returns true
        every { mockedTask.result } returns mockedResult

        val slot = slot<OnCompleteListener<Location>>()

        every { mockedTask.addOnCompleteListener(capture(slot)) } answers {
            slot.captured.onComplete(mockedTask)
            mockedTask
        }

        every { fusedLocationProvider.getCurrentLocation(any(), any()) } returns mockedTask

        mapViewModel.getCurrentLocation()

        verify(exactly = 0) {
            _errorLiveData.onChanged(any())
        }

        verify(exactly = 1) {
            _progressLiveData.onChanged(true)
            _progressLiveData.onChanged(false)
            _locationLiveData.onChanged(mockedResult)
            sharedPreferenceWrapper.saveMapLatitude(location.latitude.toFloat())
            sharedPreferenceWrapper.saveMapLongitude(location.longitude.toFloat())
        }
    }

    @Test
    fun gettingErrorInsteadOfLocation() {
        val mockedTask = mockk<Task<Location>>()
        every { mockedTask.isSuccessful } returns true
        every { mockedTask.result } returns null

        val slot = slot<OnCompleteListener<Location>>()

        every { mockedTask.addOnCompleteListener(capture(slot)) } answers {
            slot.captured.onComplete(mockedTask)
            mockedTask
        }

        every { fusedLocationProvider.getCurrentLocation(any(), any()) } returns mockedTask

        mapViewModel.getCurrentLocation()

        verify(exactly = 0) {
            _locationLiveData.onChanged(any())
            sharedPreferenceWrapper.saveMapLatitude(any())
            sharedPreferenceWrapper.saveMapLongitude(any())
        }

        verify(exactly = 1) {
            _progressLiveData.onChanged(true)
            _progressLiveData.onChanged(false)
            _errorLiveData.onChanged(any())
        }
    }

    @Test
    fun getHistoryLocation(){
        every { sharedPreferenceWrapper.getMapLatitude() } returns LATITUDE_F
        every { sharedPreferenceWrapper.getMapLongitude() } returns LONGITUDE_F

        mapViewModel.setToLastKnownLocationIfAny()

        verify(exactly = 1) {
            _historyLiveData.onChanged(LatLng(LATITUDE_F.toDouble(), LONGITUDE_F.toDouble()))
        }
    }

    @Test
    fun returnLocationWithoutHistory(){
        every { sharedPreferenceWrapper.getMapLatitude() } returns DEFAULT_LAT
        every { sharedPreferenceWrapper.getMapLongitude() } returns DEFAULT_LON

        mapViewModel.setToLastKnownLocationIfAny()

        verify(exactly = 1) {
            _historyLiveData.onChanged(LatLng(DEFAULT_LAT.toDouble(), DEFAULT_LON.toDouble()))
        }
    }

    private companion object {
        private const val LATITUDE: Double = 50.0
        private const val LONGITUDE: Double = 50.0

        private const val LATITUDE_F: Float = 50.0F
        private const val LONGITUDE_F: Float = 50.0F

        private const val DEFAULT_LAT = 55.75f // Если нету истории
        private const val DEFAULT_LON = 37.61f

        private val location = Location("system").apply {
            latitude = LATITUDE
            longitude = LONGITUDE
        }
    }
}