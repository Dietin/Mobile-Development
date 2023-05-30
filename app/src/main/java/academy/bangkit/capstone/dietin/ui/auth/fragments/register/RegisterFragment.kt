package academy.bangkit.capstone.dietin.ui.auth.fragments.register

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.databinding.FragmentRegisterBinding
import academy.bangkit.capstone.dietin.ui.auth.activity.AuthenticationActivity
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding  = FragmentRegisterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[RegisterViewModel::class.java]


        val genders = resources.getStringArray(R.array.genders)
        val dropdownAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, genders)

        binding.actGender.setAdapter(dropdownAdapter)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnGoLogin.setOnClickListener {
            (activity as AuthenticationActivity).supportFragmentManager.popBackStack()
        }

        binding.btnRegister.setOnClickListener {
            Toast.makeText(activity, "Register Yoooo...", Toast.LENGTH_SHORT).show()
            //belum di buat
        }

        binding.actGender.setOnItemClickListener { _, _, position, _ ->

            when(position){
                0 -> binding.inputGender.startIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_male_divider, null)
                1 -> binding.inputGender.startIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_female_divider, null)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}