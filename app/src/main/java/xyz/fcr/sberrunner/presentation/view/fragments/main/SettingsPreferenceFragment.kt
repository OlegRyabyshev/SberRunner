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
import es.dmoral.toasty.Toasty
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
    private var themePref: ListPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return super.onPreferenceTreeClick(preference)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        namePref = findPreference(NAME_KEY)
        namePref?.setOnPreferenceChangeListener { _, newName ->
            if (nameIsValid(newName as String)) {
                viewModel.updateName(newName)
                return@setOnPreferenceChangeListener true
            }
            return@setOnPreferenceChangeListener false
        }

        weightPref = findPreference(WEIGHT_KEY)
        weightPref?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }
        weightPref?.setOnPreferenceChangeListener { _, newWeight ->
            if (weightIsValid(newWeight as String)) {
                viewModel.updateWeight(newWeight)
                return@setOnPreferenceChangeListener true
            }
            return@setOnPreferenceChangeListener false
        }

        themePref = findPreference(THEME_KEY)
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
    }

    /**
     * Проверка корректности нового имени пользователя
     *
     * @param nameToCheck [String] - имя пользователя
     * @return [Boolean] - корректеный ввод (true) / некорректный ввод (false)
     */
    private fun nameIsValid(nameToCheck: String): Boolean {
        val name = nameToCheck.trim { it <= ' ' }

        return when {
            name.isBlank() -> {
                displayError(App.appComponent.context().getString(R.string.name_cant_be_empty))
                false
            }
            else -> true
        }
    }

    /**
     * Проверка корректности нового веса пользователя
     *
     * @param weightToCheck [String] - вес пользователя
     * @return [Boolean] - корректеный ввод (true) / некорректный ввод (false)
     */
    private fun weightIsValid(weightToCheck: String): Boolean {
        val weight = weightToCheck.toIntOrNull()

        return when {
            weight == null || weight > 350 || weight <= 0 || weightToCheck.startsWith("0") -> {
                displayError(App.appComponent.context().getString(R.string.weight_not_valid))
                false
            }
            else -> true
        }
    }

    /**
     * Вывод ошибок
     *
     * @param error [String] - текст ошибки
     */
    private fun displayError(error: String){
        Toasty.error(requireContext(), error, Toasty.LENGTH_SHORT).show()
    }
}