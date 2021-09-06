package xyz.fcr.sberrunner.data.repository.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import xyz.fcr.sberrunner.data.model.RunEntity

interface IFirestoreRepository {

    /**
     * Запрос на обновление имени пользоваателя в Firestore
     *
     * @return Task<Void> - результат асинхронного запроса обновления имени
     */
    fun updateName(newName: String): Task<Void>

    /**
     * Запрос на обновление веса пользоваателя в Firestore
     *
     * @return Task<Void> - результат асинхронного запроса обновления веса
     */
    fun updateWeight(newWeight: String): Task<Void>

    /**
     * Получение документа пользователя из Firestore
     *
     * @return Task<DocumentSnapshot> - результат асинхронного запроса получения документа
     */
    fun getDocumentFirestore(): Task<DocumentSnapshot>

    /**
     * Сохранение данных пользователя
     *
     * @param name [String] - имя пользователя
     * @param weight [String] - вес пользователя
     */
    fun fillUserDataInFirestore(name: String, weight: String): Task<Void>

    fun getAllRuns(): Task<QuerySnapshot>
}