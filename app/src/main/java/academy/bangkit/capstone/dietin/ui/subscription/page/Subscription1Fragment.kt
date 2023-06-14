package academy.bangkit.capstone.dietin.ui.subscription.page

import academy.bangkit.capstone.dietin.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import academy.bangkit.capstone.dietin.databinding.FragmentSubscription1Binding
import android.widget.Toast


class Subscription1Fragment : Fragment() {

    private var _binding : FragmentSubscription1Binding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding  = FragmentSubscription1Binding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBuySubscription.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.subscription_coming_soon), Toast.LENGTH_SHORT).show()
        }

        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}