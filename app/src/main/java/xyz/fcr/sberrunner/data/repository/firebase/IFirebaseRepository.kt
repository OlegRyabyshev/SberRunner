package xyz.fcr.sberrunner.data.repository.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Интерфейс взаимодействия с объектами Firebase
 */
interface IFirebaseRepository {

    /**
     * Регистрация
     */
    fun registration(name: String, email: String, password: String, weight: String): Task<AuthResult>


    /**
     * Вход а аккаунт
     */
    fun login(email: String, password: String): Task<AuthResult>

    /**
     * Получения папки в Firestore
     */
    fun getDocumentFirestore(): Task<DocumentSnapshot>

    /**
     * Отправка запроса на сброс пароля (сообщение на почту)
     */
    fun sendResetEmail(email: String): Task<Void>

    /**
     * Удаление текущего пользователя
     */
    fun signOut()

    /**
     * Удаление аккаунта пользователя
     */
    fun deleteAccount(): Task<Void>

    /**
     * Обновление имени пользователя в FireStore
     */
    fun updateName(newName: String): Task<Void>

    /**
     * Обновление веса пользователя в FireStore
     */
    fun updateWeight(newWeight: String): Task<Void>

}