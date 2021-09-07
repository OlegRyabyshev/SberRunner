package xyz.fcr.sberrunner.data.repository.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import xyz.fcr.sberrunner.data.model.RunEntity

interface IFirestoreRepository {

    /**
     * Запрос на обновление имени пользоваателя в Firestore
     *
     * @param newName [String] - новое имя пользователя
     * @return [Task] - результат асинхронного запроса обновления имени
     */
    fun updateName(newName: String): Task<Void>

    /**
     * Запрос на обновление веса пользоваателя в Firestore
     *
     * @param newWeight [String] - новый вес пользователя
     * @return [Task] - результат асинхронного запроса обновления веса
     */
    fun updateWeight(newWeight: String): Task<Void>

    /**
     * Получение документа пользователя из Firestore
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
     * Получение всех забегов пользователя из Firestore
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
     * Добавление забегов в Firestore
     *
     * @param list [List] - список забегов на добавление
     *
     * @return [Task] - результат асинхронного запроса добавления всех забегов
     */
    fun addRunsToCloud(list: List<RunEntity>): Task<Void>
}