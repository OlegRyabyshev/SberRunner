package xyz.fcr.sberrunner.presentation.viewmodels.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import xyz.fcr.sberrunner.data.datastore.shared.SharedPreferenceWrapper
import xyz.fcr.sberrunner.domain.interactor.db.DatabaseInteractor
import xyz.fcr.sberrunner.domain.interactor.cloud.CloudInteractor
import xyz.fcr.sberrunner.utils.schedulers.SchedulersProvider

class SharedSettingsViewModelTest {
    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    private val firebaseInteractor: CloudInteractor = mockk(relaxed = true)
    private val databaseInteractor: DatabaseInteractor = mockk(relaxed = true)
    private val schedulersProvider: SchedulersProvider = mockk(relaxed = true)
    private val sharedPreferenceWrapper: SharedPreferenceWrapper = mockk(relaxed = true)

    private val _progressLiveData: Observer<Boolean> = mockk()
    private val _signOutLiveData: Observer<Boolean> = mockk()
    private val _errorLiveData: Observer<String> = mockk()

    private lateinit var settingsViewModel: SharedSettingsViewModel

    @Before
    fun setUp() {
        settingsViewModel = SharedSettingsViewModel(
            firebaseInteractor,
            databaseInteractor,
            schedulersProvider,
            sharedPreferenceWrapper
        )

        settingsViewModel.progressLiveData.observeForever(_progressLiveData)
        settingsViewModel.signOutLiveData.observeForever(_signOutLiveData)
        settingsViewModel.errorLiveData.observeForever(_errorLiveData)

        every { _progressLiveData.onChanged(any()) } just Runs
        every { _signOutLiveData.onChanged(any()) } just Runs
        every { _errorLiveData.onChanged(any()) } just Runs

        every { schedulersProvider.io() } returns Schedulers.trampoline()
        every { schedulersProvider.ui() } returns Schedulers.trampoline()
    }

    @Test
    fun testSignOut() {
        every { firebaseInteractor.signOut() } returns Completable.complete()

        settingsViewModel.signOut()

        verify(exactly = 0) {
            _errorLiveData.onChanged(any())
        }

        verify(exactly = 1) {
            _progressLiveData.onChanged(true)
            _signOutLiveData.onChanged(true)
        }
    }

    @Test
    fun testFailSignOut() {
        every { firebaseInteractor.signOut() } returns Completable.error(Exception())

        settingsViewModel.signOut()

        verify(exactly = 0) {
            _signOutLiveData.onChanged(true)
        }

        verify(exactly = 1) {
            _progressLiveData.onChanged(true)
            _errorLiveData.onChanged(any())
        }
    }

    @Test
    fun testDeleteAccount() {
        val deleteResult = mockk<Void>()

        val deleteTask = mockk<Task<Void>>()
        every { deleteTask.isComplete } returns true
        every { deleteTask.isSuccessful } returns true
        every { deleteTask.result } returns deleteResult

        val slotDelete = slot<OnCompleteListener<Void>>()

        every { deleteTask.addOnCompleteListener(capture(slotDelete)) } answers {
            slotDelete.captured.onComplete(deleteTask)
            deleteTask
        }

        every { firebaseInteractor.deleteAccount() } returns Single.just(deleteTask)

        settingsViewModel.deleteAccount()

        verify(exactly = 0) {
            _progressLiveData.onChanged(false)
        }

        verify(exactly = 1) {
            _progressLiveData.onChanged(true)
        }
    }

    @Test
    fun testUpdateName() {
        val updNameResult = mockk<Void>()

        val updNameTask = mockk<Task<Void>>()
        every { updNameTask.isComplete } returns true
        every { updNameTask.isSuccessful } returns true
        every { updNameTask.result } returns updNameResult

        val slotUpdName = slot<OnCompleteListener<Void>>()

        every { updNameTask.addOnCompleteListener(capture(slotUpdName)) } answers {
            slotUpdName.captured.onComplete(updNameTask)
            updNameTask
        }

        every { firebaseInteractor.updateName(VALID_NAME) } returns Single.just(updNameTask)

        settingsViewModel.updateName(VALID_NAME)

        verify(exactly = 1) {
            _progressLiveData.onChanged(true)
            _progressLiveData.onChanged(false)
        }
    }

    @Test
    fun testUpdateWeight() {
        val updWeightResult = mockk<Void>()

        val updWeightTask = mockk<Task<Void>>()
        every { updWeightTask.isComplete } returns true
        every { updWeightTask.isSuccessful } returns true
        every { updWeightTask.result } returns updWeightResult

        val slotUpdWeight = slot<OnCompleteListener<Void>>()

        every { updWeightTask.addOnCompleteListener(capture(slotUpdWeight)) } answers {
            slotUpdWeight.captured.onComplete(updWeightTask)
            updWeightTask
        }

        every { firebaseInteractor.updateName(VALID_WEIGHT) } returns Single.just(updWeightTask)

        settingsViewModel.updateName(VALID_WEIGHT)

        verify(exactly = 1) {
            _progressLiveData.onChanged(true)
            _progressLiveData.onChanged(false)
        }
    }

    private companion object{
        private const val VALID_NAME = "Name"
        private const val VALID_WEIGHT = "78"
    }
}