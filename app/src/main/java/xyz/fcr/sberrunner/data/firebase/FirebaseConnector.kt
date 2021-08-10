package xyz.fcr.sberrunner.data.firebase

import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty

class FirebaseConnector(firebaseAuth: FirebaseAuth, fireStore: FirebaseFirestore) {

    fun firebaseRegistration(regName:String, regEmail: String, regPassword: String, regWeight: String) {

        val name = regName.trim { it <= ' ' }
        val email = regEmail.trim { it <= ' ' }
        val password = regPassword.trim { it <= ' ' }
        val weight = regWeight.toIntOrNull()

        //Checking Email
        var amountOfErrors = 0

        when {
            email.isBlank() -> {
                binding.registerEmailTv.isErrorEnabled = true
                binding.registerEmailTv.error = "Email can't be empty"
                amountOfErrors++
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.registerEmailTv.error = "Wrong email format"
                amountOfErrors++
            }
            else -> {
                binding.registerEmailTv.isErrorEnabled = false
            }
        }

        //Checking Passport
        when {
            password.isBlank() -> {
                binding.registerPasswordTv.isErrorEnabled = true
                binding.registerPasswordTv.error = "Password can't be empty"
                amountOfErrors++
            }
            password.length < 6 -> {
                binding.registerPasswordTv.error = "Password should be at least 6 charters"
                amountOfErrors++
            }
            else -> {
                binding.registerPasswordTv.isErrorEnabled = false
            }
        }

        //Checking Name
        when {
            name.isBlank() -> {
                binding.registerNameTv.isErrorEnabled = true
                binding.registerNameTv.error = "Login can't be empty"
                amountOfErrors++
            }
            else -> {
                binding.registerNameTv.isErrorEnabled = false
            }
        }

        //Checking Weight
        when {
            weight == null || weight > 350 || weight <= 0 -> {
                binding.registerWeightTv.isErrorEnabled = true
                binding.registerWeight.error = "Weight is not valid"
                amountOfErrors++
            }
            else -> {
                binding.registerWeightTv.isErrorEnabled = false
            }
        }

        if (amountOfErrors != 0) return
        signUpAndAuth(email, password, name, height!!, weight!!)
    }

    private fun signUpAndAuth(email: String, password: String, name: String, height: Int, weight: Int) {
        binding.progressCircularRegistration.visibility = View.VISIBLE

        fireAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressCircularRegistration.visibility = View.INVISIBLE

                if (task.isSuccessful) {
                    val user = hashMapOf(
                        "name" to name,
                        "height" to height,
                        "weight" to weight
                    )

                    val userId = fireAuth.currentUser?.uid

                    if (userId != null) {
                        val documentReference = fireStore.collection("user").document(userId)

                        documentReference
                            .set(user)
                            .addOnSuccessListener {
                                startMainActivity()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toasty.error(requireContext(), task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }
}