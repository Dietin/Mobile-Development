package academy.bangkit.capstone.dietin.ui.onboarding.fragments.page2

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.databinding.FragmentOnboarding2Binding
import academy.bangkit.capstone.dietin.ui.onboarding.activity.OnboardingActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors

class Onboarding2Fragment : Fragment() {

    private var _binding: FragmentOnboarding2Binding? = null
    private val binding get() = _binding!!
    private lateinit var activity: OnboardingActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding  = FragmentOnboarding2Binding.inflate(inflater, container, false)
        activity = getActivity() as OnboardingActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRadioCard(binding.llTujuan)
    }

    private fun updateRadioCard(card: MaterialCardView, newStatus: Boolean) {
        if (newStatus) {
            card.strokeWidth = dpToPixel(2)
            card.strokeColor = MaterialColors.getColor(card, com.google.android.material.R.attr.colorPrimary, ResourcesCompat.getColor(resources, R.color.satin_gold, null))
        } else {
            card.strokeWidth = dpToPixel(1)
            card.strokeColor = ResourcesCompat.getColor(resources, R.color.jet_calmer, null)
        }
    }

    private fun uncheckAllRadioCard(linearLayout: LinearLayout) {
        for (i in 0 until linearLayout.childCount) {
            val card = linearLayout.getChildAt(i) as MaterialCardView
            updateRadioCard(card, false)
        }
    }

    private fun setupRadioCard(linearLayout: LinearLayout) {
        val selected = activity.userData.goal!!
        for (i in 0 until linearLayout.childCount) {
            val card = linearLayout.getChildAt(i) as MaterialCardView
            card.setOnClickListener {
                uncheckAllRadioCard(linearLayout)
                updateRadioCard(card, true)
                activity.userData.goal = i+1
                activity.setButtonContinueState(true)
            }

            if (selected-1 == i) {
                updateRadioCard(card, true)
            }
        }

        if (selected != 0) {
            activity.setButtonContinueState(true)
        } else {
            activity.setButtonContinueState(false)
        }
    }

    private fun dpToPixel(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}