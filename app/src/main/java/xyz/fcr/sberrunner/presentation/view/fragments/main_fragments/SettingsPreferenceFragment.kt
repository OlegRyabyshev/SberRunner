package xyz.fcr.sberrunner.presentation.view.fragments.main_fragments

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.view.activities.MainActivity
import xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels.SharedSettingsViewModel
import javax.inject.Inject

class SettingsPreferenceFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by viewModels<SharedSettingsViewModel>({ activity as MainActivity }) { factory }

    init {
        App.appComponent.inject(this)
    }

    private var namePref: EditTextPreference? = null
    private var weightPref: EditTextPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return super.onPreferenceTreeClick(preference)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.displayNameAndWeightInSummary()


        namePref = findPreference("name_key")
        namePref?.setOnPreferenceChangeListener { _, newName ->
            viewModel.updateName(newName as String)
            return@setOnPreferenceChangeListener false
        }

        weightPref = findPreference("weight_key")
        weightPref?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }
        weightPref?.setOnPreferenceChangeListener { _, newWeight ->
            viewModel.updateWeight(newWeight as String)
            return@setOnPreferenceChangeListener false
        }

        val themePref: ListPreference? = findPreference("theme_key")
        themePref?.setOnPreferenceChangeListener { _, newValue ->
            when (newValue) {
                Configuration.UI_MODE_NIGHT_UNDEFINED.toString() ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_UNSPECIFIED)
                Configuration.UI_MODE_NIGHT_NO.toString() ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Configuration.UI_MODE_NIGHT_YES.toString() ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            return@setOnPreferenceChangeListener true
        }

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

        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.nameSummaryLiveData.observe(viewLifecycleOwner) { name: String ->
            namePref?.summary = name
        }
        viewModel.weightSummaryLiveData.observe(viewLifecycleOwner) { weight: String ->
            weightPref?.summary = weight
        }
    }
}