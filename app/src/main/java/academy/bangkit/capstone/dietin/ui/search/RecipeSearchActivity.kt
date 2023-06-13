package academy.bangkit.capstone.dietin.ui.search

import academy.bangkit.capstone.dietin.databinding.ActivityRecipeSearchBinding
import academy.bangkit.capstone.dietin.ui.search.before.BeforeSearchFragment
import academy.bangkit.capstone.dietin.ui.search.on.OnSearchFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment

class RecipeSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeSearchBinding

    private val onSearchFrg = OnSearchFragment()
    private val beforeSearchFrg = BeforeSearchFragment()

    private var activeFragment: Fragment = beforeSearchFrg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeFragmentSearch(onSearchFrg)
        changeFragmentSearch(beforeSearchFrg)
        setupListener()

        // Auto focus on the search bar
        binding.inputSearch.editText?.requestFocus()

        binding.btnBack.setOnClickListener {
            goBack()
        }
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
        supportFragmentManager
            .beginTransaction()
            .replace(binding.fragmentSearchHolder.id, fragment)
            .addToBackStack(null)
            .commit()
        activeFragment = fragment
    }

    private fun goBack() {
        if (activeFragment == onSearchFrg) {
            changeFragmentSearch(beforeSearchFrg)
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        goBack()
    }
}