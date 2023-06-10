package academy.bangkit.capstone.dietin.ui.onboarding.fragments.page1

import academy.bangkit.capstone.dietin.databinding.FragmentOnboarding1Binding
import academy.bangkit.capstone.dietin.ui.onboarding.activity.OnboardingActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class Onboarding1Fragment : Fragment() {

    private var _binding: FragmentOnboarding1Binding? = null
    private val binding get() = _binding!!
    private lateinit var activity: OnboardingActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding  = FragmentOnboarding1Binding.inflate(inflater, container, false)
        activity = getActivity() as OnboardingActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity.setButtonContinueState(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}