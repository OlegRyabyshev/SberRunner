package xyz.fcr.sberrunner.ui.fragments.welcome_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import es.dmoral.toasty.Toasty
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.ui.fragments.firebase_fragments.LoginFragment

class WelcomeFragmentThird : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome_third, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val enterWithAccountButton : MaterialButton = view.findViewById(R.id.btn_enter_with_account)
        val skipTextView : TextView = view.findViewById(R.id.tv_skip_login)

        enterWithAccountButton.setOnClickListener {
            val loginFragment = LoginFragment()

            val manager = activity?.supportFragmentManager
            manager
                ?.beginTransaction()
                ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                ?.replace(R.id.welcome_container, loginFragment)
                ?.commit()
        }

        skipTextView.setOnClickListener {
            Toasty.info(requireContext(), "Not supported yet", Toast.LENGTH_SHORT).show()
        }
    }
}