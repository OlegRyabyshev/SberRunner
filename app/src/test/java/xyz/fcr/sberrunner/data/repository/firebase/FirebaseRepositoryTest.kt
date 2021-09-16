package xyz.fcr.sberrunner.data.repository.firebase

import com.google.firebase.auth.FirebaseAuth
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class FirebaseRepositoryTest {
    private val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
    private val firebaseRepository: FirebaseRepository = FirebaseRepository(firebaseAuth)

    @Test
    fun registration() {
        firebaseRepository.registration(EMAIL, PASS)

        verify(exactly = 1) {
            firebaseAuth.createUserWithEmailAndPassword(EMAIL, PASS)
        }
    }

    @Test
    fun login() {
        firebaseRepository.login(EMAIL, PASS)

        verify(exactly = 1) {
            firebaseAuth.signInWithEmailAndPassword(EMAIL, PASS)
        }
    }

    @Test
    fun sendResetEmail() {
        firebaseRepository.sendResetEmail(EMAIL)

        verify(exactly = 1) {
            firebaseAuth.sendPasswordResetEmail(EMAIL)
        }
    }

    @Test
    fun signOut() {
        firebaseRepository.signOut()

        verify(exactly = 1) {
            firebaseAuth.signOut()
        }
    }

    @Test
    fun deleteAccount() {
        firebaseRepository.signOut()

        verify(exactly = 1) {
            firebaseAuth.signOut()
        }
    }

    private companion object {
        private const val EMAIL = "email"
        private const val PASS = "pass"
    }
}