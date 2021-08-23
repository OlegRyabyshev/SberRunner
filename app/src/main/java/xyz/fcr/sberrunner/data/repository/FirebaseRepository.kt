package xyz.fcr.sberrunner.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

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
        val user = hashMapOf("name" to name, "weight" to weight)
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            val document = fireStore.collection("user").document(userId)
            document.set(user)
        }
    }

    fun getDocumentFirestore(): Task<DocumentSnapshot>? {
        val userId = firebaseAuth.currentUser?.uid
        var document: Task<DocumentSnapshot>? = null

        if (userId != null) {
            document = fireStore.collection("user").document(userId).get()
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

    fun deleteAccount(): Task<Void>? {
        val user = firebaseAuth.currentUser
        return user?.delete()
    }

    fun updateName(newName: String): Task<Void>? {
        val userId = firebaseAuth.currentUser?.uid
        var task : Task<Void>? = null

        if (userId != null) {
            val document = fireStore.collection("user").document(userId)
            task = document.update("name", newName)
        }

        return task
    }

    fun updateWeight(newWeight: String): Task<Void>? {
        val userId = firebaseAuth.currentUser?.uid
        var task : Task<Void>? = null

        if (userId != null) {
            val document = fireStore.collection("user").document(userId)
            task = document.update("weight", newWeight)
        }

        return task
    }
}