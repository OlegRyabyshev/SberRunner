package xyz.fcr.sberrunner.data.repository.firestorage

import com.google.android.gms.tasks.Task
import com.google.firebase.storage.UploadTask
import xyz.fcr.sberrunner.data.model.RunEntity

/**
 * Интерфейс взаимодействия с изображениями
 */
interface ImageRepositoryInterface {

    /**
     * Добавление изображения в хранилище
     *
     * @param run [RunEntity] - объект бега
     * @return [UploadTask] - результат загрузки изображения
     */
    fun addImage(run: RunEntity): UploadTask

    /**
     * Получение изображения из хранилища
     *
     * @param run [RunEntity] - объект бега
     * @return [Task] - результат асинхронного запроса получения изображения
     */
    fun getImage(run: RunEntity): Task<ByteArray>
}