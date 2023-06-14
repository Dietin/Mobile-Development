package academy.bangkit.capstone.dietin.ui.main_screen.profile

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.databinding.FragmentProfileBinding
import academy.bangkit.capstone.dietin.helper.BottomSheetHelper
import academy.bangkit.capstone.dietin.ui.auth.activity.AuthenticationActivity
import academy.bangkit.capstone.dietin.ui.main_screen.profile.edit.EditProfileFragment
import academy.bangkit.capstone.dietin.ui.onboarding.activity.OnboardingActivity
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileFragment : Fragment(), BottomSheetHelper {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel
    private lateinit var loader: AlertDialog

    private var editProfileFragment : EditProfileFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[ProfileViewModel::class.java]
        setupViewModelBinding()
        loader = Utils.generateLoader(requireActivity())
        return binding.root
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
                setAllContent()
            }
        }

        viewModel.logoutSuccess.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(requireActivity(), AuthenticationActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListener()
    }


    override fun onResume() {
        super.onResume()
        editProfileFragment = EditProfileFragment()
        lifecycleScope.launch {
            val user = viewModel.getUser()
            editProfileFragment?.arguments = Bundle().apply {
                putString(EditProfileFragment.NAME_FIELD, user?.name)
                putString(EditProfileFragment.EMAIL_FIELD, user?.email)
            }
        }
        editProfileFragment!!.setBottomSheetListener(this)
    }

    override fun onStart() {
        super.onStart()
        setAllContent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListener(){

        binding.btnUpdateProfile.setOnClickListener {
            editProfileFragment!!.show(parentFragmentManager, EditProfileFragment.TAG)
        }

        binding.btnGoToOnboarding.setOnClickListener {
            val intent = Intent(requireActivity(), OnboardingActivity::class.java)
            intent.putExtra(OnboardingActivity.EXTRA_IS_UPDATE, true)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        binding.btnLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }


    private fun setAllContent() = lifecycleScope.launch {

        val user = viewModel.getUser()
        val dataUser = viewModel.getDataUser()

        binding.tvUserName.text = user?.name
        binding.tvUserEmail.text = user?.email

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val dateCreatedAt = try {
            sdf.parse(user?.createdAt!!)!!
        } catch (e: Exception){
            Calendar.getInstance().time
        }
        binding.tvUserMemberSince.text = Html.fromHtml(getString(R.string.user_member_since, SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(dateCreatedAt)), Html.FROM_HTML_MODE_COMPACT)

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


    override fun dataChange(name: String, email: String) {
        viewModel.updateUser(name, email)
    }



}