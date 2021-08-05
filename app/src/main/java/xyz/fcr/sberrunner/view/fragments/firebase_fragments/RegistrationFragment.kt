package xyz.fcr.sberrunner.view.fragments.firebase_fragments

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.FragmentRegistrationBinding
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty
import xyz.fcr.sberrunner.view.activities.MainActivity

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val fireAuth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpButton.setOnClickListener {
            checkFieldsForRegister()
        }

        initSignInLink()
    }

    private fun checkFieldsForRegister() {
        var amountOfErrors = 0

        val name = binding.registerName.text.toString().trim { it <= ' ' }
        val email = binding.registerEmail.text.toString().trim { it <= ' ' }
        val password = binding.registerPassword.text.toString().trim { it <= ' ' }
        val height = binding.registerHeight.text.toString().toIntOrNull()
        val weight = binding.registerWeight.text.toString().toIntOrNull()

        //Checking Email
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

        //Checking Height
        when {
            height == null || height > 250 || height <= 40 -> {
                binding.registerHeightTv.isErrorEnabled = true
                binding.registerHeightTv.error = "Height is not valid"
                amountOfErrors++
            }
            else -> {
                binding.registerHeightTv.isErrorEnabled = false
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

    private fun startMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }


    private fun initSignInLink() {
        val fullString = getString(R.string.already_have_an_account)
        val partToClick = getString(R.string.part_to_click_registration)

        val startIndex = fullString.indexOf(partToClick)
        val endIndex = startIndex + partToClick.length

        val spannableString = SpannableString(fullString)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val manager = activity?.supportFragmentManager
                manager
                    ?.beginTransaction()
                    ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    ?.replace(R.id.welcome_container, LoginFragment())
                    ?.commit()
            }

            override fun updateDrawState(textPaint: TextPaint) {
                super.updateDrawState(textPaint)
                textPaint.color = ContextCompat.getColor(requireContext(), R.color.main_green)
                textPaint.isFakeBoldText = true
            }
        }

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.signInTv.text = spannableString
        binding.signInTv.movementMethod = LinkMovementMethod.getInstance()
    }
}