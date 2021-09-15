package xyz.fcr.sberrunner.presentation.viewmodels.firebase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import xyz.fcr.sberrunner.data.repository.shared.SharedPreferenceWrapper
import xyz.fcr.sberrunner.domain.interactor.firebase.FirebaseInteractor
import xyz.fcr.sberrunner.utils.schedulers.SchedulersProvider

@RunWith(JUnit4::class)
class LoginViewModelTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    private val firebaseInteractor: FirebaseInteractor = mockk(relaxed = true)
    private val schedulersProvider: SchedulersProvider = mockk(relaxed = true)
    private val sharedPreferenceWrapper: SharedPreferenceWrapper = mockk(relaxed = true)

    private val _progressLiveData: Observer<Boolean> = mockk()
    private val _loginLiveData: Observer<Boolean> = mockk()
    private val _resetLiveData: Observer<Boolean> = mockk()

    private val _errorEmail: Observer<String> = mockk()
    private val _errorPass: Observer<String> = mockk()
    private val _errorFirebase: Observer<String> = mockk()

    private lateinit var loginViewModel: LoginViewModel

    @Before
    fun setUp() {
        loginViewModel = LoginViewModel(
            firebaseInteractor,
            schedulersProvider,
            sharedPreferenceWrapper
        )

        loginViewModel.progressLiveData.observeForever(_progressLiveData)
        loginViewModel.loginLiveData.observeForever(_loginLiveData)
        loginViewModel.resetLiveData.observeForever(_resetLiveData)

        loginViewModel.errorEmail.observeForever(_errorEmail)
        loginViewModel.errorPass.observeForever(_errorPass)
        loginViewModel.errorFirebase.observeForever(_errorFirebase)

        every { _progressLiveData.onChanged(any()) } just Runs
        every { _loginLiveData.onChanged(any()) } just Runs
        every { _resetLiveData.onChanged(any()) } just Runs

        every { _errorEmail.onChanged(any()) } just Runs
        every { _errorPass.onChanged(any()) } just Runs
        every { _errorFirebase.onChanged(any()) } just Runs
    }

    @Test
    fun assertEmailAndPasswordIsValid() {
        loginViewModel.initSignIn(VALID_EMAIL, VALID_PASS)

        verify(exactly = 1) {
            _errorEmail.onChanged(VALID)
            _errorPass.onChanged(VALID)
        }
    }

    @Test
    fun assertEmailAndPasswordIsNotValid() {
        loginViewModel.initSignIn(NON_VALID_EMAIL, NON_VALID_PASS)

        verify(exactly = 1) {
            _errorEmail.onChanged(not(VALID))
            _errorPass.onChanged(not(VALID))
        }
    }

    @Test
    fun assertEmailAndPasswordIsEmptyAndNonValid() {
        loginViewModel.initSignIn(EMPTY_EMAIL, EMPTY_PASS)

        verify(exactly = 1) {
            _errorEmail.onChanged(EMPTY)
            _errorPass.onChanged(EMPTY)
        }
    }

    private companion object {
        private const val VALID_EMAIL = "bob@gmail.com"
        private const val VALID_PASS = "bobIsTheBest123"

        private const val NON_VALID_EMAIL = "bob@"
        private const val NON_VALID_PASS = "123"

        private const val EMPTY_EMAIL = "bob@"
        private const val EMPTY_PASS = "123"

        private const val VALID = "valid"
        private const val EMPTY = ""
    }
}