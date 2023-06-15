package academy.bangkit.capstone.dietin.ui.onboarding.fragments.page5

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.databinding.FragmentOnboarding5Binding
import academy.bangkit.capstone.dietin.ui.main_screen.MainScreenActivity
import academy.bangkit.capstone.dietin.ui.onboarding.activity.OnboardingActivity
import academy.bangkit.capstone.dietin.ui.onboarding.activity.OnboardingViewModel
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

        // Setup title & text
        if (activity.isIntentUpdate) {
            binding.tvTitle.text = getString(R.string.ob5_title_update)
            binding.tvDesc.text = getString(R.string.ob5_desc_update)
        }

        setBtnGoToHome(false, getString(R.string.ob5_btn_uploading))

        binding.btnGoToHome.setOnClickListener {
            if (isUploadSuccess) {
                if (activity.isIntentUpdate) {
                    activity.finish()
                } else {
                    val intent = Intent(activity, MainScreenActivity::class.java)
                    startActivity(intent)
                }
            } else {
                activity.uploadUserData()
                setBtnGoToHome(false, getString(R.string.ob5_btn_uploading))
            }
        }
    }

    private fun setupViewModelBinding() {
        viewModel.isSuccess.observe(viewLifecycleOwner) {
            setupResponse(it)
        }
    }

    private fun setupResponse(isSuccess: Boolean) = lifecycleScope.launch {
        if (isSuccess) {
            isUploadSuccess = true

            if (activity.isIntentUpdate) {
                binding.tvDesc.text = getString(R.string.ob5_desc_finished_update)
                setBtnGoToHome(true, getString(R.string.ob5_btn_updated))
            } else {
                setBtnGoToHome(true, getString(R.string.ob5_btn_uploaded))
            }
        } else {
            isUploadSuccess = false
            setBtnGoToHome(true, getString(R.string.ob5_btn_upload_failed))
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