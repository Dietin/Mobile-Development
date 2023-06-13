package academy.bangkit.capstone.dietin.ui.main_screen.favourite

import academy.bangkit.capstone.dietin.databinding.FragmentFavouriteBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.btnBack.setOnClickListener {
//             TODO: In the morning
//        }
    }

    private fun setupListener() {
//        binding.inputSearch.editText?.addTextChangedListener { s ->
//            if (s?.length!! > 0) {
//                if (activeFragment != onSearchFrg) {
//                    changeFragmentSearch(onSearchFrg)
//                }
//                onSearchFrg.updateQuery(s.toString())
//            }else{
//                changeFragmentSearch(beforeSearchFrg)
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}