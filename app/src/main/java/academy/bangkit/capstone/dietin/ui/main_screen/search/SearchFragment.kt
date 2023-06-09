package academy.bangkit.capstone.dietin.ui.main_screen.search

import academy.bangkit.capstone.dietin.ui.main_screen.search.before.BeforeSearchFragment
import academy.bangkit.capstone.dietin.databinding.FragmentSearchBinding
import academy.bangkit.capstone.dietin.ui.main_screen.search.on.OnSearchFragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!


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

        changeFragmentSearch(BeforeSearchFragment())
        setupListener()


    }


    private fun setupListener() {


        binding.inputSearch.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length!! > 0) {
                    changeFragmentSearch(OnSearchFragment())
                }else{
                    changeFragmentSearch(BeforeSearchFragment())
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun changeFragmentSearch(fragment : Fragment){
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentSearchHolder.id, fragment)
            .addToBackStack(null)
            .commit()
    }



}