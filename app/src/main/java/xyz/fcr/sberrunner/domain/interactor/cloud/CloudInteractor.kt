package xyz.fcr.sberrunner.domain.interactor.cloud

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import xyz.fcr.sberrunner.data.datastore.firebase.AuthDataStoreInterface
import xyz.fcr.sberrunner.data.datastore.firestorage.ImageDataStoreInterface
import xyz.fcr.sberrunner.data.datastore.firestore.StoreDataStoreInterface
import xyz.fcr.sberrunner.data.converter.RunConverter
import xyz.fcr.sberrunner.domain.model.Run

/**
 * Имплементация интерфейса [ICloudInteractor]
 *
 * @param authDataStore [AuthDataStoreInterface] - объект взааимодействия с Firebase
 * @param storeDataStore [StoreDataStoreInterface] - объект взааимодействия с Firestore
 * @param storageDataStore [ImageDataStoreInterface] - объект взааимодействия с Firebase Storage
 */
class CloudInteractor(
    private val authDataStore: AuthDataStoreInterface,
    private val storeDataStore: StoreDataStoreInterface,
    private val storageDataStore: ImageDataStoreInterface,
    private val converter: RunConverter
) : ICloudInteractor {

    /**
     * Авторизация
     *
     * @param email [String] - почта пользователя
     * @param password [String] - пароль пользователя
     *
     * @return [Single] - асинхронный результат авторизации
     */
    override fun login(email: String, password: String): Single<Task<AuthResult>> {
        return Single.fromCallable {
            authDataStore.login(email, password)
        }
    }

    /**
     * Регистрация
     *
     * @param email [String] - email пользователя
     * @param pass [String] - пароль пользователя
     *
     * @return [Single] - асинхронный результат регистрации
     */
    override fun registration(
        email: String,
        pass: String
    ): Single<Task<AuthResult>> {
        return Single.fromCallable {
            authDataStore.registration(email, pass)
        }
    }

    /**
     * Отправка сообщения на восстановление почты
     *
     * @param email [String] - email пользователя
     *
     * @return [Single] - асинхронный результат отправки сообщения
     */
    override fun sendResetEmail(email: String): Single<Task<Void>> {
        return Single.fromCallable {
            authDataStore.sendResetEmail(email)
        }
    }

    /**
     * Отправка запроса на выход пользователя из приложения
     *
     * @return [Completable] - асинхронный результат отправки запроса
     */
    override fun signOut(): Completable {
        return Completable.fromCallable {
            authDataStore.signOut()
        }
    }

    /**
     * Удаление аккаунта текущего пользователя
     *
     * @return [Single] - асинхронный результат удаления
     */
    override fun deleteAccount(): Single<Task<Void>> {
        return Single.fromCallable {
            authDataStore.deleteAccount()
        }
    }

    /**
     * Получение документа
     *
     * @return [Single] - асинхронный результат получения документа
     */
    override fun getDocumentFirestore(): Single<Task<DocumentSnapshot>> {
        return Single.fromCallable {
            storeDataStore.getDocumentFirestore()
        }
    }

    /**
     * Обновление веса пользователя
     *
     * @param weight [String] - вес пользователя
     *
     * @return [Single] - асинхронный результат обновления веса
     */
    override fun updateWeight(weight: String): Single<Task<Void>> {
        return Single.fromCallable {
            storeDataStore.updateWeight(weight)
        }
    }

    /**
     * Обновление имени пользователя
     *
     * @param name [String] - имя пользователя
     *
     * @return [Single] - асинхронный результат обновления имени
     */
    override fun updateName(name: String): Single<Task<Void>> {
        return Single.fromCallable {
            storeDataStore.updateName(name)
        }
    }

    /**
     * Получение запроса на список всех забегов
     *
     * @return [Single] - асинхронный результат получения списка
     */
    override fun getAllRunsFromCloud(): Single<Task<QuerySnapshot>> {
        return Single.fromCallable {
            storeDataStore.getAllRuns()
        }
    }

    /**
     * Изначальное занесение имени и веса пользователя
     *
     * @param name [String] - имя пользователя
     * @param weight [String] - вес пользователя
     *
     * @return [Single] - асинхронный результат занесения данных
     */
    override fun fillUserDataInFirestore(name: String, weight: String): Single<Task<Void>> {
        return Single.fromCallable {
            storeDataStore.fillUserDataInFirestore(name, weight)
        }
    }

    /**
     * Результат изменения флагов на удаление
     *
     * @param listToSwitch [List] - список забегов
     *
     * @return [Single] - асинхронный результат изменения флагов
     */
    override fun switchToDeleteFlagsInCloud(listToSwitch: List<Run>): Single<Task<Void>> {
        return Single.fromCallable {
            storeDataStore.switchToDeleteFlags(
                converter.toRunEntityList(listToSwitch)
            )
        }
    }

    /**
     * Результат загрузки недостоющих забегов
     *
     * @param missingList [List] - список забегов
     *
     * @return [Single] - асинхронный результат загрузки забегов
     */
    override fun uploadMissingFromDbToCloud(missingList: List<Run>): Single<Task<Void>> {
        return Single.fromCallable {
            storeDataStore.addRunsToCloud(
                converter.toRunEntityList(missingList)
            )
        }
    }

    /**
     * Результат загрузки изображения
     *
     * @param run [Run] - объект забега
     *
     * @return [Single] - асинхронный результат загрузки изображения
     */
    override fun uploadImageToStorage(run: Run): Single<UploadTask> {
        return Single.fromCallable {
            storageDataStore.addImage(
                converter.toRunEntity(run)
            )
        }
    }

    /**
     * Результат загрузки изображения
     *
     * @param run [Run] - объект забега
     *
     * @return [Single] - асинхронный результат загрузки изображения
     */
    override fun downloadImageFromStorage(run: Run): Single<Task<ByteArray>> {
        return Single.fromCallable {
            storageDataStore.getImage(
                converter.toRunEntity(run)
            )
        }
    }
}