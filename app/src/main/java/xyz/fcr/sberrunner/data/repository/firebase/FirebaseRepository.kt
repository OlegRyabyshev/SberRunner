package xyz.fcr.sberrunner.data.repository.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import xyz.fcr.sberrunner.utils.Constants.NAME
import xyz.fcr.sberrunner.utils.Constants.USER
import xyz.fcr.sberrunner.utils.Constants.WEIGHT

class FirebaseRepository(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) {
    fun registration(name: String, email: String, password: String, weight: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fillUserDataInFirestore(name, weight)
                }
            }
    }

    private fun fillUserDataInFirestore(name: String, weight: String) {
        val user = hashMapOf(NAME to name, WEIGHT to weight)
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            val document = fireStore.collection(USER).document(userId)
            document.set(user)
        }
    }

    fun getDocumentFirestore(): Task<DocumentSnapshot>? {
        val userId = firebaseAuth.currentUser?.uid
        var document: Task<DocumentSnapshot>? = null

        if (userId != null) {
            document = fireStore.collection(USER).document(userId).get()
        }

        return document
    }

    fun login(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    fun sendResetEmail(email: String): Task<Void> {
        return firebaseAuth.sendPasswordResetEmail(email)
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun deleteAccount(): Task<Void> {
        val user = firebaseAuth.currentUser
        return user!!.delete()
    }

    fun updateName(newName: String): Task<Void> {
        val userId = firebaseAuth.currentUser?.uid
        val document = fireStore.collection(USER).document(userId!!)
        return document.update(NAME, newName)
    }

    fun updateWeight(newWeight: String): Task<Void> {
        val userId = firebaseAuth.currentUser?.uid
        val document = fireStore.collection(USER).document(userId!!)
        return document.update(WEIGHT, newWeight)
    }
}