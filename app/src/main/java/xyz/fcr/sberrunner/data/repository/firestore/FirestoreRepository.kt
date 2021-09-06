package xyz.fcr.sberrunner.data.repository.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import xyz.fcr.sberrunner.utils.Constants

/**
 * Имплементация интерфейса [IFirestoreRepository], служит для взаимодействия с объектами FirebaseAuth и FirebaseStore
 *
 * @param firebaseAuth [FirebaseAuth] - объект аутентификации
 * @param fireStore [FirebaseFirestore] - объект облачной NoSQL DB
 */
class FirestoreRepository(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : IFirestoreRepository {

    private val userId
        get() = firebaseAuth.currentUser?.uid ?: throw IllegalAccessError("Can't find user id")

    /**
     * Запрос на обновление имени пользоваателя в Firestore
     *
     * @return Task<Void> - результат асинхронного запроса обновления имени
     */
    override fun updateName(newName: String): Task<Void> {
        val document = fireStore.collection(Constants.USER_TABLE).document(userId)
        return document.update(Constants.NAME, newName)
    }

    /**
     * Запрос на обновление веса пользоваателя в Firestore
     *
     * @return Task<Void> - результат асинхронного запроса обновления веса
     */
    override fun updateWeight(newWeight: String): Task<Void> {
        val document = fireStore.collection(Constants.USER_TABLE).document(userId)
        return document.update(Constants.WEIGHT, newWeight)
    }

    /**
     * Сохранение данных пользователя
     *
     * @param name [String] - имя пользователя
     * @param weight [String] - вес пользователя
     */
    override fun fillUserDataInFirestore(name: String, weight: String): Task<Void> {
        val user = hashMapOf(Constants.NAME to name, Constants.WEIGHT to weight)
        val document = fireStore.collection(Constants.USER_TABLE).document(userId)
        return document.set(user)
    }

    /**
     * Получение документа пользователя из Firestore
     *
     * @return Task<DocumentSnapshot> - результат асинхронного запроса получения документа
     */
    override fun getDocumentFirestore(): Task<DocumentSnapshot> {
        return fireStore.collection(Constants.USER_TABLE).document(userId).get()
    }

    override fun getAllRuns(): Task<QuerySnapshot> {
        return fireStore.collection(Constants.RUNS_TABLE).document(userId).collection(Constants.RUNS_TABLE).get()
    }
}