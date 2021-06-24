package xyz.fcr.sberrunner.fragments.welcome_fragments.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import xyz.fcr.sberrunner.fragments.welcome_fragments.WelcomeFirstFragment
import xyz.fcr.sberrunner.fragments.welcome_fragments.WelcomeSecondFragment
import xyz.fcr.sberrunner.fragments.welcome_fragments.WelcomeThirdFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        when (position){
            1 -> return WelcomeSecondFragment()
            2 -> return WelcomeThirdFragment()
        }

        return WelcomeFirstFragment()
    }

    override fun getItemCount(): Int {
        return 3
    }
}