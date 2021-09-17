package xyz.fcr.sberrunner.domain.interactor.firebase

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import xyz.fcr.sberrunner.data.datastore.firebase.FirebaseDataStore
import xyz.fcr.sberrunner.data.datastore.firestorage.StorageDataStore
import xyz.fcr.sberrunner.data.datastore.firestore.FirestoreDataStore
import xyz.fcr.sberrunner.data.converter.RunConverter
import xyz.fcr.sberrunner.domain.model.Run

class FirebaseInteractorTest {

    private val authDataStore: FirebaseDataStore = mockk()
    private val storeDataStore: FirestoreDataStore = mockk()
    private val storageDataStore: StorageDataStore = mockk()

    private val converter: RunConverter = RunConverter()

    private val firebaseInteractor = FirebaseInteractor(
        authDataStore,
        storeDataStore,
        storageDataStore,
        converter
    )

    @Test
    fun `login should not invoke function until subscription`() {
        val single = firebaseInteractor.login(EMAIl, PASS)

        verify(exactly = 0) {
            authDataStore.login(EMAIl, PASS)
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            authDataStore.login(EMAIl, PASS)
        }

        disposable.dispose()
    }

    @Test
    fun `registration should not invoke function until subscription`() {
        val single = firebaseInteractor.registration(EMAIl, PASS)

        verify(exactly = 0) {
            authDataStore.registration(EMAIl, PASS)
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            authDataStore.registration(EMAIl, PASS)
        }

        disposable.dispose()
    }

    @Test
    fun `sendResetEmail should not invoke function until subscription`() {
        val single = firebaseInteractor.sendResetEmail(EMAIl)

        verify(exactly = 0) {
            authDataStore.sendResetEmail(EMAIl)
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            authDataStore.sendResetEmail(EMAIl)
        }

        disposable.dispose()
    }

    @Test
    fun `signOut should not invoke function until subscription`() {
        val completable = firebaseInteractor.signOut()

        verify(exactly = 0) {
            authDataStore.signOut()
        }

        val disposable = completable.subscribe({}, {})

        verify(exactly = 1) {
            authDataStore.signOut()
        }

        disposable.dispose()
    }

    @Test
    fun `deleteAccount should not invoke function until subscription`() {
        val single = firebaseInteractor.deleteAccount()

        verify(exactly = 0) {
            authDataStore.deleteAccount()
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            authDataStore.deleteAccount()
        }

        disposable.dispose()
    }

    @Test
    fun `getDocumentFirestore should not invoke function until subscription`() {
        val single = firebaseInteractor.getDocumentFirestore()

        verify(exactly = 0) {
            storeDataStore.getDocumentFirestore()
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storeDataStore.getDocumentFirestore()
        }

        disposable.dispose()
    }

    @Test
    fun `updateWeight should not invoke function until subscription`() {
        val single = firebaseInteractor.updateWeight(WEIGHT)

        verify(exactly = 0) {
            storeDataStore.updateWeight(WEIGHT)
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storeDataStore.updateWeight(WEIGHT)
        }

        disposable.dispose()
    }

    @Test
    fun `updateName should not invoke function until subscription`() {
        val single = firebaseInteractor.updateName(NAME)

        verify(exactly = 0) {
            storeDataStore.updateName(NAME)
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storeDataStore.updateName(NAME)
        }

        disposable.dispose()
    }

    @Test
    fun `getAllRunsFromCloud should not invoke function until subscription`() {
        val single = firebaseInteractor.getAllRunsFromCloud()

        verify(exactly = 0) {
            storeDataStore.getAllRuns()
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storeDataStore.getAllRuns()
        }

        disposable.dispose()
    }

    @Test
    fun `fillUserDataInFirestore should not invoke function until subscription`() {
        val single = firebaseInteractor.fillUserDataInFirestore(NAME, WEIGHT)

        verify(exactly = 0) {
            storeDataStore.fillUserDataInFirestore(NAME, WEIGHT)
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storeDataStore.fillUserDataInFirestore(NAME, WEIGHT)
        }

        disposable.dispose()
    }

    @Test
    fun `switchToDeleteFlagsInCloud should not invoke function until subscription`() {
        val single = firebaseInteractor.switchToDeleteFlagsInCloud(listOfRuns)

        verify(exactly = 0) {
            storeDataStore.switchToDeleteFlags(
                converter.toRunEntityList(listOfRuns)
            )
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storeDataStore.switchToDeleteFlags(
                converter.toRunEntityList(listOfRuns)
            )
        }

        disposable.dispose()
    }

    @Test
    fun `uploadMissingFromDbToCloud should not invoke function until subscription`() {
        val single = firebaseInteractor.uploadMissingFromDbToCloud(listOfRuns)

        verify(exactly = 0) {
            storeDataStore.addRunsToCloud(
                converter.toRunEntityList(listOfRuns)
            )
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storeDataStore.addRunsToCloud(
                converter.toRunEntityList(listOfRuns)
            )
        }

        disposable.dispose()
    }

    @Test
    fun `uploadImageToStorage should not invoke function until subscription`() {
        val single = firebaseInteractor.uploadImageToStorage(run)

        verify(exactly = 0) {
            storageDataStore.addImage(
                converter.toRunEntity(run)
            )
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storageDataStore.addImage(
                converter.toRunEntity(run)
            )
        }

        disposable.dispose()
    }

    @Test
    fun `downloadImageFromStorage should not invoke function until subscription`() {
        val single = firebaseInteractor.downloadImageFromStorage(run)

        verify(exactly = 0) {
            storageDataStore.getImage(
                converter.toRunEntity(run)
            )
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            storageDataStore.getImage(
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