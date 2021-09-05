package xyz.fcr.sberrunner.presentation.view.fragments.welcome.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import xyz.fcr.sberrunner.presentation.view.fragments.welcome.WelcomeFragmentFirst
import xyz.fcr.sberrunner.presentation.view.fragments.welcome.WelcomeFragmentSecond
import xyz.fcr.sberrunner.presentation.view.fragments.welcome.WelcomeFragmentThird

/**
 * ViewPager адаптер фрагмента приветствия.
 */
class WelcomeViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WelcomeFragmentFirst()
            1 -> WelcomeFragmentSecond()
            else -> WelcomeFragmentThird()
        }
    }

    override fun getItemCount() = 3
}