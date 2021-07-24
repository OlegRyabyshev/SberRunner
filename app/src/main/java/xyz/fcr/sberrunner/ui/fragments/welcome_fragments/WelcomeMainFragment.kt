package xyz.fcr.sberrunner.ui.fragments.welcome_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import xyz.fcr.sberrunner.databinding.FragmentWelcomeMainBinding
import xyz.fcr.sberrunner.adapters.WelcomeViewPagerAdapter

class WelcomeMainFragment : Fragment() {

    private var _binding: FragmentWelcomeMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var adapterWelcome: WelcomeViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initWelcomeTabs()
    }

    private fun initWelcomeTabs() {
        tabLayout = binding.welcomeTabs
        viewPager = binding.viewPager2

        adapterWelcome = WelcomeViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapterWelcome

        TabLayoutMediator(tabLayout, viewPager) { tab, _ ->
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }
}