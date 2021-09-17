package xyz.fcr.sberrunner.data.datastore.firestorage

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.data.util.BitmapConverter

/**
 * Имплементация интерфейса [ImageDataStoreInterface], служит для взаимодействия с Firebase Storage
 *
 * @param firebaseAuth [FirebaseAuth] - объект аутентификации
 * @param firebaseStorage [FirebaseStorage] - объект хранилища Firebase Storage
 * @param bitmapConverter [BitmapConverter] - конвертер Bitmap <-> ByteArray
 */
class StorageDataStore(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val bitmapConverter: BitmapConverter
) : ImageDataStoreInterface {

    private val userId
        get() = firebaseAuth.currentUser?.uid ?: throw IllegalAccessError("Can't find user id")

    /**
     * Добавление изображения в хранилище Firebase Storage
     *
     * @param run [RunEntity] - объект бега
     * @return [UploadTask] - результат загрузки изображения
     */
    override fun addImage(run: RunEntity): UploadTask {
        val imageName = run.timestamp.toString()
        val byteImage = bitmapConverter.fromBitmap(run.mapImage)

        val storageRef = firebaseStorage.reference.child("$userId/$imageName.png")

        return storageRef.putBytes(byteImage!!)
    }

    /**
     * Получение изображения из хранилища Firebase Storage
     *
     * @param run [RunEntity] - объект бега
     * @return [Task] - результат асинхронного запроса получения изображения
     */
    override fun getImage(run: RunEntity): Task<ByteArray> {
        val imageName = run.timestamp.toString()
        val storageRef = firebaseStorage.reference.child("$userId/$imageName.png")

        return storageRef.getBytes(TEN_MEGABYTES)

    }

    private companion object {
        private const val TEN_MEGABYTES: Long = 1024 * 1024 * 10 // 10 мегабайт
    }
}