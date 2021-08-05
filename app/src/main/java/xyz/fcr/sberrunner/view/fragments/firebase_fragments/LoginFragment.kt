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
import es.dmoral.toasty.Toasty
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.FragmentLoginBinding
import xyz.fcr.sberrunner.view.activities.MainActivity

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signInButton.setOnClickListener {
            checkFieldsForLogin()
        }

        binding.forgotPassword.setOnClickListener {
            sendResetEmail()
        }

        initSignUpLink()
    }

    private fun checkFieldsForLogin() {
        var amountOfErrors = 0

        val email = binding.signInEmail.text.toString().trim { it <= ' ' }
        val password = binding.signInPassword.text.toString().trim { it <= ' ' }

        if (email.isBlank()) {
            binding.signInEmail.error = "Email can't be empty"
            amountOfErrors++
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signInEmailTv.error = "Wrong email format"
            amountOfErrors++
        }

        if (password.isBlank()) {
            binding.signInPassword.error = "Password can't be empty"
            amountOfErrors++
        } else if (password.length < 6) {
            binding.signInPasswordTv.error = "Password should be at least 6 charters"
            amountOfErrors++
        }

        if (amountOfErrors > 0) return

        signIn(email, password)
    }

    private fun signIn(email: String, password: String) {
        binding.progressCircularLogin.visibility = View.VISIBLE

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressCircularLogin.visibility = View.INVISIBLE

                if (task.isSuccessful) {
                    startMainActivity()
                } else {
                    Toast.makeText(context, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun startMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun initSignUpLink() {
        val fullString = getString(R.string.new_to_sberrunner)
        val partToClick = getString(R.string.part_to_click_login)

        val startIndex = fullString.indexOf(partToClick)
        val endIndex = startIndex + partToClick.length

        val spannableString = SpannableString(fullString)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val manager = activity?.supportFragmentManager
                manager
                    ?.beginTransaction()
                    ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    ?.replace(R.id.welcome_container, RegistrationFragment())
                    ?.addToBackStack("")
                    ?.commit()
            }

            override fun updateDrawState(textPaint: TextPaint) {
                super.updateDrawState(textPaint)
                textPaint.color = ContextCompat.getColor(requireContext(), R.color.main_green)
                textPaint.isFakeBoldText = true
            }
        }

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.signUpTextView.text = spannableString
        binding.signUpTextView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun sendResetEmail() {
        val email = binding.signInEmail.text.toString().trim { it <= ' ' }

        if (email.isBlank()) {
            binding.signInEmail.error = "Email can't be empty"
            return
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signInEmailTv.error = "Wrong email format"
            return
        }

        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener {

            Toasty.success(
                requireContext(),
                "Check your email for a password reset",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener {
            Toasty.error(
                requireContext(),
                "Can't send an email. Check your connection or email",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}