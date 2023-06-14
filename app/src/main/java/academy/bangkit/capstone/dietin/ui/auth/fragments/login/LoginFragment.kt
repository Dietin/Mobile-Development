package academy.bangkit.capstone.dietin.ui.auth.fragments.login

import academy.bangkit.capstone.dietin.MainScreenActivity
import academy.bangkit.capstone.dietin.databinding.FragmentLoginBinding
import academy.bangkit.capstone.dietin.ui.auth.activity.AuthenticationActivity
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel
    private lateinit var loader: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding  = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[LoginViewModel::class.java]
        setupViewModelBinding()
        loader = Utils.generateLoader(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGoRegister.setOnClickListener {

            val listElements : ArrayList<Pair<View, String>> = ArrayList()

            listElements.add(Pair(binding.mainLogo, "se_main_logo"))
            listElements.add(Pair(binding.inputEmail, "se_name"))
            listElements.add(Pair(binding.inputPassword, "se_password"))
            listElements.add(Pair(binding.btnLogin, "se_main_button"))
            listElements.add(Pair(binding.btnGoRegister, "se_go_button"))
            listElements.add(Pair(binding.mainCard, "se_main_card"))
            listElements.add(Pair(binding.topCard, "se_top_card"))
            listElements.add(Pair(binding.bottomCard, "se_bottom_card"))

            (activity as AuthenticationActivity).setFragment("register", listElements = listElements)
        }

        binding.btnLogin.setOnClickListener {
            // Reset all errors
            binding.inputEmail.error = null
            binding.inputPassword.error = null

            // Continue
            viewModel.clientLogin(
                email = binding.inputEmail.editText?.text.toString(),
                password = binding.inputPassword.editText?.text.toString()
            )
        }
    }

    private fun setupViewModelBinding() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                loader.show()
            } else {
                loader.dismiss()
            }
        }

        viewModel.message.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        // Handling login result
        viewModel.loginResult.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                if (it.token.isNotEmpty()) {
                    // Go to homepage
                    val intent = Intent(requireContext(), MainScreenActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }

        // Handling login error
        viewModel.loginError.observe(viewLifecycleOwner) {
            it.errors?.forEach { (key, value) ->
                when (key) {
                    "email" -> binding.inputEmail.error = value[0]
                    "password" -> binding.inputPassword.error = value[0]
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}