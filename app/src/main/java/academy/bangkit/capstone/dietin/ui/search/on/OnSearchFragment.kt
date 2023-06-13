package academy.bangkit.capstone.dietin.ui.search.on

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.Result
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.databinding.FragmentOnSearchBinding
import academy.bangkit.capstone.dietin.databinding.ItemCategoryBinding
import academy.bangkit.capstone.dietin.databinding.ItemFoodCard2Binding
import academy.bangkit.capstone.dietin.databinding.ItemFoodCard2PlaceholderBinding
import academy.bangkit.capstone.dietin.ui.food_detail.FoodDetailActivity
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide

class OnSearchFragment : Fragment() {

    private var _binding : FragmentOnSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: OnSearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnSearchBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[OnSearchViewModel::class.java]
        setupViewModelBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupViewModelBindings() {
        viewModel.message.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.searchResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    Utils.setShimmerVisibility(binding.shimmerSearchResult, false)
                    Utils.setComposableFunction(binding.cvSearchResult) { SetFoodResult(null) }
                }
                is Result.Success -> {
//                    Utils.setShimmerVisibility(binding.shimmerSearchResult, false)
                    Utils.setComposableFunction(binding.cvSearchResult) { SetFoodResult(it.data) }
                }
                is Result.Error -> {
//                    Utils.setShimmerVisibility(binding.shimmerSearchResult, false)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Composable
    fun SetFoodResult(foodList: List<Recipe>?){
        val categoriesList by viewModel.listCategories.observeAsState()

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        ) {
            item {
                LazyRow(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier
                ) {
                    item {
                        AndroidViewBinding(ItemCategoryBinding::inflate) {
                            this.ivCategoryImage.setImageResource(R.drawable.food_placeholder)
                            this.tvCategoryTitle.text = "Semua"
                        }
                    }
                    items((categoriesList as Result.Success).data) { item ->
                        AndroidViewBinding(
                            factory = { layoutInflater, parent, _ ->
                                ItemCategoryBinding.inflate(layoutInflater, parent, false)
                            },

                            update = {
                                Glide.with(this.root)
                                    .load(item.icon)
                                    .placeholder(R.drawable.food_placeholder)
                                    .into(this.ivCategoryImage)
                                this.tvCategoryTitle.text = item.name
                            }
                        )
                    }
                }
            }

            items(foodList ?: emptyList()){ item ->
                AndroidViewBinding(
                    factory = ItemFoodCard2Binding::inflate,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Glide.with(this.root)
                        .load(item.image)
                        .placeholder(R.drawable.food_placeholder)
                        .into(this.ivFoodImage)

                    this.chipFoodCategory.apply {
                        text = item.category.name
                        chipBackgroundColor = ColorStateList.valueOf(item.category.getColourArrayAsHex())
                    }

                    this.tvFoodName.text = item.name
                    this.tvFoodCal.text = getString(R.string.food_cal, item.calories.toInt())

                    this.root.setOnClickListener {
                        val intent = Intent(requireContext(), FoodDetailActivity::class.java)
                        intent.putExtra(FoodDetailActivity.EXTRA_RECIPE_ID, item.id)
                        startActivity(intent)
                    }
                }
            }

            if (foodList == null) {
                items(10) {
                    AndroidViewBinding(
                        factory = ItemFoodCard2PlaceholderBinding::inflate,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

    }

    fun updateQuery(query: String) {
        viewModel.searchGlobal(query)
    }

}