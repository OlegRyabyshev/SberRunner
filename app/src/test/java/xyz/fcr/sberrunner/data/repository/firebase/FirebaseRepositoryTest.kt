package xyz.fcr.sberrunner.data.repository.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.Test

class FirebaseRepositoryTest {

    private val taskAuth: Task<AuthResult> = mockk()
    private val taskVoid: Task<Void> = mockk()

    private val firebaseAuth = mockk<FirebaseAuth>()
    private val firebaseRepository: FirebaseRepository = FirebaseRepository(firebaseAuth)

    @Test
    fun registration() {
        every { firebaseAuth.createUserWithEmailAndPassword(EMAIL, PASS) } returns taskAuth

        firebaseRepository.registration(EMAIL, PASS)

        verifySequence {
            firebaseAuth.createUserWithEmailAndPassword(EMAIL, PASS)
            taskAuth
        }
    }

    @Test
    fun login() {
        every { firebaseAuth.signInWithEmailAndPassword(EMAIL, PASS) } returns taskAuth

        firebaseRepository.login(EMAIL, PASS)

        verify {
            firebaseAuth.signInWithEmailAndPassword(EMAIL, PASS)
            taskAuth
        }
    }

    @Test
    fun sendResetEmail() {
        every { firebaseAuth.sendPasswordResetEmail(EMAIL) } returns taskVoid

        firebaseRepository.sendResetEmail(EMAIL)

        verify {
            firebaseAuth.sendPasswordResetEmail(EMAIL)
            taskVoid
        }
    }

    @Test
    fun signOut() {
        every { firebaseAuth.signOut() } returns Unit

        firebaseRepository.signOut()

        verify {
            firebaseAuth.signOut()
        }
    }

    @Test
    fun deleteAccount() {
        every { firebaseAuth.signOut() } returns Unit

        firebaseRepository.signOut()

        verify {
            firebaseAuth.signOut()
        }
    }

    private companion object {
        private const val EMAIL = "email"
        private const val PASS = "pass"
    }
}