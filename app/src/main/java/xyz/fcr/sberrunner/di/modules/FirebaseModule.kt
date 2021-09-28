package xyz.fcr.sberrunner.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import xyz.fcr.sberrunner.data.datastore.firebase.FirebaseDataStore
import xyz.fcr.sberrunner.data.datastore.firebase.AuthDataStoreInterface
import xyz.fcr.sberrunner.data.datastore.firestorage.ImageDataStoreInterface
import xyz.fcr.sberrunner.data.datastore.firestorage.StorageDataStore
import xyz.fcr.sberrunner.data.datastore.firestore.FirestoreDataStore
import xyz.fcr.sberrunner.data.datastore.firestore.StoreDataStoreInterface
import xyz.fcr.sberrunner.data.util.BitmapConverter
import xyz.fcr.sberrunner.data.converter.RunConverter
import xyz.fcr.sberrunner.domain.interactor.cloud.CloudInteractor
import xyz.fcr.sberrunner.domain.interactor.cloud.ICloudInteractor
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
     * Предоставление взаимодействия с Firebase
     *
     * @param auth [FirebaseAuth] - класс взаимодействия с аутентификацией
     *
     * @return [AuthDataStoreInterface] - интерфейс взаимодействия с Firebase
     */
    @Provides
    @Singleton
    fun providesFirebaseDataStore(
        auth: FirebaseAuth
    ): AuthDataStoreInterface {
        return FirebaseDataStore(auth)
    }

    /**
     * Предоставление взаимодействия с Firestore
     *
     * @param auth [FirebaseAuth] - объект взаимодействия с аутентификацией
     * @param store [FirebaseFirestore] - объект взаимодействия с Firestore
     *
     * @return [StoreDataStoreInterface] - интерфейс взаимодействия с Firestore
     */
    @Provides
    @Singleton
    fun providesFirestoreDataStore(
        auth: FirebaseAuth,
        store: FirebaseFirestore
    ): StoreDataStoreInterface {
        return FirestoreDataStore(auth, store)
    }

    /**
     * Предоставление взаимодействия с Firebase Storage
     *
     * @param auth [FirebaseAuth] - объект взаимодействия с аутентификацией
     * @param storage [FirebaseStorage] - объект взаимодействия с Firebase Storage
     * @param converter [BitmapConverter] - объект конвертора Bitmap <-> ByteArray
     *
     * @return [ImageDataStoreInterface] - интерфейс взаимодействия с Firebase Storage
     */
    @Provides
    @Singleton
    fun providesStorageDataStore(
        auth: FirebaseAuth,
        storage: FirebaseStorage,
        converter: BitmapConverter
    ): ImageDataStoreInterface {
        return StorageDataStore(auth, storage, converter)
    }

    /**
     * Предоставление интерактора взаимодействия с Firebase (auth/firestore/storage)
     *
     * @param firebaseDataStore [AuthDataStoreInterface] - объект взаимодействия с Firebase
     * @param storeDataStore [StoreDataStoreInterface] - объект взаимодействия с Firestore
     * @param storageDataStore [ImageDataStoreInterface] - объект взаимодействия с Firebase Storage
     * @param converter [RunConverter] - конвертер забегов
     *
     * @return [ICloudInteractor] - интерактор взаимодействия с Firebase
     */
    @Provides
    @Singleton
    fun providesFirebaseInteractor(
        firebaseDataStore: AuthDataStoreInterface,
        storeDataStore: StoreDataStoreInterface,
        storageDataStore: ImageDataStoreInterface,
        converter: RunConverter
    ): ICloudInteractor {
        return CloudInteractor(
            firebaseDataStore,
            storeDataStore,
            storageDataStore,
            converter
        )
    }
}