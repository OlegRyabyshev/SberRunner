package xyz.fcr.sberrunner.data.repository.firebase

import com.google.common.truth.Truth.assertThat
import io.mockk.spyk
import org.junit.Test

class FirebaseRepositoryTest {

    private val firebaseRepository: FirebaseRepository = spyk()

    @Test
    fun registration() {
        val result = firebaseRepository.registration(NAME, EMAIL, PASS, WEIGHT)
        assertThat(result).isNotNull()
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