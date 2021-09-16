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

        verify {
            firebaseAuth.createUserWithEmailAndPassword(EMAIL, PASS)
        }
    }

    @Test
    fun login() {
        firebaseRepository.login(EMAIL, PASS)

        verify {
            firebaseAuth.signInWithEmailAndPassword(EMAIL, PASS)
        }
    }

    @Test
    fun sendResetEmail() {
        firebaseRepository.sendResetEmail(EMAIL)

        verify {
            firebaseAuth.sendPasswordResetEmail(EMAIL)
        }
    }

    @Test
    fun signOut() {
        firebaseRepository.signOut()

        verify {
            firebaseAuth.signOut()
        }
    }

    @Test
    fun deleteAccount() {
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