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
import xyz.fcr.sberrunner.domain.firebase.FirebaseInteractor
import xyz.fcr.sberrunner.domain.firebase.IFirebaseInteractor
import javax.inject.Singleton

@Module
class FirebaseModule {

    @Provides
    @Singleton
    fun providesFirebaseAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirebaseRepository(
        auth: FirebaseAuth
    ): IFirebaseRepository {
        return FirebaseRepository(auth)
    }

    @Provides
    @Singleton
    fun providesFirestoreRepository(
        auth: FirebaseAuth,
        store: FirebaseFirestore
    ): IFirestoreRepository {
        return FirestoreRepository(auth, store)
    }

    @Provides
    @Singleton
    fun providesStorageRepository(
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ): IStorageRepository {
        return StorageRepository(auth, storage)
    }

    @Provides
    @Singleton
    fun providesFirebaseInteractor(
        baseRepo: IFirebaseRepository,
        storeRepo: IFirestoreRepository,
        storage: IStorageRepository
    ): IFirebaseInteractor {
        return FirebaseInteractor(baseRepo, storeRepo, storage)
    }
}