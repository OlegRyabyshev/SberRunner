package xyz.fcr.sberrunner.data.repository.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifySequence
import org.junit.Test

class FirebaseRepositoryTest {

    private val taskAuth: Task<AuthResult> = mockk()
    private val firebaseAuth = mockk<FirebaseAuth>()
    private val firebaseRepository: FirebaseRepository = FirebaseRepository(firebaseAuth)

    @Test
    fun registration() {
        every { firebaseAuth.createUserWithEmailAndPassword(EMAIL, PASS) } returns taskAuth

        firebaseRepository.registration(EMAIL, PASS)

        verifySequence {
            firebaseRepository.registration(EMAIL, PASS)
            firebaseAuth.createUserWithEmailAndPassword(EMAIL, PASS)
        }
    }

    @Test
    fun login() {
    }

    @Test
    fun sendResetEmail() {
    }

    @Test
    fun signOut() {
    }

    @Test
    fun deleteAccount() {
    }

    private companion object {
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val PASS = "pass"
        private const val WEIGHT = "weight"
    }
}