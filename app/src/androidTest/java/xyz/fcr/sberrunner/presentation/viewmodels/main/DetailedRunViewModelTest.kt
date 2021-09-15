package xyz.fcr.sberrunner.presentation.viewmodels.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import xyz.fcr.sberrunner.data.repository.shared.SharedPreferenceWrapper
import xyz.fcr.sberrunner.domain.interactor.db.RoomInteractor
import xyz.fcr.sberrunner.presentation.model.Run
import xyz.fcr.sberrunner.utils.schedulers.SchedulersProvider

@RunWith(JUnit4::class)
class DetailedRunViewModelTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    private val databaseInteractor: RoomInteractor = mockk(relaxed = true)
    private val sharedPreferenceWrapper: SharedPreferenceWrapper = mockk(relaxed = true)
    private val schedulersProvider: SchedulersProvider = mockk(relaxed = true)

    private val _runLiveData: Observer<Run> = mockk()
    private val _unitsLiveData: Observer<Boolean> = mockk()

    private lateinit var detailedRunViewModel: DetailedRunViewModel

    @Before
    fun setUp() {
        detailedRunViewModel = DetailedRunViewModel(
            databaseInteractor,
            sharedPreferenceWrapper,
            schedulersProvider
        )

        detailedRunViewModel.runLiveData.observeForever(_runLiveData)
        detailedRunViewModel.unitsLiveData.observeForever(_unitsLiveData)

        every { _runLiveData.onChanged(any()) } just Runs
        every { _unitsLiveData.onChanged(any()) } just Runs
    }

    @Test
    fun assertSharedPrefsSetsTrueIfMetric() {
        every { sharedPreferenceWrapper.isMetric() } returns METRIC

        detailedRunViewModel.setUnits()

        assertTrue(detailedRunViewModel.unitsLiveData.value == METRIC)

    }

    @Test
    fun assertSharedPrefsSetsFalseIfImperial() {
        every { sharedPreferenceWrapper.isMetric() } returns IMPERIAL

        detailedRunViewModel.setUnits()

        assertTrue(detailedRunViewModel.unitsLiveData.value == IMPERIAL)
    }

    @Test
    fun assertGettingRunFromDb() {
        every { databaseInteractor.getRun(TIMESTAMP) } returns Single.just(mockRun)

        databaseInteractor.getRun(TIMESTAMP)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue{
                it.timestamp == TIMESTAMP
            }
    }

    @Test
    fun assertGettingGunFromDb() {
        every { databaseInteractor.getRun(TIMESTAMP) } returns Single.just(mockRun)

        detailedRunViewModel.getRunFromDB(TIMESTAMP)

        databaseInteractor.getRun(TIMESTAMP)
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue{
                it.timestamp == TIMESTAMP
            }
    }

    private companion object {
        private const val METRIC = true
        private const val IMPERIAL = false

        private const val TIMESTAMP = 100L

        private val mockRun = Run(
            timestamp = TIMESTAMP
        )
    }
}