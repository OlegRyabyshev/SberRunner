package xyz.fcr.sberrunner.domain.interactor.firebase

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import xyz.fcr.sberrunner.data.repository.firebase.FirebaseRepository
import xyz.fcr.sberrunner.data.repository.firestorage.StorageRepository
import xyz.fcr.sberrunner.data.repository.firestore.FirestoreRepository
import xyz.fcr.sberrunner.domain.converter.RunConverter
import xyz.fcr.sberrunner.presentation.model.Run

class FirebaseInteractorTest {

    private val authRepository: FirebaseRepository = mockk()
    private val storeRepository: FirestoreRepository = mockk()
    private val storageRepository: StorageRepository = mockk()

    private val converter: RunConverter = RunConverter()

    private val firebaseInteractor = FirebaseInteractor(
        authRepository,
        storeRepository,
        storageRepository,
        converter
    )

    @Test
    fun `login should not invoke function until subscription`() {
        val single = firebaseInteractor.login(EMAIl, PASS)

        verify(exactly = 0) {
            authRepository.login(EMAIl, PASS)
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            authRepository.login(EMAIl, PASS)
        }

        disposable.dispose()
    }

    @Test
    fun `registration should not invoke function until subscription`() {
        val single = firebaseInteractor.registration(EMAIl, PASS)

        verify(exactly = 0) {
            authRepository.registration(EMAIl, PASS)
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            authRepository.registration(EMAIl, PASS)
        }

        disposable.dispose()
    }

    @Test
    fun `sendResetEmail should not invoke function until subscription`() {
        val single = firebaseInteractor.sendResetEmail(EMAIl)

        verify(exactly = 0) {
            authRepository.sendResetEmail(EMAIl)
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            authRepository.sendResetEmail(EMAIl)
        }

        disposable.dispose()
    }

    @Test
    fun `signOut should not invoke function until subscription`() {
        val completable = firebaseInteractor.signOut()

        verify(exactly = 0) {
            authRepository.signOut()
        }

        val disposable = completable.subscribe({}, {})

        verify(exactly = 1) {
            authRepository.signOut()
        }

        disposable.dispose()
    }

    @Test
    fun `deleteAccount should not invoke function until subscription`() {
        val single = firebaseInteractor.deleteAccount()

        verify(exactly = 0) {
            authRepository.deleteAccount()
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            authRepository.deleteAccount()
        }

        disposable.dispose()
    }

    @Test
    fun `getDocumentFirestore should not invoke function until subscription`() {
        val single = firebaseInteractor.getDocumentFirestore()

        verify(exactly = 0) {
            storeRepository.getDocumentFirestore()
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storeRepository.getDocumentFirestore()
        }

        disposable.dispose()
    }

    @Test
    fun `updateWeight should not invoke function until subscription`() {
        val single = firebaseInteractor.updateWeight(WEIGHT)

        verify(exactly = 0) {
            storeRepository.updateWeight(WEIGHT)
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storeRepository.updateWeight(WEIGHT)
        }

        disposable.dispose()
    }

    @Test
    fun `updateName should not invoke function until subscription`() {
        val single = firebaseInteractor.updateName(NAME)

        verify(exactly = 0) {
            storeRepository.updateName(NAME)
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storeRepository.updateName(NAME)
        }

        disposable.dispose()
    }

    @Test
    fun `getAllRunsFromCloud should not invoke function until subscription`() {
        val single = firebaseInteractor.getAllRunsFromCloud()

        verify(exactly = 0) {
            storeRepository.getAllRuns()
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storeRepository.getAllRuns()
        }

        disposable.dispose()
    }

    @Test
    fun `fillUserDataInFirestore should not invoke function until subscription`() {
        val single = firebaseInteractor.fillUserDataInFirestore(NAME, WEIGHT)

        verify(exactly = 0) {
            storeRepository.fillUserDataInFirestore(NAME, WEIGHT)
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storeRepository.fillUserDataInFirestore(NAME, WEIGHT)
        }

        disposable.dispose()
    }

    @Test
    fun `switchToDeleteFlagsInCloud should not invoke function until subscription`() {
        val single = firebaseInteractor.switchToDeleteFlagsInCloud(listOfRuns)

        verify(exactly = 0) {
            storeRepository.switchToDeleteFlags(
                converter.toRunEntityList(listOfRuns)
            )
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storeRepository.switchToDeleteFlags(
                converter.toRunEntityList(listOfRuns)
            )
        }

        disposable.dispose()
    }

    @Test
    fun `uploadMissingFromDbToCloud should not invoke function until subscription`() {
        val single = firebaseInteractor.uploadMissingFromDbToCloud(listOfRuns)

        verify(exactly = 0) {
            storeRepository.addRunsToCloud(
                converter.toRunEntityList(listOfRuns)
            )
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storeRepository.addRunsToCloud(
                converter.toRunEntityList(listOfRuns)
            )
        }

        disposable.dispose()
    }

    @Test
    fun `uploadImageToStorage should not invoke function until subscription`() {
        val single = firebaseInteractor.uploadImageToStorage(run)

        verify(exactly = 0) {
            storageRepository.addImage(
                converter.toRunEntity(run)
            )
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storageRepository.addImage(
                converter.toRunEntity(run)
            )
        }

        disposable.dispose()
    }

    @Test
    fun `downloadImageFromStorage should not invoke function until subscription`() {
        val single = firebaseInteractor.downloadImageFromStorage(run)

        verify(exactly = 0) {
            storageRepository.getImage(
                converter.toRunEntity(run)
            )
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storageRepository.getImage(
                converter.toRunEntity(run)
            )
        }

        disposable.dispose()
    }

    private companion object {
        private const val EMAIl = "test@gmail.com"
        private const val PASS = "123456"

        private const val NAME = "Bob"
        private const val WEIGHT = "87"

        private val run = Run()
        private val listOfRuns = listOf(Run(), Run(), Run())
    }
}