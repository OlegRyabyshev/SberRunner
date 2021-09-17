package xyz.fcr.sberrunner.presentation.viewmodels.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import io.mockk.*
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import xyz.fcr.sberrunner.data.datastore.shared.SharedPreferenceWrapper
import xyz.fcr.sberrunner.domain.interactor.db.RoomInteractor
import xyz.fcr.sberrunner.presentation.model.Run
import xyz.fcr.sberrunner.utils.schedulers.SchedulersProvider

@RunWith(JUnit4::class)
class RunViewModelTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    private val databaseInteractor: RoomInteractor = mockk(relaxed = true)
    private val schedulersProvider: SchedulersProvider = mockk(relaxed = true)
    private val sharedPreferenceWrapper: SharedPreferenceWrapper = mockk(relaxed = true)

    private val _historyLiveData: Observer<LatLng> = mockk()
    private val _unitsLiveData: Observer<Boolean> = mockk()
    private val _weightLiveData: Observer<Int> = mockk()

    private lateinit var runViewModel: RunViewModel

    @Before
    fun setUp() {
        runViewModel = RunViewModel(
            databaseInteractor,
            schedulersProvider,
            sharedPreferenceWrapper
        )

        runViewModel.historyLiveData.observeForever(_historyLiveData)
        runViewModel.unitsLiveData.observeForever(_unitsLiveData)
        runViewModel.weightLiveData.observeForever(_weightLiveData)

        every { _historyLiveData.onChanged(any()) } just Runs
        every { _unitsLiveData.onChanged(any()) } just Runs
        every { _weightLiveData.onChanged(any()) } just Runs
    }

    @Test
    fun assertInsertRun() {
        runViewModel.insertRun(mockRun)

        verify(exactly = 1) {
            databaseInteractor.addRun(mockRun)
        }
    }

    @Test
    fun assertSharedPrefsSetsTrueIfMetric() {
        every { sharedPreferenceWrapper.isMetric() } returns METRIC

        runViewModel.setUnits()

        verify(exactly = 1){
            _unitsLiveData.onChanged(METRIC)
        }

        assertTrue(runViewModel.unitsLiveData.value == METRIC)
    }

    @Test
    fun assertSharedPrefsSetsFalseIfImperial() {
        every { sharedPreferenceWrapper.isMetric() } returns IMPERIAL

        runViewModel.setUnits()

        verify(exactly = 1){
            _unitsLiveData.onChanged(IMPERIAL)
        }

        assertTrue(runViewModel.unitsLiveData.value == IMPERIAL)
    }

    @Test
    fun getHistoryLocation(){
        every { sharedPreferenceWrapper.getRunLatitude() } returns LATITUDE_F
        every { sharedPreferenceWrapper.getRunLongitude() } returns LONGITUDE_F

        runViewModel.setToLastKnownLocationIfAny()

        verify(exactly = 1) {
            _historyLiveData.onChanged(LatLng(LATITUDE_F.toDouble(), LONGITUDE_F.toDouble()))
        }
    }

    @Test
    fun returnLocationWithoutHistory(){
        every { sharedPreferenceWrapper.getRunLatitude() } returns DEFAULT_LAT
        every { sharedPreferenceWrapper.getRunLongitude() } returns DEFAULT_LON

        runViewModel.setToLastKnownLocationIfAny()

        verify(exactly = 1) {
            _historyLiveData.onChanged(LatLng(DEFAULT_LAT.toDouble(), DEFAULT_LON.toDouble()))
        }
    }

    @Test
    fun returnWeightOfUser(){
        every { sharedPreferenceWrapper.getIntWeight() } returns WEIGHT

        runViewModel.setWeight()

        verify(exactly = 1) {
            _weightLiveData.onChanged(WEIGHT)
        }
    }

    private companion object {
        private const val METRIC = true
        private const val IMPERIAL = false

        private const val LATITUDE_F: Float = 50.0F
        private const val LONGITUDE_F: Float = 50.0F

        private const val DEFAULT_LAT = 55.75f // Если нету истории
        private const val DEFAULT_LON = 37.61f

        private val mockRun = Run(timestamp = 100L)

        private const val WEIGHT = 70
    }
}