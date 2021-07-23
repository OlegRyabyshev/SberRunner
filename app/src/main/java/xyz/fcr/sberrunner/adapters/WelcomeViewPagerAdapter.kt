package xyz.fcr.sberrunner.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import xyz.fcr.sberrunner.ui.fragments.welcome_fragments.WelcomeFirstFragment
import xyz.fcr.sberrunner.ui.fragments.welcome_fragments.WelcomeSecondFragment
import xyz.fcr.sberrunner.ui.fragments.welcome_fragments.WelcomeThirdFragment

class WelcomeViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position){
            1 -> WelcomeSecondFragment()
            2 -> WelcomeThirdFragment()
            else -> WelcomeFirstFragment()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}