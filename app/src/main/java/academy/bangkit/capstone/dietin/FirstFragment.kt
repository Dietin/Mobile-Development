package academy.bangkit.capstone.dietin

import academy.bangkit.capstone.dietin.data.remote.service.ApiConfig
import academy.bangkit.capstone.dietin.databinding.FragmentFirstBinding
import academy.bangkit.capstone.dietin.ui.auth.activity.AuthenticationActivity
import academy.bangkit.capstone.dietin.utils.Utils
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


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
//            binding.buttonFirst.isEnabled = false

//            thisIsNotAGoodArchitecturePleaseMoveToViewModelWithLiveDataButJustForTestingThisIsOkayHere()

            binding.buttonFirst.setOnClickListener {
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            }

            binding.buttonLogout.setOnClickListener {
                lifecycleScope.launch {
                    val token = Utils.getToken(requireContext()).first()
                    // disable this button
                    it.isEnabled = false
                    try {
                        ApiConfig.getApiService().logout(
                            "Bearer $token"
                        )
                        logout()
                    } catch (e: IOException) {
                        Log.e("FirstFragment", e.message.toString())
                    } catch (e: HttpException) {
                        Log.e("FirstFragment", e.message.toString())
                        if (e.code() == 401) {
                            logout()
                        }
                    } finally {
                        it.isEnabled = true
                        // Do nothing (for now)
                    }
                }
            }

            binding.buttonFirst.isEnabled = true
        }
    }

    private suspend fun logout() {
        Utils.setToken(requireContext(), "")
        Utils.setUser(requireContext(), null)
        Utils.setUserData(requireContext(), null)
        // Start AuthenticationActivity
        val intent = Intent(requireContext(), AuthenticationActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("FirstFragment", "onDestroyView")
        _binding = null
    }

//    private fun thisIsNotAGoodArchitecturePleaseMoveToViewModelWithLiveDataButJustForTestingThisIsOkayHere() = runBlocking {
//        val client = ApiConfig.getRecipeApiService()
//        try {
//            val response = client.getRecipes()
//            var recipeTxt = ""
//            response.data.forEach { rec ->
//                recipeTxt += "${rec.name} | ${rec.calories} | ${rec.carbs} | ${rec.fats} | ${rec.proteins}\n\n"
//            }
//            binding.tvDisplayItem.text = recipeTxt
//        } catch (e: Exception) {
//            binding.tvDisplayItem.text = e.message
//        }
//    }
}