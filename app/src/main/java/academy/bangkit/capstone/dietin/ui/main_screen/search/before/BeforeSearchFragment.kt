package academy.bangkit.capstone.dietin.ui.main_screen.search.before

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.Result
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.data.remote.model.SearchHistory
import academy.bangkit.capstone.dietin.databinding.FragmentBeforeSearchBinding
import academy.bangkit.capstone.dietin.databinding.ItemFoodCard1Binding
import academy.bangkit.capstone.dietin.databinding.ItemFoodCard2Binding
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide

class BeforeSearchFragment : Fragment() {

    private var _binding : FragmentBeforeSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BeforeSearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBeforeSearchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[BeforeSearchViewModel::class.java]
        setupViewModelBinding()
        return binding.root
    }

    private fun setupViewModelBinding(){
        viewModel.favouriteList.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    Utils.setShimmerVisibility(binding.shimmerFavourite, true)
                }
                is Result.Success -> {
                    Utils.setComposableFunction(binding.cvFavouriteFood) { SetFavouriteFoods(it.data) }
                    Utils.setShimmerVisibility(binding.shimmerFavourite, false)
                }
                is Result.Error -> {
                    Utils.setShimmerVisibility(binding.shimmerFavourite, false)
                }
            }
        }

        viewModel.searchHistory.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    Utils.setShimmerVisibility(binding.shimmerLatestSearch, true)
                    binding.llNoDataSearchHistory.visibility = View.GONE
                }
                is Result.Success -> {
                    Log.e("TAG", "setupViewModelBinding: ${it.data}")
                    if (it.data.isNotEmpty()) {
                        Utils.setComposableFunction(binding.cvLatestSearch) { SetSearchHistory(it.data) }
                    } else {
                        binding.llNoDataSearchHistory.visibility = View.VISIBLE
                    }
                    Utils.setShimmerVisibility(binding.shimmerLatestSearch, false)
                }
                is Result.Error -> {
                    Utils.setShimmerVisibility(binding.shimmerLatestSearch, false)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Composable
    fun SetSearchHistory(foodList: List<SearchHistory>){
        LazyRow(
            modifier = Modifier
                .padding(PaddingValues(bottom = 8.dp)),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
        ) {
            items(foodList){ item ->
                AndroidViewBinding(
                    factory = { layoutInflater, parent, _ ->
                        ItemFoodCard1Binding.inflate(layoutInflater, parent, false)
                    },

                    update = {
                        val recipe = item.recipe
                        Glide.with(this.root)
                            .load(recipe?.image)
                            .placeholder(R.drawable.food_placeholder)
                            .into(this.ivFoodImage)
                        this.chipFoodCategory.apply {
                            text = recipe?.category?.name
                            chipBackgroundColor = ColorStateList.valueOf(recipe?.category?.getColourArrayAsHex() ?: 0xffffff)
                        }

                        this.tvFoodName.text = recipe?.name
                        this.tvFoodCal.text = getString(R.string.food_cal, recipe?.calories?.toInt())
                    }
                )
            }
        }

    }

    @Composable
    fun SetFavouriteFoods(foodList: List<Recipe>){
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        ) {
            items(foodList){ item ->
                AndroidViewBinding(
                    factory = { layoutInflater, parent, _ ->
                        ItemFoodCard2Binding.inflate(layoutInflater, parent, false)
                    },

                    update = {
                        val recipe = item
                        Glide.with(this.root)
                            .load(recipe?.image)
                            .placeholder(R.drawable.food_placeholder)
                            .into(this.ivFoodImage)
                        this.chipFoodCategory.apply {
                            text = recipe?.category?.name
                            chipBackgroundColor = ColorStateList.valueOf(recipe?.category?.getColourArrayAsHex() ?: 0xffffff)
                        }

                        this.tvFoodName.text = recipe?.name
                        this.tvFoodCal.text = getString(R.string.food_cal, recipe?.calories?.toInt())
                    }
                )
            }
        }
    }
}