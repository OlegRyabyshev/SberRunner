package xyz.fcr.sberrunner.presentation.viewmodels.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Assert.*
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
class ProgressViewModelTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    private val databaseInteractor: RoomInteractor = mockk(relaxed = true)
    private val sharedPreferenceWrapper: SharedPreferenceWrapper = mockk(relaxed = true)
    private val schedulersProvider: SchedulersProvider = mockk(relaxed = true)

    private val _unitsLiveData: Observer<Boolean> = mockk()
    private val _runsLiveData: Observer<List<Run>> = mockk()

    private lateinit var progressViewModel: ProgressViewModel

    @Before
    fun setUp() {
        progressViewModel = ProgressViewModel(
            databaseInteractor,
            sharedPreferenceWrapper,
            schedulersProvider
        )

        progressViewModel.listOfRunsLiveData.observeForever(_runsLiveData)
        progressViewModel.unitsLiveData.observeForever(_unitsLiveData)

        every { _runsLiveData.onChanged(any()) } just Runs
        every { _unitsLiveData.onChanged(any()) } just Runs
    }

    @Test
    fun assertSharedPrefsSetsTrueIfMetric() {
        every { sharedPreferenceWrapper.isMetric() } returns METRIC

        progressViewModel.setUnits()

        assertTrue(progressViewModel.unitsLiveData.value == METRIC)
    }

    @Test
    fun assertSharedPrefsSetsFalseIfImperial() {
        every { sharedPreferenceWrapper.isMetric() } returns IMPERIAL

        progressViewModel.setUnits()

        assertTrue(progressViewModel.unitsLiveData.value == IMPERIAL)
    }

    @Test
    fun assertGettingRunsFromDb() {
        every { databaseInteractor.getAllRuns() } returns Single.just(mockRuns)

        progressViewModel.updateListOfRuns()

        databaseInteractor.getAllRuns()
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValue {
                it == mockRuns
            }
    }

    private companion object {
        private const val METRIC = true
        private const val IMPERIAL = false

        private val mockRuns = listOf(
            Run(timestamp = 100L),
            Run(timestamp = 200L),
            Run(timestamp = 300L)
        )
    }
}