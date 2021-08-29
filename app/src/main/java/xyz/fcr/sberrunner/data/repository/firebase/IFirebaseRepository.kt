package xyz.fcr.sberrunner.data.repository.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.DocumentSnapshot


interface IFirebaseRepository {

    fun registration(name: String, email: String, password: String, weight: String): Task<AuthResult>

    fun getDocumentFirestore(): Task<DocumentSnapshot>?

    fun login(email: String, password: String): Task<AuthResult>

    fun sendResetEmail(email: String): Task<Void>

    fun signOut()

    fun deleteAccount(): Task<Void>

    fun updateName(newName: String): Task<Void>

    fun updateWeight(newWeight: String): Task<Void>

}