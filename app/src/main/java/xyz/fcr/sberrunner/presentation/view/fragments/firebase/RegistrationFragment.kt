package xyz.fcr.sberrunner.presentation.view.fragments.firebase

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
import xyz.fcr.sberrunner.databinding.FragmentRegistrationBinding
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.utils.Constants.VALID
import xyz.fcr.sberrunner.presentation.view.activities.MainActivity
import xyz.fcr.sberrunner.presentation.viewmodels.firebase.RegistrationViewModel
import javax.inject.Inject

/**
 * Фрагмент регистрации.
 * Пользователь вводит тут необходимые данные для регистрации и при успехе попадает в MainActivity.
 */
class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: RegistrationViewModel by viewModels { factory }
    init {
        App.appComponent.inject(this)
    }

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
            viewModel.initRegistration(
                binding.signUpName.text.toString(),
                binding.signUpEmail.text.toString(),
                binding.signUpPass.text.toString(),
                binding.signUpWeight.text.toString()
            )
        }

        observeLiveData()
        initSignInLink()
    }

    /**
     * Отслеживание изменений в livedata вьюмодели.
     */
    private fun observeLiveData() {
        viewModel.progressLiveData.observe(viewLifecycleOwner, { isVisible: Boolean -> showProgress(isVisible) })
        viewModel.successLiveData.observe(viewLifecycleOwner, { string: String -> startMainActivity(string) })
        viewModel.errorFirebase.observe(viewLifecycleOwner, { string: String -> showError(string) })

        viewModel.errorName.observe(viewLifecycleOwner, { error: String -> setError(error, binding.signUpNameTv) })
        viewModel.errorEmail.observe(viewLifecycleOwner, { error: String -> setError(error, binding.signUpEmailTv) })
        viewModel.errorPass.observe(viewLifecycleOwner, { error: String -> setError(error, binding.signUpPassTv) })
        viewModel.errorWeight.observe(viewLifecycleOwner, { error: String -> setError(error, binding.signUpWeightTv) })
    }

    private fun showError(text: String) {
        Toasty.error(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun showProgress(isVisible: Boolean) {
        binding.progressCircularRegistration.isVisible = isVisible
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

    private fun startMainActivity(error: String) {
        when (error) {
            VALID -> {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
            else -> {
                showError(error)
            }
        }
    }

    /**
     * Подчеркивает Sign in для пользователя и делает ее ссылкой на фраагмент аутентификации
     */
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