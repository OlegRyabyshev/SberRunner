package xyz.fcr.sberrunner.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import xyz.fcr.sberrunner.data.repository.firebase.FirebaseRepository
import xyz.fcr.sberrunner.data.repository.firebase.IFirebaseRepository
import xyz.fcr.sberrunner.data.repository.firestorage.IStorageRepository
import xyz.fcr.sberrunner.data.repository.firestorage.StorageRepository
import xyz.fcr.sberrunner.data.repository.firestore.FirestoreRepository
import xyz.fcr.sberrunner.data.repository.firestore.IFirestoreRepository
import xyz.fcr.sberrunner.domain.converter.RunConverter
import xyz.fcr.sberrunner.domain.interactor.firebase.FirebaseInteractor
import xyz.fcr.sberrunner.domain.interactor.firebase.IFirebaseInteractor
import javax.inject.Singleton

/**
 * Модуль приложения, предоставляющий зависимости Firebase
 */
@Module
class FirebaseModule {

    /**
     * Предоставление зависимости класса аутентификации
     *
     * @return [FirebaseAuth] - класс взаимодействия с аутентификацией
     */
    @Provides
    @Singleton
    fun providesFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    /**
     * Предоставление зависимости класса Firestore
     *
     * @return [FirebaseFirestore] - объект облачной базы данных
     */
    @Provides
    @Singleton
    fun providesFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    /**
     * Предоставление зависимости класса FirebaseStorage
     *
     * @return [FirebaseStorage] - объект облачного хранилища данных
     */
    @Provides
    @Singleton
    fun providesFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    /**
     * Предоставление репозитория взаимодействия с Firebase
     *
     * @param auth [FirebaseAuth] - класс взаимодействия с аутентификацией
     *
     * @return [IFirebaseRepository] - интерфейс взаимодействия с Firebase
     */
    @Provides
    @Singleton
    fun providesFirebaseRepository(
        auth: FirebaseAuth
    ): IFirebaseRepository {
        return FirebaseRepository(auth)
    }

    /**
     * Предоставление репозитория взаимодействия с Firestore
     *
     * @param auth [FirebaseAuth] - объект взаимодействия с аутентификацией
     * @param store [FirebaseFirestore] - объект взаимодействия с Firestore
     *
     * @return [IFirestoreRepository] - интерфейс взаимодействия с Firestore
     */
    @Provides
    @Singleton
    fun providesFirestoreRepository(
        auth: FirebaseAuth,
        store: FirebaseFirestore
    ): IFirestoreRepository {
        return FirestoreRepository(auth, store)
    }

    /**
     * Предоставление репозитория взаимодействия с Firebase Storage
     *
     * @param auth [FirebaseAuth] - объект взаимодействия с аутентификацией
     * @param storage [FirebaseStorage] - объект взаимодействия с Firebase Storage
     *
     * @return [IStorageRepository] - интерфейс взаимодействия с Firebase Storage
     */
    @Provides
    @Singleton
    fun providesStorageRepository(
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ): IStorageRepository {
        return StorageRepository(auth, storage)
    }

    /**
     * Предоставление интерактора взаимодействия с Firebase (auth/firestore/storage)
     *
     * @param firebaseRepo [IFirebaseRepository] - репозиторий взаимодействия с Firebase
     * @param storeRepo [IFirestoreRepository] - репозиторий взаимодействия с Firestore
     * @param storage [IStorageRepository] - репозиторий взаимодействия с Firebase Storage
     * @param converter [RunConverter] - конвертер забегов
     *
     * @return [IFirebaseInteractor] - интерактора взаимодействия с Firebase
     */
    @Provides
    @Singleton
    fun providesFirebaseInteractor(
        firebaseRepo: IFirebaseRepository,
        storeRepo: IFirestoreRepository,
        storage: IStorageRepository,
        converter: RunConverter
    ): IFirebaseInteractor {
        return FirebaseInteractor(
            firebaseRepo,
            storeRepo,
            storage,
            converter
        )
    }
}