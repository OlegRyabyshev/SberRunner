package xyz.fcr.sberrunner.data.repository.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Before
import org.junit.Test
import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.utils.Constants

class FirestoreRepositoryTest {

    private val firebaseAuth: FirebaseAuth = mockk()
    private val firebaseFirestore: FirebaseFirestore = mockk()

    private val firestoreRepository = FirestoreRepository(firebaseAuth, firebaseFirestore)

    private val collectionReference: CollectionReference = mockk()
    private val documentReference: DocumentReference = mockk()
    private val firebaseUser: FirebaseUser = mockk()

    private val taskVoid: Task<Void> = mockk()
    private val taskDocumentSnapshot: Task<DocumentSnapshot> = mockk()
    private val taskQuerySnapshot: Task<QuerySnapshot> = mockk()
    private val writeBatch: WriteBatch = mockk()

    @Before
    fun beforeInit() {
        every { firebaseAuth.currentUser } returns firebaseUser
        every { firebaseAuth.currentUser?.uid } returns USER_ID
    }

    @Test
    fun updateName() {
        every { firebaseFirestore.collection(Constants.USER_TABLE) } returns collectionReference
        every { collectionReference.document(any()) } returns documentReference
        every { documentReference.update(Constants.NAME, NAME) } returns taskVoid

        firestoreRepository.updateName(NAME)

        verifyOrder {
            firebaseFirestore.collection(Constants.USER_TABLE)
            collectionReference.document(any())
            documentReference.update(Constants.NAME, NAME)
        }
    }

    @Test
    fun updateWeight() {
        every { firebaseFirestore.collection(Constants.USER_TABLE) } returns collectionReference
        every { collectionReference.document(any()) } returns documentReference
        every { documentReference.update(Constants.WEIGHT, WEIGHT) } returns taskVoid

        firestoreRepository.updateWeight(WEIGHT)

        verifyOrder {
            firebaseFirestore.collection(Constants.USER_TABLE)
            collectionReference.document(any())
            documentReference.update(Constants.WEIGHT, WEIGHT)
        }
    }

    @Test
    fun fillUserDataInFirestore() {
        every { firebaseFirestore.collection(Constants.USER_TABLE) } returns collectionReference
        every { collectionReference.document(any()) } returns documentReference
        every { documentReference.set(any()) } returns taskVoid

        firestoreRepository.fillUserDataInFirestore(NAME, WEIGHT)

        verifyOrder {
            firebaseFirestore.collection(Constants.USER_TABLE)
            collectionReference.document(any())
            documentReference.set(any())
        }
    }

    @Test
    fun getDocumentFirestore() {
        every { firebaseFirestore.collection(Constants.USER_TABLE) } returns collectionReference
        every { collectionReference.document(any()) } returns documentReference
        every { documentReference.get() } returns taskDocumentSnapshot

        firestoreRepository.getDocumentFirestore()

        verifyOrder {
            firebaseFirestore.collection(Constants.USER_TABLE)
            collectionReference.document(any())
            documentReference.get()
            taskDocumentSnapshot
        }
    }

    @Test
    fun getAllRuns() {
        every { firebaseFirestore.collection(Constants.RUNS_TABLE) } returns collectionReference
        every { collectionReference.document(any()) } returns documentReference
        every { documentReference.collection(Constants.RUNS_TABLE) } returns collectionReference
        every { collectionReference.get(Source.SERVER) } returns taskQuerySnapshot

        firestoreRepository.getAllRuns()

        verifyOrder {
            firebaseFirestore.collection(Constants.RUNS_TABLE)
            collectionReference.document(any())
            documentReference.collection(Constants.RUNS_TABLE)
            collectionReference.get(Source.SERVER)
            taskQuerySnapshot
        }
    }


    @Test
    fun switchToDeleteFlags() {
        every { firebaseFirestore.collection(Constants.RUNS_TABLE) } returns collectionReference
        every { collectionReference.document(any()) } returns documentReference
        every { documentReference.collection(Constants.RUNS_TABLE) } returns collectionReference
        every { firebaseFirestore.runBatch(any()) } returns taskVoid

        firestoreRepository.switchToDeleteFlags(LIST)

        verify {
            firebaseFirestore.runBatch(any())
        }
    }

    @Test
    fun addRunsToCloud() {
        every { firebaseFirestore.batch() } returns writeBatch
        every { writeBatch.commit() } returns taskVoid

        firestoreRepository.addRunsToCloud(LIST)

        verifyOrder {
            firebaseFirestore.batch()
            writeBatch.commit()
        }
    }

    private companion object {
        private const val USER_ID = "user_id"

        private const val NAME = "name"
        private const val WEIGHT = "87"

        private val LIST = listOf<RunEntity>()
    }
}