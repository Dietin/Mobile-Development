package academy.bangkit.capstone.dietin

import academy.bangkit.capstone.dietin.data.remote.service.ApiConfig
import academy.bangkit.capstone.dietin.databinding.FragmentFirstBinding
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch


class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            binding.buttonFirst.isEnabled = false

            thisIsNotAGoodArchitecturePleaseMoveToViewModelWithLiveDataButJustForTestingThisIsOkayHere()

            binding.buttonFirst.setOnClickListener {
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            }

            binding.buttonFirst.isEnabled = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("FirstFragment", "onDestroyView")
        _binding = null
    }

    private suspend fun thisIsNotAGoodArchitecturePleaseMoveToViewModelWithLiveDataButJustForTestingThisIsOkayHere() {
        val client = ApiConfig.getRecipeApiService()
        try {
            val response = client.getRecipes()
            var recipeTxt = ""
            response.data.forEach { rec ->
                recipeTxt += "${rec.name} | ${rec.calories} | ${rec.carbs} | ${rec.fats} | ${rec.proteins}\n\n"
            }
            binding.tvDisplayItem.text = recipeTxt
        } catch (e: Exception) {
            binding.tvDisplayItem.text = e.message
        }
    }
}