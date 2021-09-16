package xyz.fcr.sberrunner.presentation.viewmodels.firebase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import io.mockk.*
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import xyz.fcr.sberrunner.data.repository.shared.SharedPreferenceWrapper
import xyz.fcr.sberrunner.domain.interactor.firebase.FirebaseInteractor
import xyz.fcr.sberrunner.utils.Constants
import xyz.fcr.sberrunner.utils.schedulers.SchedulersProvider

@RunWith(JUnit4::class)
class RegistrationViewModelTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    private val firebaseInteractor: FirebaseInteractor = mockk(relaxed = true)
    private val schedulersProvider: SchedulersProvider = mockk(relaxed = true)
    private val sharedPreferenceWrapper: SharedPreferenceWrapper = mockk(relaxed = true)

    private val _progressLiveData: Observer<Boolean> = mockk()
    private val _successLiveData: Observer<String> = mockk()
    private val _errorFirebase: Observer<String> = mockk()

    private val _errorName: Observer<String> = mockk()
    private val _errorEmail: Observer<String> = mockk()
    private val _errorPass: Observer<String> = mockk()
    private val _errorWeight: Observer<String> = mockk()

    private lateinit var registrationViewModel: RegistrationViewModel

    @Before
    fun setUp() {
        registrationViewModel = RegistrationViewModel(
            firebaseInteractor,
            schedulersProvider,
            sharedPreferenceWrapper
        )

        registrationViewModel.progressLiveData.observeForever(_progressLiveData)
        registrationViewModel.successLiveData.observeForever(_successLiveData)
        registrationViewModel.errorFirebase.observeForever(_errorFirebase)

        registrationViewModel.errorName.observeForever(_errorName)
        registrationViewModel.errorEmail.observeForever(_errorEmail)
        registrationViewModel.errorPass.observeForever(_errorPass)
        registrationViewModel.errorWeight.observeForever(_errorWeight)

        every { _progressLiveData.onChanged(any()) } just Runs
        every { _successLiveData.onChanged(any()) } just Runs
        every { _errorFirebase.onChanged(any()) } just Runs

        every { _errorName.onChanged(any()) } just Runs
        every { _errorEmail.onChanged(any()) } just Runs
        every { _errorPass.onChanged(any()) } just Runs
        every { _errorWeight.onChanged(any()) } just Runs

        every { schedulersProvider.io() } returns Schedulers.trampoline()
        every { schedulersProvider.ui() } returns Schedulers.trampoline()
    }

    @Test
    fun assertNameEmailPasswordWeightIsValid() {
        registrationViewModel.initRegistration(
            VALID_NAME,
            VALID_EMAIL,
            VALID_PASS,
            VALID_WEIGHT
        )

        verify(exactly = 1) {
            _errorName.onChanged(VALID)
            _errorEmail.onChanged(VALID)
            _errorPass.onChanged(VALID)
            _errorWeight.onChanged(VALID)
        }
    }

    @Test
    fun assertNameEmailPasswordWeightIsNotValid() {
        registrationViewModel.initRegistration(
            NON_VALID_NAME,
            NON_VALID_EMAIL,
            NON_VALID_PASS,
            NON_VALID_WEIGHT
        )

        verify(exactly = 1) {
            _errorName.onChanged(not(VALID))
            _errorEmail.onChanged(not(VALID))
            _errorPass.onChanged(not(VALID))
            _errorWeight.onChanged(not(VALID))
        }
    }

    @Test
    fun assertNameEmailPasswordWeightIsEmpty() {
        registrationViewModel.initRegistration(
            EMPTY_NAME,
            EMPTY_EMAIL,
            EMPTY_PASS,
            EMPTY_WEIGHT
        )

        verify(exactly = 1) {
            _errorName.onChanged(not(VALID))
            _errorEmail.onChanged(not(VALID))
            _errorPass.onChanged(not(VALID))
            _errorWeight.onChanged(not(VALID))
        }
    }

    @Test
    fun assertRegistration() {
        val regResult = mockk<AuthResult>()
        val regTask = mockk<Task<AuthResult>>()
        every { regTask.isComplete } returns true
        every { regTask.isSuccessful } returns true
        every { regTask.result } returns regResult

        val slotAuth = slot<OnCompleteListener<AuthResult>>()

        every { regTask.addOnCompleteListener(capture(slotAuth)) } answers {
            slotAuth.captured.onComplete(regTask)
            regTask
        }

        every { firebaseInteractor.registration(any(), any()) } returns Single.just(regTask)

        registrationViewModel.initRegistration(VALID_NAME, VALID_EMAIL, VALID_PASS, VALID_WEIGHT)

        verify(exactly = 0) {
            _successLiveData.onChanged(not(VALID))
        }

        verify(exactly = 1) {
            _progressLiveData.onChanged(false)
            _progressLiveData.onChanged(true)
        }
    }

    private companion object {
        private const val VALID_NAME = "Name"
        private const val VALID_EMAIL = "bob@gmail.com"
        private const val VALID_PASS = "bobIsTheBest123"
        private const val VALID_WEIGHT = "67"

        private const val NON_VALID_NAME = ""
        private const val NON_VALID_EMAIL = "bob@"
        private const val NON_VALID_PASS = "123"
        private const val NON_VALID_WEIGHT = "400"

        private const val EMPTY_NAME = ""
        private const val EMPTY_EMAIL = ""
        private const val EMPTY_PASS = ""
        private const val EMPTY_WEIGHT = ""

        private const val VALID = "valid"
    }
}