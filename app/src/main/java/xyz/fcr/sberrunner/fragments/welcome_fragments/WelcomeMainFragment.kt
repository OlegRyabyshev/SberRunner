package xyz.fcr.sberrunner.fragments.welcome_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.FragmentWelcomeMainBinding
import xyz.fcr.sberrunner.fragments.welcome_fragments.adapters.ViewPagerAdapter

class WelcomeMainFragment : Fragment() {
    private var _binding: FragmentWelcomeMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var tabLayout: TabLayout
    private lateinit var pager2: ViewPager2
    private lateinit var adapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        //Проверить можно ли вынести в onCreateView
        initWelcomeTabs()
    }

    private fun initWelcomeTabs() {
        tabLayout = binding.welcomeTabs
        pager2 = binding.viewPager2

        adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
        pager2.adapter = adapter

        TabLayoutMediator(tabLayout, pager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Today"
                }
                1 -> {
                    tab.text = "This week"
                }
            }
        }.attach()

        pager2.isUserInputEnabled = false
    }
}