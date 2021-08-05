package xyz.fcr.sberrunner.view.fragments.main_fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import xyz.fcr.sberrunner.R

class SettingsFragment : PreferenceFragmentCompat() {

//        binding.unSign.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//
//            val intent = Intent(activity, WelcomeActivity::class.java)
//            startActivity(intent)
//            activity?.finish()


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
    }
}