package xyz.fcr.sberrunner.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import xyz.fcr.sberrunner.data.repository.firebase.FirebaseRepository
import xyz.fcr.sberrunner.data.repository.firebase.AuthRepositoryInterface
import xyz.fcr.sberrunner.data.repository.firestorage.ImageRepositoryInterface
import xyz.fcr.sberrunner.data.repository.firestorage.StorageRepository
import xyz.fcr.sberrunner.data.repository.firestore.FirestoreRepository
import xyz.fcr.sberrunner.data.repository.firestore.StoreRepositoryInterface
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
     * @return [AuthRepositoryInterface] - интерфейс взаимодействия с Firebase
     */
    @Provides
    @Singleton
    fun providesFirebaseRepository(
        auth: FirebaseAuth
    ): AuthRepositoryInterface {
        return FirebaseRepository(auth)
    }

    /**
     * Предоставление репозитория взаимодействия с Firestore
     *
     * @param auth [FirebaseAuth] - объект взаимодействия с аутентификацией
     * @param store [FirebaseFirestore] - объект взаимодействия с Firestore
     *
     * @return [StoreRepositoryInterface] - интерфейс взаимодействия с Firestore
     */
    @Provides
    @Singleton
    fun providesFirestoreRepository(
        auth: FirebaseAuth,
        store: FirebaseFirestore
    ): StoreRepositoryInterface {
        return FirestoreRepository(auth, store)
    }

    /**
     * Предоставление репозитория взаимодействия с Firebase Storage
     *
     * @param auth [FirebaseAuth] - объект взаимодействия с аутентификацией
     * @param storage [FirebaseStorage] - объект взаимодействия с Firebase Storage
     *
     * @return [ImageRepositoryInterface] - интерфейс взаимодействия с Firebase Storage
     */
    @Provides
    @Singleton
    fun providesStorageRepository(
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ): ImageRepositoryInterface {
        return StorageRepository(auth, storage)
    }

    /**
     * Предоставление интерактора взаимодействия с Firebase (auth/firestore/storage)
     *
     * @param firebaseRepo [AuthRepositoryInterface] - репозиторий взаимодействия с Firebase
     * @param storeRepo [StoreRepositoryInterface] - репозиторий взаимодействия с Firestore
     * @param storage [ImageRepositoryInterface] - репозиторий взаимодействия с Firebase Storage
     * @param converter [RunConverter] - конвертер забегов
     *
     * @return [IFirebaseInteractor] - интерактора взаимодействия с Firebase
     */
    @Provides
    @Singleton
    fun providesFirebaseInteractor(
        firebaseRepo: AuthRepositoryInterface,
        storeRepo: StoreRepositoryInterface,
        storage: ImageRepositoryInterface,
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