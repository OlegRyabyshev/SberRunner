package xyz.fcr.sberrunner.view.fragments.main_fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.data.repository.FirebaseRepository
import xyz.fcr.sberrunner.databinding.FragmentSettingsBinding
import xyz.fcr.sberrunner.utils.SchedulersProvider
import xyz.fcr.sberrunner.view.activities.WelcomeActivity
import xyz.fcr.sberrunner.viewmodels.main_viewmodels.SharedSettingsViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SharedSettingsViewModel

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

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val fireAuth = FirebaseAuth.getInstance()
                val fireStore = FirebaseFirestore.getInstance()
                val firebaseRepo = FirebaseRepository(fireAuth, fireStore)

                return SharedSettingsViewModel(firebaseRepo) as T
            }
        }).get(SharedSettingsViewModel::class.java)

        showSettings()
        observeLiveData()
    }

    private fun showSettings() {
        val manager = childFragmentManager
        manager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsPreference())
            .commit()
    }

    private fun observeLiveData() {
        viewModel.progressLiveData.observe(viewLifecycleOwner, { isVisible: Boolean -> showProgress(isVisible) })
        viewModel.signOutLiveData.observe(viewLifecycleOwner, { result: Boolean -> startWelcomeActivity(result) })
        viewModel.deleteLiveData.observe(viewLifecycleOwner, { result: Boolean -> startWelcomeActivity(result) })
    }

    private fun showProgress(isVisible: Boolean) {
        binding.progressCircularSettings.isVisible = isVisible
    }

    private fun startWelcomeActivity(isSucceed: Boolean) {
        if (isSucceed) {
            val intent = Intent(activity, WelcomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    class SettingsPreference : PreferenceFragmentCompat() {
        private val viewModel: SharedSettingsViewModel by activityViewModels()

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_preference, rootKey)
        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            return super.onPreferenceTreeClick(preference)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val logOutPref: Preference? = findPreference("log_out")
            logOutPref?.setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle("Log out from account?")
                    setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Exit") { dialog, _ ->
                        viewModel.exitAccount()
                        dialog.dismiss()
                    }
                    show()
                }
                true
            }

            val deleteAccountPref: Preference? = findPreference("del_account")
            deleteAccountPref?.setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle("Delete account?")
                    setMessage("All you data and progress will be lost, you sure?")
                    setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Delete") { dialog, _ ->
                        viewModel.deleteAccount()
                        dialog.dismiss()
                    }
                    show()
                }
                true
            }
        }
    }
}