package academy.bangkit.capstone.dietin.ui.auth.fragments.login

import academy.bangkit.capstone.dietin.MainActivity
import academy.bangkit.capstone.dietin.databinding.FragmentLoginBinding
import academy.bangkit.capstone.dietin.ui.auth.activity.AuthenticationActivity
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding  = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[LoginViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGoRegister.setOnClickListener {
            (activity as AuthenticationActivity).setFragment("register")
        }

        binding.btnLogin.setOnClickListener {
            val intentHome = Intent(activity, MainActivity::class.java)
            startActivity(intentHome)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}