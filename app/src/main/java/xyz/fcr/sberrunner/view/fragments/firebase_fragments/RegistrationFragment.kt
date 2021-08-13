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
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.FragmentRegistrationBinding
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty
import xyz.fcr.sberrunner.data.repository.FirebaseRepository
import xyz.fcr.sberrunner.utils.Constants.VALID
import xyz.fcr.sberrunner.utils.SchedulersProvider
import xyz.fcr.sberrunner.view.activities.MainActivity
import xyz.fcr.sberrunner.viewmodels.firebase_viewmodels.RegistrationViewModel

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

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
                val fireAuth = FirebaseAuth.getInstance()
                val fireStore = FirebaseFirestore.getInstance()
                val firebaseRepo = FirebaseRepository(fireAuth, fireStore)
                val schedulersProvider = SchedulersProvider()

                return RegistrationViewModel(firebaseRepo, schedulersProvider) as T
            }
        }).get(RegistrationViewModel::class.java)


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

    private fun observeLiveData() {
        viewModel.progressLiveData.observe(viewLifecycleOwner, { isVisible: Boolean -> showProgress(isVisible) })
        viewModel.successLiveData.observe(viewLifecycleOwner, { isSucceed: Boolean -> startMainActivity(isSucceed) })
        viewModel.errorLiveData.observe(viewLifecycleOwner, { throwable: Throwable -> showWarning(throwable) })
        viewModel.errorFirebase.observe(viewLifecycleOwner, { throwable: Throwable -> showError(throwable) })

        viewModel.errorName.observe(viewLifecycleOwner, { error: String -> setError(error, binding.signUpNameTv) })
        viewModel.errorEmail.observe(viewLifecycleOwner, { error: String -> setError(error, binding.signUpEmailTv) })
        viewModel.errorPass.observe(viewLifecycleOwner, { error: String -> setError(error, binding.signUpPassTv) })
        viewModel.errorWeight.observe(viewLifecycleOwner, { error: String -> setError(error, binding.signUpWeightTv) })
    }

    private fun showWarning(throwable: Throwable) {
        Toasty.warning(requireContext(), throwable.message.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun showError(throwable: Throwable) {
        Toasty.error(requireContext(), throwable.message.toString(), Toast.LENGTH_SHORT).show()
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

    private fun startMainActivity(isSucceed: Boolean) {
        if (isSucceed) {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
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