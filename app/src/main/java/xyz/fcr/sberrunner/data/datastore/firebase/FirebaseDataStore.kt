package xyz.fcr.sberrunner.data.datastore.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

/**
 * Имплементация интерфейса [AuthDataStoreInterface], служит для взаимодействия с объектом FirebaseAuth
 *
 * @param firebaseAuth [FirebaseAuth] - объект аутентификации
 */
class FirebaseDataStore(
    private val firebaseAuth: FirebaseAuth
) : AuthDataStoreInterface {

    /**
     * Регистрация пользователя
     *
     * @param email [String] - email пользователя
     * @param password [String] - пароль пользователя
     *
     * @return [Task] - асинхронный результат выполенения регистрации
     */
    override fun registration(
        email: String,
        password: String
    ): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
    }

    /**
     * Вход в аккаунт
     *
     * @param email [String] - email пользователя
     * @param password [String] - пароль пользователя
     *
     * @return [Task] - результат асинхронного запроса входа в аккаунт
     */
    override fun login(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    /**
     * Отправка сообщения на email пользователя со сбросом пароля
     *
     * @param email [String] - email пользователя
     *
     * @return [Task] - результат асинхронного запроса сброса
     */
    override fun sendResetEmail(email: String): Task<Void> {
        return firebaseAuth.sendPasswordResetEmail(email)
    }

    /**
     * Выход пользователя из аккаунта
     */
    override fun signOut() {
       firebaseAuth.signOut()
    }

    /**
     * Удаление пользователем своего аккаунта
     *
     * @return [Task] - результат асинхронного запроса удаления аккаунта
     */
    override fun deleteAccount(): Task<Void> {
        val user = firebaseAuth.currentUser
        return user!!.delete()
    }
}