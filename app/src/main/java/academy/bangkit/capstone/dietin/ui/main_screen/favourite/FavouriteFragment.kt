package academy.bangkit.capstone.dietin.ui.main_screen.favourite

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.Result
import academy.bangkit.capstone.dietin.data.remote.model.FavouriteRecipe
import academy.bangkit.capstone.dietin.databinding.FragmentFavouriteBinding
import academy.bangkit.capstone.dietin.databinding.ItemFoodCard2Binding
import academy.bangkit.capstone.dietin.ui.food_detail.FoodDetailActivity
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide

class FavouriteFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavouriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[FavouriteViewModel::class.java]
        setupViewModelBinding()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.setOnRefreshListener {
            viewModel.getFavourites()
            binding.root.isRefreshing = false
        }
    }

    private fun setupViewModelBinding() {
        viewModel.favouriteList.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    Utils.setShimmerVisibility(binding.shimmerFavourite, true)
                    Utils.setComposableFunction(binding.cvFavouriteFood) { SetFavouriteFoods(emptyList()) }
                    binding.llNoDataFavourite.visibility = View.GONE
                }
                is Result.Success -> {
                    if (it.data.isNotEmpty()) {
                        Utils.setComposableFunction(binding.cvFavouriteFood) { SetFavouriteFoods(it.data) }
                        binding.llNoDataFavourite.visibility = View.GONE
                    } else {
                        binding.llNoDataFavourite.visibility = View.VISIBLE
                    }
                    Utils.setShimmerVisibility(binding.shimmerFavourite, false)
                }
                is Result.Error -> {
                    Utils.setShimmerVisibility(binding.shimmerFavourite, false)
                }
            }
        }
    }

    @Composable
    fun SetFavouriteFoods(foodList: List<FavouriteRecipe>){
        Column(
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            foodList.forEach { item ->
                AndroidViewBinding(ItemFoodCard2Binding::inflate) {
                    val recipe = item.recipe
                    Glide.with(this.root)
                        .load(recipe?.image)
                        .placeholder(R.drawable.food_placeholder)
                        .into(this.ivFoodImage)
                    this.chipFoodCategory.apply {
                        text = recipe?.category?.name
                        chipBackgroundColor = ColorStateList.valueOf(recipe?.category?.getColourArrayAsHex() ?: 0xffffff)
                    }

                    this.root.setOnClickListener {
                        val intent = Intent(requireContext(), FoodDetailActivity::class.java)
                        intent.putExtra(FoodDetailActivity.EXTRA_RECIPE_ID, recipe?.id)
                        startActivity(intent)
                    }

                    this.tvFoodName.text = recipe?.name
                    this.tvFoodCal.text = getString(R.string.food_cal, recipe?.calories?.toInt())
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}