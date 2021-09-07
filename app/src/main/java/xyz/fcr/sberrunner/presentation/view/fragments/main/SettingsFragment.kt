package xyz.fcr.sberrunner.presentation.view.fragments.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import es.dmoral.toasty.Toasty
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.FragmentSettingsBinding
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.view.activities.MainActivity
import xyz.fcr.sberrunner.presentation.view.activities.WelcomeActivity
import xyz.fcr.sberrunner.presentation.viewmodels.main.SharedSettingsViewModel
import javax.inject.Inject

/**
 * Фрагмент настроек приложения.
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel by viewModels<SharedSettingsViewModel>({ activity as MainActivity }) { factory }

    init {
        App.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showSettings()
        observeLiveData()
    }

    /**
     * Вывод настроек на экран.
     */
    private fun showSettings() {
        val manager = childFragmentManager

        manager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsPreferenceFragment())
            .commit()
    }

    /**
     * Отслеживание изменений в livedata вьюмодели.
     */
    private fun observeLiveData() {
        viewModel.progressLiveData.observe(viewLifecycleOwner, { isVisible: Boolean -> showProgress(isVisible) })
        viewModel.signOutLiveData.observe(viewLifecycleOwner, { result: Boolean -> startWelcomeActivity(result) })
        viewModel.errorLiveData.observe(viewLifecycleOwner, { error: String -> showError(error)})
    }

    private fun showProgress(isVisible: Boolean) {
        binding.progressCircularSettings.isVisible = isVisible
    }

    private fun showError(text: String) {
        Toasty.error(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    /**
     * Выводит на экран приветствия в случае удаления аккаунта или выхода из него.
     */
    private fun startWelcomeActivity(isSucceed: Boolean) {
        if (isSucceed) {
            val intent = Intent(activity, WelcomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}