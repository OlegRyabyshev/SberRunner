package xyz.fcr.sberrunner.data.repository.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

/**
 * Интерфейс взаимодействия с аккаунтом пользователя
 */
interface AuthRepositoryInterface {

    /**
     * Регистрация пользователя
     *
     * @param email [String] - email пользователя
     * @param password [String] - пароль пользователя
     *
     * @return [Task] - асинхронный результат выполенения регистрации
     */
    fun registration(
        email: String,
        password: String
    ): Task<AuthResult>

    /**
     * Вход в аккаунт
     *
     * @param email [String] - email пользователя
     * @param password [String] - пароль пользователя
     *
     * @return [Task] - результат асинхронного запроса входа в аккаунт
     */
    fun login(email: String, password: String): Task<AuthResult>

    /**
     * Отправка сообщения на email пользователя со сбросом пароля
     *
     * @param email [String] - email пользователя
     *
     * @return [Task] - результат асинхронного запроса сброса
     */
    fun sendResetEmail(email: String): Task<Void>

    /**
     * Выход пользователя из аккаунта
     */
    fun signOut()

    /**
     * Удаление пользователем своего аккаунта
     *
     * @return [Task] - результат асинхронного запроса удаления аккаунта
     */
    fun deleteAccount(): Task<Void>
}