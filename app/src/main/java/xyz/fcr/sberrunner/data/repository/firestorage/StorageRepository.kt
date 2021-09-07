package xyz.fcr.sberrunner.data.repository.firestorage

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

/**
 * Имплементация интерфейса [IStorageRepository], служит для взаимодействия с хранилищем Firebase
 *
 */
class StorageRepository(
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage
) : IStorageRepository {

    private val userId
        get() = firebaseAuth.currentUser?.uid ?: throw IllegalAccessError("Can't find user id")

    override fun savePicture() {
        var storageRef = storage.reference
    }
}