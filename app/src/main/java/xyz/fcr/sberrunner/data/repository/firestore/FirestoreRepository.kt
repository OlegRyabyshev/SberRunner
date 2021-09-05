package xyz.fcr.sberrunner.data.repository.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import xyz.fcr.sberrunner.data.repository.firebase.IFirebaseRepository
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

    /**
     * Запрос на обновление имени пользоваателя в Firestore
     *
     * @return Task<Void> - результат асинхронного запроса обновления имени
     */
    override fun updateName(newName: String): Task<Void> {
        val userId = firebaseAuth.currentUser?.uid
        val document = fireStore.collection(Constants.USER).document(userId!!)
        return document.update(Constants.NAME, newName)
    }

    /**
     * Запрос на обновление веса пользоваателя в Firestore
     *
     * @return Task<Void> - результат асинхронного запроса обновления веса
     */
    override fun updateWeight(newWeight: String): Task<Void> {
        val userId = firebaseAuth.currentUser?.uid
        val document = fireStore.collection(Constants.USER).document(userId!!)
        return document.update(Constants.WEIGHT, newWeight)
    }

    /**
     * Сохранение данных пользователя
     *
     * @param name [String] - имя пользователя
     * @param weight [String] - вес пользователя
     */
    override fun fillUserDataInFirestore(name: String, weight: String) {
        val user = hashMapOf(Constants.NAME to name, Constants.WEIGHT to weight)
        val userId = firebaseAuth.currentUser?.uid

        val document = fireStore.collection(Constants.USER).document(userId!!)
        document.set(user)
    }

    /**
     * Получение документа пользователя из Firestore
     *
     * @return Task<DocumentSnapshot> - результат асинхронного запроса получения документа
     */
    override fun getDocumentFirestore(): Task<DocumentSnapshot> {
        val userId = firebaseAuth.currentUser?.uid
        return fireStore.collection(Constants.USER).document(userId!!).get()
    }
}