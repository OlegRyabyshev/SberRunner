package xyz.fcr.sberrunner.data.repository.firestore

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.utils.Constants

/**
 * Имплементация интерфейса [IFirestoreRepository], служит для взаимодействия с объектами FirebaseAuth и FirebaseStore
 *
 * @param auth [FirebaseAuth] - объект аутентификации
 * @param firestore [FirebaseFirestore] - объект облачной NoSQL DB
 */
class FirestoreRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : IFirestoreRepository {

    private val userId
        get() = auth.currentUser?.uid ?: throw IllegalAccessError("Can't find user id")

    /**
     * Запрос на обновление имени пользоваателя в Firestore
     *
     * @return Task<Void> - результат асинхронного запроса обновления имени
     */
    override fun updateName(newName: String): Task<Void> {
        val document = firestore.collection(Constants.USER_TABLE).document(userId)
        return document.update(Constants.NAME, newName)
    }

    /**
     * Запрос на обновление веса пользоваателя в Firestore
     *
     * @return Task<Void> - результат асинхронного запроса обновления веса
     */
    override fun updateWeight(newWeight: String): Task<Void> {
        val document = firestore.collection(Constants.USER_TABLE).document(userId)
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
        val document = firestore.collection(Constants.USER_TABLE).document(userId)
        return document.set(user)
    }

    /**
     * Получение документа пользователя из Firestore
     *
     * @return Task<DocumentSnapshot> - результат асинхронного запроса получения документа
     */
    override fun getDocumentFirestore(): Task<DocumentSnapshot> {
        return firestore.collection(Constants.USER_TABLE).document(userId).get()
    }

    override fun getAllRuns(): Task<QuerySnapshot> {
        return firestore.collection(Constants.RUNS_TABLE).document(userId).collection(Constants.RUNS_TABLE).get()
    }

    override fun switchToDeleteFlags(listToSwitch: List<RunEntity>): Task<Void> {
        val timeStampList: List<Long> = listToSwitch.map { it.timestamp }

        val documents: List<DocumentReference> = timeStampList.map {
            firestore
                .collection(Constants.RUNS_TABLE)
                .document(userId)
                .collection(Constants.RUNS_TABLE)
                .document(it.toString())
        }

        return firestore.runBatch { batch ->
            documents.forEach { doc ->
                batch.update(doc, "toDeleteFlag", true)
            }
        }
    }

    override fun addRunsToCloud(list: List<RunEntity>): Task<Void> {
        val batch = firestore.batch()

        list.forEach { run ->
            val user = hashMapOf(
                "avgSpeedInKMH" to run.avgSpeedInKMH,
                "calories" to run.calories,
                "distanceInMeters" to run.distanceInMeters,
                "timeInMillis" to run.timeInMillis,
                "timestamp" to run.timestamp,
                "toDeleteFlag" to run.toDeleteFlag
            )

            val docRef = firestore
                .collection(Constants.RUNS_TABLE)
                .document(userId)
                .collection(Constants.RUNS_TABLE)
                .document(run.timestamp.toString())

            batch.set(docRef, user)
        }

        return batch.commit()
    }
}