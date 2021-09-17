package xyz.fcr.sberrunner.data.datastore.firebase

import com.google.firebase.auth.FirebaseAuth
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class FirebaseDataStoreTest {
    private val firebaseAuth = mockk<FirebaseAuth>(relaxed = true)
    private val firebaseDataStore: FirebaseDataStore = FirebaseDataStore(firebaseAuth)

    @Test
    fun registration() {
        firebaseDataStore.registration(EMAIL, PASS)

        verify(exactly = 1) {
            firebaseAuth.createUserWithEmailAndPassword(EMAIL, PASS)
        }
    }

    @Test
    fun login() {
        firebaseDataStore.login(EMAIL, PASS)

        verify(exactly = 1) {
            firebaseAuth.signInWithEmailAndPassword(EMAIL, PASS)
        }
    }

    @Test
    fun sendResetEmail() {
        firebaseDataStore.sendResetEmail(EMAIL)

        verify(exactly = 1) {
            firebaseAuth.sendPasswordResetEmail(EMAIL)
        }
    }

    @Test
    fun signOut() {
        firebaseDataStore.signOut()

        verify(exactly = 1) {
            firebaseAuth.signOut()
        }
    }

    @Test
    fun deleteAccount() {
        firebaseDataStore.signOut()

        verify(exactly = 1) {
            firebaseAuth.signOut()
        }
    }

    private companion object {
        private const val EMAIL = "email"
        private const val PASS = "pass"
    }
}