package xyz.fcr.sberrunner.data.datastore.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import xyz.fcr.sberrunner.data.model.RunEntity

/**
 * Интерфейс взаимодействия с информацией пользователя
 */
interface StoreDataStoreInterface {

    /**
     * Запрос на обновление имени пользователя
     *
     * @param newName [String] - новое имя пользователя
     * @return [Task] - результат асинхронного запроса обновления имени
     */
    fun updateName(newName: String): Task<Void>

    /**
     * Запрос на обновление веса пользователя
     *
     * @param newWeight [String] - новый вес пользователя
     * @return [Task] - результат асинхронного запроса обновления веса
     */
    fun updateWeight(newWeight: String): Task<Void>

    /**
     * Получение документа пользователя
     *
     * @return [Task] - результат асинхронного запроса получения документа
     */
    fun getDocumentFirestore(): Task<DocumentSnapshot>

    /**
     * Сохранение данных пользователя
     *
     * @param name [String] - имя пользователя
     * @param weight [String] - вес пользователя
     * @return [Task] - результат асинхронного запроса обновления информации о пользователе
     */
    fun fillUserDataInFirestore(name: String, weight: String): Task<Void>

    /**
     * Получение всех забегов пользователя
     *
     * @return [Task] - результат асинхронного запроса получения всех забегов
     */
    fun getAllRuns(): Task<QuerySnapshot>

    /**
     * Переключние флагов удаления забегов в Firestore
     * (они будут удалены со всех устройств при синхронизации)
     *
     * @param listToSwitch [List] - список забегов на переключение
     *
     * @return [Task] - результат асинхронного запроса на переключение флагов
     */
    fun switchToDeleteFlags(listToSwitch: List<RunEntity>): Task<Void>

    /**
     * Добавление забегов
     *
     * @param list [List] - список забегов на добавление
     *
     * @return [Task] - результат асинхронного запроса добавления всех забегов
     */
    fun addRunsToCloud(list: List<RunEntity>): Task<Void>
}