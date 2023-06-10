package academy.bangkit.capstone.dietin.ui.auth.fragments.register

import academy.bangkit.capstone.dietin.databinding.FragmentRegisterBinding
import academy.bangkit.capstone.dietin.ui.onboarding.activity.OnboardingActivity
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RegisterViewModel
    private lateinit var loader: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding  = FragmentRegisterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[RegisterViewModel::class.java]
        setupViewModelBinding()
        loader = Utils.generateLoader(requireActivity())

        ViewCompat.setZ(binding.bottomCard, 0f)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnGoLogin.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnRegister.setOnClickListener {
            // Reset all errors
            binding.inputName.error = null
            binding.inputEmail.error = null
            binding.inputPassword.error = null

            // Continue
            viewModel.clientRegister(
                name = binding.inputName.editText?.text.toString(),
                email = binding.inputEmail.editText?.text.toString(),
                password = binding.inputPassword.editText?.text.toString(),
//                gender = idGender ?: -1
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

        viewModel.isSuccess.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(requireContext(), OnboardingActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        viewModel.registerError.observe(viewLifecycleOwner) {
            it.errors?.forEach { (key, value) ->
                when (key) {
                    "name" -> binding.inputName.error = value[0]
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