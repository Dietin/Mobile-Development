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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Locale

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


    private fun setAllContent() = lifecycleScope.launch {

        val user = viewModel.getUser()
        val dataUser = viewModel.getDataUser()

        binding.tvUserName.text = user?.name
        binding.tvUserEmail.text = user?.email

        binding.tvUserAge.text = Html.fromHtml(getString(R.string.user_age, dataUser?.age), Html.FROM_HTML_MODE_COMPACT)

        val gender = dataUser?.gender
        // get string array from resources
        val genders = resources.getStringArray(R.array.genders)
        when(gender){
            // gender di db: 1 = pria, 0 = wanita
            // genderdi dropdown: 0 = pria, 1 = wanita
            1 -> {
                binding.tvUserGender.apply {
                    text = genders[0]
                    setCompoundDrawables(ResourcesCompat.getDrawable(resources, R.drawable.ic_male_32, null), null, null, null)
                }
            }

            0 -> {
                binding.tvUserGender.apply {
                    text = genders[1]
                    setCompoundDrawables(ResourcesCompat.getDrawable(resources, R.drawable.ic_female_32, null), null, null, null)
                }
            }
        }

        binding.tvUserWeight.text = Html.fromHtml(getString(R.string.user_weight, String.format(Locale.getDefault(), "%,.1f", dataUser?.currentWeight)), Html.FROM_HTML_MODE_COMPACT)
        binding.tvUserHeight.text = Html.fromHtml(getString(R.string.user_height, String.format(Locale.getDefault(), "%,.1f", dataUser?.height)), Html.FROM_HTML_MODE_COMPACT)


    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}