package xyz.fcr.sberrunner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.FragmentMainScreenBinding
import xyz.fcr.sberrunner.ui.fragments.main_fragments.HomeFragment
import xyz.fcr.sberrunner.ui.fragments.main_fragments.MapFragment
import xyz.fcr.sberrunner.ui.fragments.main_fragments.SettingsFragment
import xyz.fcr.sberrunner.ui.fragments.main_fragments.YouFragment

class MainScreenFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openScreen(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    openScreen(HomeFragment())
                }
                R.id.nav_map -> openScreen(MapFragment())
                R.id.nav_you -> openScreen(YouFragment())
                R.id.nav_settings -> openScreen(SettingsFragment())
            }
            true
        }
    }

    private fun openScreen(fragmentToOpen: Fragment) {
        childFragmentManager
            .beginTransaction()
            .add(R.id.main_container, fragmentToOpen, "tag")
            .commit()
    }
}
