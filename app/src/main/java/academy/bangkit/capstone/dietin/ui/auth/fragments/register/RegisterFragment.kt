package academy.bangkit.capstone.dietin.ui.auth.fragments.register

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.databinding.FragmentRegisterBinding
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RegisterViewModel
    private lateinit var loader: AlertDialog

    private var idGender: Int? = null


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

        val genders = resources.getStringArray(R.array.genders)
        val dropdownAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, genders)

        ViewCompat.setZ(binding.bottomCard, 0f)

        binding.actGender.setAdapter(dropdownAdapter)

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
            binding.inputGender.error = null

            // Continue
            viewModel.clientRegister(
                name = binding.inputName.editText?.text.toString(),
                email = binding.inputEmail.editText?.text.toString(),
                password = binding.inputPassword.editText?.text.toString(),
                gender = idGender ?: -1,
                weight = 0, // TODO
                height = 0 // TODO
            )
        }

        binding.actGender.setOnItemClickListener { _, _, position, _ ->
            when(position){
                0 -> binding.inputGender.startIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_male_divider, null)
                1 -> binding.inputGender.startIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_female_divider, null)
            }
            idGender = position
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

        //
        viewModel.registeredUser.observe(viewLifecycleOwner) {
            if (it.id != 0) {
                Toast.makeText(requireContext(), "Berhasil mendaftar. Silakan masuk.", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        viewModel.registerError.observe(viewLifecycleOwner) {
            it.errors?.forEach { (key, value) ->
                when (key) {
                    "name" -> binding.inputName.error = value[0]
                    "email" -> binding.inputEmail.error = value[0]
                    "password" -> binding.inputPassword.error = value[0]
                    "gender" -> binding.inputGender.error = value[0]
                    "weight" -> value[0] // TODO
                    "height" -> value[0] // TODO
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}