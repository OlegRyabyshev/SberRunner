package xyz.fcr.sberrunner.domain.interactor.cloud

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import xyz.fcr.sberrunner.domain.model.Run

/**
 * Интерфейс взааимодействия с Облаком
 */
interface ICloudInteractor {

    /**
     * Авторизация
     *
     * @param email [String] - почта пользователя
     * @param password [String] - пароль пользователя
     *
     * @return [Single] - асинхронный результат авторизации
     */
    fun login(email: String, password: String): Single<Task<AuthResult>>

    /**
     * Регистрация
     *
     * @param email [String] - email пользователя
     * @param pass [String] - пароль пользователя
     *
     * @return [Single] - асинхронный результат регистрации
     */
    fun registration(email: String, pass: String): Single<Task<AuthResult>>

    /**
     * Отправка сообщения на восстановление почты
     *
     * @param email [String] - email пользователя
     *
     * @return [Single] - асинхронный результат отправки сообщения
     */
    fun sendResetEmail(email: String): Single<Task<Void>>

    /**
     * Отправка запроса на выход пользователя из приложения
     *
     * @return [Completable] - асинхронный результат отправки запроса
     */
    fun signOut(): Completable

    /**
     * Удаление аккаунта текущего пользователя из Firebase
     *
     * @return [Single] - асинхронный результат удаления
     */
    fun deleteAccount(): Single<Task<Void>>

    /**
     * Получение документа
     *
     * @return [Single] - асинхронный результат получения документа
     */
    fun getDocumentFirestore(): Single<Task<DocumentSnapshot>>

    /**
     * Обновление веса пользователя
     *
     * @param weight [String] - вес пользователя
     *
     * @return [Single] - асинхронный результат обновления веса
     */
    fun updateWeight(weight: String): Single<Task<Void>>

    /**
     * Обновление имени пользователя
     *
     * @param name [String] - имя пользователя
     *
     * @return [Single] - асинхронный результат обновления имени
     */
    fun updateName(name: String): Single<Task<Void>>

    /**
     * Получение запроса на список всех забегов
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
     * @return [Single] - асинхронный результат занесения данных
     */
    fun fillUserDataInFirestore(name: String, weight: String): Single<Task<Void>>

    /**
     * Результат изменения флагов на удаление
     *
     * @param listToSwitch [List] - список забегов
     *
     * @return [Single] - асинхронный результат изменения флагов
     */
    fun switchToDeleteFlagsInCloud(listToSwitch: List<Run>): Single<Task<Void>>

    /**
     * Результат загрузки недостоющих забегов
     *
     * @param missingList [List] - список забегов
     *
     * @return [Single] - асинхронный результат загрузки забегов
     */
    fun uploadMissingFromDbToCloud(missingList: List<Run>): Single<Task<Void>>

    /**
     * Результат загрузки изображения
     *
     * @param run [Run] - объект забега
     *
     * @return [Single] - асинхронный результат загрузки изображения
     */
    fun uploadImageToStorage(run: Run): Single<UploadTask>

    /**
     * Результат загрузки изображения
     *
     * @param run [Run] - объект забега
     *
     * @return [Single] - асинхронный результат загрузки изображения
     */
    fun downloadImageFromStorage(run: Run): Single<Task<ByteArray>>
}
