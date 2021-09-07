package xyz.fcr.sberrunner.domain.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import xyz.fcr.sberrunner.data.model.RunEntity

/**
 * Интерфейс взааимодействия с Firebase
 */
interface IFirebaseInteractor {

    /**
     * Авторизация в Firebase
     *
     * @param email [String] - почта пользователя
     * @param password [String] - пароль пользователя
     *
     * @return [Single] - асинхронный результат авторизации
     */
    fun login(email: String, password: String): Single<Task<AuthResult>>

    /**
     * Регистрация в Firebase
     *
     * @param name [String] - имя пользователя
     * @param email [String] - email пользователя
     * @param pass [String] - пароль пользователя
     * @param weight [String] - вес пользователя
     *
     * @return [Single] - асинхронный результат регистрации
     */
    fun registration(name: String, email: String, pass: String, weight: String): Single<Task<AuthResult>>

    /**
     * Отправка сообщения на восстановление почты в Firebase
     *
     * @param email [String] - email пользователя
     *
     * @return [Completable] - асинхронный результат отправки сообщения
     */
    fun sendResetEmail(email: String): Completable

    /**
     * Отправка запроса на выход пользователя из приложения в Firebase
     *
     * @return [Completable] - асинхронный результат отправки запроса
     */
    fun signOut(): Completable

    /**
     * Удаление аккаунта текущего пользователя из Firebase
     *
     * @return [Completable] - асинхронный результат удаления
     */
    fun deleteAccount(): Completable

    /**
     * Получение документа из Firestore
     *
     * @return [Single] - асинхронный результат получения документа
     */
    fun getDocumentFirestore(): Single<Task<DocumentSnapshot>>

    /**
     * Обновление веса пользователя в Firestore
     *
     * @param weight [String] - вес пользователя
     *
     * @return [Completable] - асинхронный результат обновления веса
     */
    fun updateWeight(weight: String): Completable

    /**
     * Обновление имени пользователя в Firestore
     *
     * @param name [String] - имя пользователя
     *
     * @return [Completable] - асинхронный результат обновления имени
     */
    fun updateName(name: String): Completable

    /**
     * Получение запроса на список всех забегов из Firestore
     *
     * @return [Single] - асинхронный результат получения списка
     */
    fun getAllRunsFromCloud(): Single<Task<QuerySnapshot>>

    /**
     * Изначальное занесение имени и веса пользователя в Firestore
     *
     * @param name [String] - имя пользователя
     * @param weight [String] - вес пользователя
     *
     * @return [Completable] - асинхронный результат занесения данных
     */
    fun fillUserDataInFirestore(name: String, weight: String): Completable

    /**
     * Результат изменения флагов на удаление в Firestore
     *
     * @param listToSwitch [List] - список забегов
     *
     * @return [Completable] - асинхронный результат изменения флагов
     */
    fun switchToDeleteFlagsInCloud(listToSwitch: List<RunEntity>): Completable

    /**
     * Результат загрузки недостоющих забегов в Firestore
     *
     * @param missingList [List] - список забегов
     *
     * @return [Completable] - асинхронный результат загрузки забегов
     */
    fun uploadMissingFromDbToCloud(missingList: List<RunEntity>): Completable

    /**
     * Результат загрузки изображения в Firebase Storage
     *
     * @param run [RunEntity] - объект забега
     *
     * @return [Single] - асинхронный результат загрузки изображения
     */
    fun uploadImageToStorage(run: RunEntity): Single<UploadTask>

    /**
     * Результат загрузки изображения из Firebase Storage
     *
     * @param run [RunEntity] - объект забега
     *
     * @return [Single] - асинхронный результат загрузки изображения
     */
    fun downloadImageFromStorage(run: RunEntity): Single<Task<ByteArray>>
}
