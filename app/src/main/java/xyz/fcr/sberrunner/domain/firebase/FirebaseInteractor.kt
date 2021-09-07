package xyz.fcr.sberrunner.domain.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.rxjava3.core.Single
import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.data.repository.firebase.IFirebaseRepository
import xyz.fcr.sberrunner.data.repository.firestorage.IStorageRepository
import xyz.fcr.sberrunner.data.repository.firestore.IFirestoreRepository

class FirebaseInteractor(
    private val firebase: IFirebaseRepository,
    private val firestore: IFirestoreRepository,
    private val storage: IStorageRepository
) : IFirebaseInteractor {

    override fun login(email: String, password: String): Single<Task<AuthResult>> {
        return Single.fromCallable { firebase.login(email, password) }
    }

    override fun registration(name: String, email: String, pass: String, weight: String): Single<Task<AuthResult>> {
        return Single.fromCallable { firebase.registration(name, email, pass, weight) }
    }

    override fun sendResetEmail(email: String): Single<Task<Void>> {
        return Single.fromCallable { firebase.sendResetEmail(email) }
    }

    override fun signOut(): Single<Unit> {
        return Single.fromCallable { firebase.signOut() }
    }

    override fun deleteAccount(): Single<Task<Void>> {
        return Single.fromCallable { firebase.deleteAccount() }
    }

    override fun getDocumentFirestore(): Single<Task<DocumentSnapshot>> {
        return Single.fromCallable { firestore.getDocumentFirestore() }
    }

    override fun updateWeight(weight: String): Single<Task<Void>> {
        return Single.fromCallable { firestore.updateWeight(weight) }
    }

    override fun updateName(name: String): Single<Task<Void>> {
        return Single.fromCallable { firestore.updateName(name) }
    }

    override fun getAllRunsFromCloud(): Single<Task<QuerySnapshot>> {
        return Single.fromCallable { firestore.getAllRuns() }
    }

    override fun fillUserDataInFirestore(name: String, weight: String): Single<Task<Void>> {
        return Single.fromCallable { firestore.fillUserDataInFirestore(name, weight) }
    }

    override fun switchToDeleteFlagsInCloud(listToSwitch: List<RunEntity>): Single<Task<Void>> {
        return Single.fromCallable { firestore.switchToDeleteFlags(listToSwitch) }
    }

    override fun uploadMissingFromDbToCloud(missingList: List<RunEntity>): Single<Task<Void>> {
        return Single.fromCallable { firestore.addRunsToCloud(missingList) }
    }
}