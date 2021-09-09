package xyz.fcr.sberrunner.domain.interactor.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import xyz.fcr.sberrunner.data.repository.firebase.IFirebaseRepository
import xyz.fcr.sberrunner.data.repository.firestorage.IStorageRepository
import xyz.fcr.sberrunner.data.repository.firestore.IFirestoreRepository
import xyz.fcr.sberrunner.domain.converter.RunConverter
import xyz.fcr.sberrunner.presentation.model.Run

/**
 * Имплементация интерфейса [IFirebaseInteractor],
 * служит для связи Firebase (auth, firestore, storage) <-> ViewModel
 *
 * @param firebase [IFirebaseRepository] - репозиторий взааимодействия с Firebase
 * @param firestore [IFirestoreRepository] - репозиторий взааимодействия с Firestore
 * @param storage [IStorageRepository] - репозиторий взааимодействия с Firebase Storage
 */
class FirebaseInteractor(
    private val firebase: IFirebaseRepository,
    private val firestore: IFirestoreRepository,
    private val storage: IStorageRepository,
    private val converter: RunConverter
) : IFirebaseInteractor {

    /**
     * Авторизация в Firebase
     *
     * @param email [String] - почта пользователя
     * @param password [String] - пароль пользователя
     *
     * @return [Single] - асинхронный результат авторизации
     */
    override fun login(email: String, password: String): Single<Task<AuthResult>> {
        return Single.fromCallable {
            firebase.login(email, password)
        }
    }

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
    override fun registration(
        name: String,
        email: String,
        pass: String,
        weight: String
    ): Single<Task<AuthResult>> {
        return Single.fromCallable {
            firebase.registration(name, email, pass, weight)
        }
    }

    /**
     * Отправка сообщения на восстановление почты в Firebase
     *
     * @param email [String] - email пользователя
     *
     * @return [Single] - асинхронный результат отправки сообщения
     */
    override fun sendResetEmail(email: String): Single<Task<Void>> {
        return Single.fromCallable {
            firebase.sendResetEmail(email)
        }
    }

    /**
     * Отправка запроса на выход пользователя из приложения в Firebase
     *
     * @return [Completable] - асинхронный результат отправки запроса
     */
    override fun signOut(): Completable {
        return Completable.fromCallable {
            firebase.signOut()
        }
    }

    /**
     * Удаление аккаунта текущего пользователя из Firebase
     *
     * @return [Single] - асинхронный результат удаления
     */
    override fun deleteAccount(): Single<Task<Void>> {
        return Single.fromCallable {
            firebase.deleteAccount()
        }
    }

    /**
     * Получение документа из Firestore
     *
     * @return [Single] - асинхронный результат получения документа
     */
    override fun getDocumentFirestore(): Single<Task<DocumentSnapshot>> {
        return Single.fromCallable {
            firestore.getDocumentFirestore()
        }
    }

    /**
     * Обновление веса пользователя в Firestore
     *
     * @param weight [String] - вес пользователя
     *
     * @return [Single] - асинхронный результат обновления веса
     */
    override fun updateWeight(weight: String): Single<Task<Void>> {
        return Single.fromCallable {
            firestore.updateWeight(weight)
        }
    }

    /**
     * Обновление имени пользователя в Firestore
     *
     * @param name [String] - имя пользователя
     *
     * @return [Single] - асинхронный результат обновления имени
     */
    override fun updateName(name: String): Single<Task<Void>> {
        return Single.fromCallable {
            firestore.updateName(name)
        }
    }

    /**
     * Получение запроса на список всех забегов из Firestore
     *
     * @return [Single] - асинхронный результат получения списка
     */
    override fun getAllRunsFromCloud(): Single<Task<QuerySnapshot>> {
        return Single.fromCallable {
            firestore.getAllRuns()
        }
    }

    /**
     * Изначальное занесение имени и веса пользователя в Firestore
     *
     * @param name [String] - имя пользователя
     * @param weight [String] - вес пользователя
     *
     * @return [Single] - асинхронный результат занесения данных
     */
    override fun fillUserDataInFirestore(name: String, weight: String): Single<Task<Void>> {
        return Single.fromCallable {
            firestore.fillUserDataInFirestore(name, weight)
        }
    }

    /**
     * Результат изменения флагов на удаление в Firestore
     *
     * @param listToSwitch [List] - список забегов
     *
     * @return [Single] - асинхронный результат изменения флагов
     */
    override fun switchToDeleteFlagsInCloud(listToSwitch: List<Run>): Single<Task<Void>> {
        return Single.fromCallable {
            firestore.switchToDeleteFlags(
                converter.toRunEntityList(listToSwitch)
            )
        }
    }

    /**
     * Результат загрузки недостоющих забегов в Firestore
     *
     * @param missingList [List] - список забегов
     *
     * @return [Single] - асинхронный результат загрузки забегов
     */
    override fun uploadMissingFromDbToCloud(missingList: List<Run>): Single<Task<Void>> {
        return Single.fromCallable {
            firestore.addRunsToCloud(
                converter.toRunEntityList(missingList)
            )
        }
    }

    /**
     * Результат загрузки изображения в Firebase Storage
     *
     * @param run [Run] - объект забега
     *
     * @return [Single] - асинхронный результат загрузки изображения
     */
    override fun uploadImageToStorage(run: Run): Single<UploadTask> {
        return Single.fromCallable {
            storage.addImage(
                converter.toRunEntity(run)
            )
        }
    }

    /**
     * Результат загрузки изображения из Firebase Storage
     *
     * @param run [Run] - объект забега
     *
     * @return [Single] - асинхронный результат загрузки изображения
     */
    override fun downloadImageFromStorage(run: Run): Single<Task<ByteArray>> {
        return Single.fromCallable {
            storage.getImage(
                converter.toRunEntity(run)
            )
        }
    }
}