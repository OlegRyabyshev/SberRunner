package xyz.fcr.sberrunner.data.repository.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import xyz.fcr.sberrunner.utils.Constants.NAME
import xyz.fcr.sberrunner.utils.Constants.USER
import xyz.fcr.sberrunner.utils.Constants.WEIGHT

/**
 * Имплементация интерфейса [IFirebaseRepository], служит для взаимодействия с объектами FirebaseAuth и FirebaseStore
 *
 * @param firebaseAuth [FirebaseAuth] - объект аутентификации
 * @param fireStore [FirebaseFirestore] - объект облачной NoSQL DB
 */
class FirebaseRepository(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : IFirebaseRepository {

    /**
     * Регистрация пользователя
     *
     * @param name [String] - имя пользователя
     * @param email [String] - email пользователя
     * @param password [String] - пароль пользователя
     * @param weight [String] - вес пользователя
     *
     * @return Task<AuthResult> - асинхронный результат выполенения регистрации
     */
    override fun registration(name: String, email: String, password: String, weight: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fillUserDataInFirestore(name, weight)
                }
            }
    }

    /**
     * Сохранение данных пользователя
     *
     * @param name [String] - имя пользователя
     * @param weight [String] - вес пользователя
     */
    private fun fillUserDataInFirestore(name: String, weight: String) {
        val user = hashMapOf(NAME to name, WEIGHT to weight)
        val userId = firebaseAuth.currentUser?.uid

        val document = fireStore.collection(USER).document(userId!!)
        document.set(user)
    }

    /**
     * Получение документа пользователя
     *
     * @return Task<DocumentSnapshot> - результат выполенения запроса
     */
    override fun getDocumentFirestore(): Task<DocumentSnapshot> {
        val userId = firebaseAuth.currentUser?.uid
        return fireStore.collection(USER).document(userId!!).get()
    }

    override fun login(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    override fun sendResetEmail(email: String): Task<Void> {
        return firebaseAuth.sendPasswordResetEmail(email)
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    override fun deleteAccount(): Task<Void> {
        val user = firebaseAuth.currentUser
        return user!!.delete()
    }

    override fun updateName(newName: String): Task<Void> {
        val userId = firebaseAuth.currentUser?.uid
        val document = fireStore.collection(USER).document(userId!!)
        return document.update(NAME, newName)
    }

    override fun updateWeight(newWeight: String): Task<Void> {
        val userId = firebaseAuth.currentUser?.uid
        val document = fireStore.collection(USER).document(userId!!)
        return document.update(WEIGHT, newWeight)
    }
}