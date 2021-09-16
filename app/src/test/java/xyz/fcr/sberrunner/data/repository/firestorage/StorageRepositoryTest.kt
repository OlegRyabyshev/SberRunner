package xyz.fcr.sberrunner.data.repository.firestorage

import android.graphics.Bitmap
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.Before
import org.junit.Test
import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.data.util.BitmapConverter

class StorageRepositoryTest {

    private val firebaseAuth: FirebaseAuth = mockk()
    private val firebaseStorage: FirebaseStorage = mockk()
    private val bitmapConverter: BitmapConverter = mockk()

    private val uploadTask: UploadTask = mockk()
    private val firebaseUser: FirebaseUser = mockk()
    private val storageReference: StorageReference = mockk()
    private val map: Bitmap = mockk()

    private val taskByteArray: Task<ByteArray> = mockk()

    private val storageRepository = StorageRepository(firebaseAuth, firebaseStorage, bitmapConverter)

    @Before
    fun beforeInit() {
        every { firebaseAuth.currentUser } returns firebaseUser
        every { firebaseAuth.currentUser?.uid } returns USER_ID
    }

    @Test
    fun addImage() {
        val mockRun = RunEntity(timestamp = 0L, mapImage = map)

        every { bitmapConverter.fromBitmap(any()) } returns ByteArray(0)
        every { firebaseStorage.reference.child(any()) } returns storageReference
        every { storageReference.putBytes(any()) } returns uploadTask

        storageRepository.addImage(mockRun)

        verifyOrder {
            firebaseStorage.reference.child(any())
            storageReference.putBytes(any())
            uploadTask
        }
    }

    @Test
    fun getImage() {
        val mockRun = RunEntity(timestamp = 0L)

        every { firebaseStorage.reference.child(any()) } returns storageReference
        every { storageReference.getBytes(any()) } returns taskByteArray

        storageRepository.getImage(mockRun)

        verifyOrder {
            firebaseStorage.reference.child(any())
            storageReference.getBytes(any())
            taskByteArray
        }
    }

    private companion object {
        private const val USER_ID = "user_id"
    }
}