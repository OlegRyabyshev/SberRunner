package xyz.fcr.sberrunner.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import xyz.fcr.sberrunner.data.repository.firebase.FirebaseRepository
import xyz.fcr.sberrunner.data.repository.firebase.IFirebaseRepository
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
    fun providesFirebaseRepository(auth: FirebaseAuth, store: FirebaseFirestore): IFirebaseRepository {
        return FirebaseRepository(auth, store)
    }

//    @Provides
//    @Singleton
//    fun providesFirestoreRepository(runDao: RunDao, store: FirebaseFirestore): IFirestoreRepository {
//        return FirestoreRepository(store, runDao)
//    }
}