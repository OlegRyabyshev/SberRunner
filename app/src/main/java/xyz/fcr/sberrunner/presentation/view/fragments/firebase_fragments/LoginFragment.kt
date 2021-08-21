package xyz.fcr.sberrunner.presentation.view.fragments.firebase_fragments

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import es.dmoral.toasty.Toasty
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.FragmentLoginBinding
import xyz.fcr.sberrunner.utils.Constants.VALID
import xyz.fcr.sberrunner.presentation.view.activities.MainActivity
import xyz.fcr.sberrunner.presentation.viewmodels.firebase_viewmodels.LoginViewModel
import javax.inject.Inject

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    val viewModel: LoginViewModel by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener {
            viewModel.initSignIn(
                binding.signInEmail.text.toString(),
                binding.signInPassword.text.toString(),
            )
        }

        binding.resetPassword.setOnClickListener {
            viewModel.initResetEmail(binding.signInEmail.text.toString())
        }

        observeLiveData()
        initSignUpLink()
    }

    private fun observeLiveData() {
        viewModel.progressLiveData.observe(viewLifecycleOwner, { isVisible: Boolean -> showProgress(isVisible) })
        viewModel.loginLiveData.observe(viewLifecycleOwner, { isSucceed: Boolean -> startMainActivity(isSucceed) })
        viewModel.resetLiveData.observe(viewLifecycleOwner, { result: Boolean -> showResetToast(result) })
        viewModel.errorFirebase.observe(viewLifecycleOwner, { string: String -> showError(string) })

        viewModel.errorEmail.observe(viewLifecycleOwner, { error: String -> setError(error, binding.signInEmailTv) })
        viewModel.errorPass.observe(viewLifecycleOwner, { error: String -> setError(error, binding.signInPasswordTv) })
    }

    private fun showResetToast(resetSent: Boolean) {
        if (resetSent) {
            Toasty.success(requireContext(), getString(R.string.check_email_toast), Toast.LENGTH_SHORT).show()
        } else {
            Toasty.error(requireContext(), getString(R.string.failed_check_email_toast), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showError(text: String) {
        Toasty.error(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun showProgress(isVisible: Boolean) {
        binding.progressCircularLogin.isVisible = isVisible
    }

    private fun setError(error: String, textInputLayout: TextInputLayout) {
        when (error) {
            VALID -> textInputLayout.isErrorEnabled = false
            else -> {
                textInputLayout.isErrorEnabled = true
                textInputLayout.error = error
            }
        }
    }

    private fun startMainActivity(isSucceed: Boolean) {
        if (isSucceed) {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        } else {
            Toasty.error(requireContext(), getString(R.string.not_matching_email_password), Toast.LENGTH_SHORT).show()
        }
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
}