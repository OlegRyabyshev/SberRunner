package xyz.fcr.sberrunner.view.fragments.firebase_fragments

import android.content.Context
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.FragmentRegistrationBinding
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty
import xyz.fcr.sberrunner.view.activities.MainActivity
import xyz.fcr.sberrunner.viewmodel.firebase_viewmodels.RegistrationViewModel

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val fireAuth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()

    private lateinit var viewModel: RegistrationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return DetailedViewModel(weatherRepository, schedulersProvider) as T
            }
        }).get(DetailedViewModel::class.java)

        binding.signUpButton.setOnClickListener {
//            checkFieldsForRegister()
        }

        initSignInLink()
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