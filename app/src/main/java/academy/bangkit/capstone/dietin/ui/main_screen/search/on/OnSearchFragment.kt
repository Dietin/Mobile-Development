package academy.bangkit.capstone.dietin.ui.main_screen.search.on

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.Result
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.databinding.FragmentOnSearchBinding
import academy.bangkit.capstone.dietin.databinding.ItemFoodCard2Binding
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
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
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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

    private val listKategori = listOf(
        "Semua",
        "Makanan",
        "Minuman",
        "Cemilan",
        "Sarapan",
        "Makan Siang",
    )

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
            Utils.setComposableFunction(binding.cvSearchResult) {
                when (it) {
                    is Result.Loading -> {
                        Utils.setShimmerVisibility(binding.shimmerSearchResult, true)
                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        Utils.setShimmerVisibility(binding.shimmerSearchResult, false)
                        SetFoodResult(it.data)
                    }
                    is Result.Error -> {
                        Utils.setShimmerVisibility(binding.shimmerSearchResult, false)
                        Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Composable
    fun SetFoodResult(foodList: List<Recipe>){
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
        ) {
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier
                ) {
                    items(listKategori){ item ->
                        Button(
                            onClick = {
                                updateQuery(item)
                            },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(text = item)
                        }
                    }
                }
            }

            items(foodList){ item ->
                AndroidViewBinding(
                    factory = { layoutInflater, parent, _ ->
                        ItemFoodCard2Binding.inflate(layoutInflater, parent, false)
                    },

                    update = {
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
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

    }

    fun updateQuery(query: String) {
        viewModel.searchGlobal(query)
    }

}