package academy.bangkit.capstone.dietin.ui.main_screen.search.on

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.databinding.FragmentOnSearchBinding
import academy.bangkit.capstone.dietin.databinding.ItemFoodCard2Binding
import academy.bangkit.capstone.dietin.ui.main_screen.home.HomeViewModel
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide

class OnSearchFragment : Fragment() {

    private var _binding : FragmentOnSearchBinding? = null
    private val binding get() = _binding!!


    //nanti dihapus, ini untuk keperluan data dummy, tapi lagnsung dari API saya tembaknya.. :V
    private lateinit var viewModel: HomeViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOnSearchBinding.inflate(inflater, container, false)

        //ini nanti dihpaus, karena supaya agar cuman dapat memiliki data dummy
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[HomeViewModel::class.java]
        setupDataDummy()
        //nanti hapus ini yoo


        return binding.root
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun setupDataDummy(){
        viewModel.recommendations.observe(viewLifecycleOwner) {
            binding.cvSearchResult.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    MaterialTheme {

                        //tinggal ganti disini
                        SetFoodResult(it)
                    }
                }
            }

        }
    }


    @Composable
    fun SetFoodResult(foodList: List<Recipe>){
        LazyColumn(
            modifier = Modifier
                .padding(PaddingValues(bottom = 8.dp)),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        ) {
            items(foodList){ item ->
                AndroidViewBinding(
                    factory = { layoutInflater, parent, _ ->
                        ItemFoodCard2Binding.inflate(layoutInflater, parent, false)
                    },

                    update = {
                        Glide.with(this.root)
                            .load(item.image)
                            .into(this.ivFoodImage)
                        this.chipFoodCategory.text = item.category.name

                        //nanti set colornya juga

                        this.tvFoodName.text = item.name
                        this.tvFoodCal.text = getString(R.string.food_cal, item.calories.toInt())
                    }
                )
            }
        }

    }

}