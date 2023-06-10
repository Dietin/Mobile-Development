package academy.bangkit.capstone.dietin.ui.main_screen.search

import academy.bangkit.capstone.dietin.databinding.FragmentSearchBinding
import academy.bangkit.capstone.dietin.ui.main_screen.search.before.BeforeSearchFragment
import academy.bangkit.capstone.dietin.ui.main_screen.search.on.OnSearchFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val onSearchFrg = OnSearchFragment()
    private val beforeSearchFrg = BeforeSearchFragment()

    private val activeFragment: Fragment = beforeSearchFrg

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeFragmentSearch(onSearchFrg)
        changeFragmentSearch(beforeSearchFrg)
        setupListener()
    }

    private fun setupListener() {
        binding.inputSearch.editText?.addTextChangedListener { s ->
            if (s?.length!! > 0) {
                if (activeFragment != onSearchFrg) {
                    changeFragmentSearch(onSearchFrg)
                }
                onSearchFrg.updateQuery(s.toString())
            }else{
                changeFragmentSearch(beforeSearchFrg)
            }
        }
    }

    private fun changeFragmentSearch(fragment : Fragment){
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentSearchHolder.id, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}