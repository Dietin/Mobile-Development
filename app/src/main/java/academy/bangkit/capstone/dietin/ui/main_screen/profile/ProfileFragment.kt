package academy.bangkit.capstone.dietin.ui.main_screen.profile

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.databinding.FragmentProfileBinding
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel
    private lateinit var loader: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[ProfileViewModel::class.java]
        setAllContent()
        loader = Utils.generateLoader(requireActivity())
        return binding.root
    }


    private fun setAllContent(){

        binding.tvUserName.text = "Vincent"
        binding.tvUserEmail.text = "siapanih@gmail.com"

        binding.tvUserAge.text = Html.fromHtml(getString(R.string.user_age, 30))

        val gender = "Pria"
        when(gender){
            "Pria" -> {
                binding.tvUserGender.apply {
                    text = gender
                    setCompoundDrawables(ResourcesCompat.getDrawable(resources, R.drawable.ic_male_32, null), null, null, null)
                }
            }

            "Wanita" -> {
                binding.tvUserGender.apply {
                    text = gender
                    setCompoundDrawables(ResourcesCompat.getDrawable(resources, R.drawable.ic_female_32, null), null, null, null)
                }
            }
        }

        binding.tvUserWeight.text = Html.fromHtml(getString(R.string.user_weight, 70))
        binding.tvUserHeight.text = Html.fromHtml(getString(R.string.user_height, 170))


    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}