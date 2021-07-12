package xyz.fcr.sberrunner.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import xyz.fcr.sberrunner.R

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startRegistrationButton : TextView = view.findViewById(R.id.switch_to_registration)

        startRegistrationButton.setOnClickListener {
            val registrationFragment = RegistrationFragment()

            val manager = activity?.supportFragmentManager
            manager
                ?.beginTransaction()
                ?.replace(R.id.container, registrationFragment)
                ?.addToBackStack("")
                ?.commit()
        }
    }
}