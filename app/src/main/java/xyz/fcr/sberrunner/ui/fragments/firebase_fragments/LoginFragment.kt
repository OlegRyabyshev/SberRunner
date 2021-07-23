package xyz.fcr.sberrunner.ui.fragments.firebase_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.FragmentLoginBinding
import xyz.fcr.sberrunner.ui.fragments.MainScreenFragment

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            val manager = activity?.supportFragmentManager
            manager
                ?.beginTransaction()
                ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                ?.replace(R.id.container, MainScreenFragment())
                ?.commit()
        }

        binding.startRegistrationFragment.setOnClickListener {
            val manager = activity?.supportFragmentManager
            manager
                ?.beginTransaction()
                ?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                ?.replace(R.id.container, RegistrationFragment())
                ?.addToBackStack("")
                ?.commit()
        }
    }
}