package xyz.fcr.sberrunner.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
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

    fun login(email: String, password: String): Task<AuthResult> {

        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    fun sendResetEmail(email: String): Task<Void> {
        return firebaseAuth.sendPasswordResetEmail(email)
    }

    private fun fillUserDataInFirestore(name: String, weight: String) {
        val user = hashMapOf("name" to name, "weight" to weight)
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            val documentReference = fireStore.collection("user").document(userId)
            documentReference.set(user)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun deleteAccount(): Task<Void>? {
        val user = firebaseAuth.currentUser
        return user?.delete()
    }
}