package xyz.fcr.sberrunner.domain.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot
import io.reactivex.rxjava3.core.Single

interface IFirebaseInteractor {

    fun login(email: String, password: String): Single<Task<AuthResult>>
    fun registration(name: String, email: String, pass: String, weight: String): Single<Task<AuthResult>>
    fun sendResetEmail(email: String): Single<Task<Void>>

    fun signOut(): Single<Unit>
    fun deleteAccount(): Single<Task<Void>>

    fun getDocumentFirestore(): Single<Task<DocumentSnapshot>>
    fun updateWeight(weight: String): Single<Task<Void>>
    fun updateName(name: String): Single<Task<Void>>
}
