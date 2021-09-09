package xyz.fcr.sberrunner.presentation.view.fragments.main

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
import xyz.fcr.sberrunner.presentation.viewmodels.main.SharedSettingsViewModel
import xyz.fcr.sberrunner.utils.Constants.DEL_ACCOUNT
import xyz.fcr.sberrunner.utils.Constants.LOG_OUT
import xyz.fcr.sberrunner.utils.Constants.NAME_KEY
import xyz.fcr.sberrunner.utils.Constants.THEME_KEY
import xyz.fcr.sberrunner.utils.Constants.WEIGHT_KEY
import javax.inject.Inject

/**
 * Фрагмент настроек
 */
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


        namePref = findPreference(NAME_KEY)
        namePref?.setOnPreferenceChangeListener { _, newName ->
            viewModel.updateName(newName as String)
            return@setOnPreferenceChangeListener false
        }

        weightPref = findPreference(WEIGHT_KEY)
        weightPref?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }
        weightPref?.setOnPreferenceChangeListener { _, newWeight ->
            viewModel.updateWeight(newWeight as String)
            return@setOnPreferenceChangeListener false
        }

        val themePref: ListPreference? = findPreference(THEME_KEY)
        themePref?.setOnPreferenceChangeListener { _, newValue ->
            when (newValue) {
                Configuration.UI_MODE_NIGHT_UNDEFINED.toString() ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                Configuration.UI_MODE_NIGHT_NO.toString() ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Configuration.UI_MODE_NIGHT_YES.toString() ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            return@setOnPreferenceChangeListener true
        }

        val logOutPref: Preference? = findPreference(LOG_OUT)
        logOutPref?.setOnPreferenceClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(getString(R.string.log_out_from_account))
                setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                setPositiveButton(getString(R.string.exit)) { dialog, _ ->
                    viewModel.signOut()
                    dialog.dismiss()
                }
                show()
            }
            true
        }

        val deleteAccountPref: Preference? = findPreference(DEL_ACCOUNT)
        deleteAccountPref?.setOnPreferenceClickListener {
            MaterialAlertDialogBuilder(requireContext()).apply {
                setTitle(getString(R.string.delete_account))
                setMessage(getString(R.string.data_will_be_lost))
                setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                    viewModel.deleteAccount()
                    dialog.dismiss()
                }
                show()
            }
            true
        }

        observeLiveData()
    }

    /**
     * Отслеживание изменений в livedata вьюмодели
     */
    private fun observeLiveData() {
        viewModel.nameSummaryLiveData.observe(viewLifecycleOwner) { name: String ->
            namePref?.summary = name
        }
        viewModel.weightSummaryLiveData.observe(viewLifecycleOwner) { weight: String ->
            weightPref?.summary = weight
        }
    }
}