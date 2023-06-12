package academy.bangkit.capstone.dietin.ui.onboarding.fragments.page5

import academy.bangkit.capstone.dietin.MainScreenActivity
import academy.bangkit.capstone.dietin.databinding.FragmentOnboarding5Binding
import academy.bangkit.capstone.dietin.ui.onboarding.activity.OnboardingActivity
import academy.bangkit.capstone.dietin.ui.onboarding.activity.OnboardingViewModel
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class Onboarding5Fragment : Fragment() {

    private var _binding: FragmentOnboarding5Binding? = null
    private val binding get() = _binding!!
    private lateinit var activity: OnboardingActivity
    private val viewModel: OnboardingViewModel by activityViewModels { ViewModelFactory.getInstance(requireActivity().application) }
    // Kalau mau, bisa kita pakai view model dari activity di fragment ini untuk share data: https://developer.android.com/guide/fragments/communicate#viewmodel

    private var isUploadSuccess = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboarding5Binding.inflate(inflater, container, false)
        activity = getActivity() as OnboardingActivity
        setupViewModelBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBtnGoToHome(false, "Mengunggah data...")

        binding.btnGoToHome.setOnClickListener {
            if (isUploadSuccess) {
                // Update preferences
                val intent = Intent(activity, MainScreenActivity::class.java)
                startActivity(intent)
            } else {
                activity.uploadUserData()
                setBtnGoToHome(false, "Mengunggah data...")
            }
        }
    }

    private fun setupViewModelBinding() {
        viewModel.isSuccess.observe(viewLifecycleOwner) {
            setupResponse(it)
        }
    }

    fun setupResponse(isSuccess: Boolean) = lifecycleScope.launch {
        if (isSuccess) {
            isUploadSuccess = true
            setBtnGoToHome(true, "Lanjutkan")

            // Update preferences
            Utils.setIsUserFirstTime(requireActivity(), 0)
        } else {
            isUploadSuccess = false
            setBtnGoToHome(true, "Coba lagi")
        }
    }

    private fun setBtnGoToHome(isEnabled: Boolean, text: String) {
        binding.btnGoToHome.isEnabled = isEnabled
        binding.btnGoToHome.text = text
        if (isEnabled) {
            binding.btnGoToHome.alpha = 1f
        } else {
            binding.btnGoToHome.alpha = 0.5f
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}