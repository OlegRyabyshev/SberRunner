package xyz.fcr.sberrunner.presentation.view.fragments.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.presentation.view.fragments.firebase.LoginFragment

/**
 * Третий фрагмент приветствия. Пользователь может перейти отсюда на фрагменты аутентификации и регистрации.
 */
class WelcomeFragmentThird : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome_third, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val enterWithAccountButton: MaterialButton = view.findViewById(R.id.btn_enter_with_account)

        enterWithAccountButton.setOnClickListener {
            val loginFragment = LoginFragment()

            val manager = activity?.supportFragmentManager
            manager
                ?.beginTransaction()
                ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                ?.replace(R.id.welcome_container, loginFragment)
                ?.commit()
        }
    }
}