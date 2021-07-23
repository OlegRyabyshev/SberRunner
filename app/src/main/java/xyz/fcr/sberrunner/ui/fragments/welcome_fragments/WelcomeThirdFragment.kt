package xyz.fcr.sberrunner.ui.fragments.welcome_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.ui.fragments.firebase_fragments.LoginFragment


class WelcomeThirdFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
                ?.replace(R.id.container, loginFragment)
                ?.commit()
        }

        skipTextView.setOnClickListener {
            Toast.makeText(context, "Not supported yet", Toast.LENGTH_SHORT).show()
        }
    }
}