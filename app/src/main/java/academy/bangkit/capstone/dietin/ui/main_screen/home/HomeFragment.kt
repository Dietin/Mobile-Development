package academy.bangkit.capstone.dietin.ui.main_screen.home

import academy.bangkit.capstone.dietin.MainActivity
import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.Result
import academy.bangkit.capstone.dietin.data.remote.model.FoodHistoryGroup
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.data.remote.model.RecipeCategory
import academy.bangkit.capstone.dietin.databinding.FragmentHomeBinding
import academy.bangkit.capstone.dietin.databinding.ItemCategoryBinding
import academy.bangkit.capstone.dietin.databinding.ItemFoodCard1Binding
import academy.bangkit.capstone.dietin.databinding.ItemUserEatBinding
import academy.bangkit.capstone.dietin.ui.food_detail.FoodDetailActivity
import academy.bangkit.capstone.dietin.ui.food_history.AddFoodHistoryViewModel
import academy.bangkit.capstone.dietin.ui.search.RecipeSearchActivity
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var loader: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[HomeViewModel::class.java]
        setupViewModelBinding()
        loader = Utils.generateLoader(requireActivity())

        binding.btnRecordFood.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inputSearch.editText?.setOnClickListener {
            val intent = Intent(requireContext(), RecipeSearchActivity::class.java)
            startActivity(intent)
        }

        setAllContent()

        binding.root.setOnRefreshListener {
            viewModel.getAllRecommendations()
            viewModel.getCaloriesHistory()
            viewModel.getAllCategories()
            getRandomFact()
            binding.root.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setAllContent() = lifecycleScope.launch {
        val greet: String
        val timelyDesc: String
        when(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)){
            in 0..11 -> {
                greet = getString(R.string.txt_morning)
                timelyDesc = getString(R.string.msa_morning_desc)
            }
            in 12..15 -> {
                greet = getString(R.string.txt_afternoon)
                timelyDesc = getString(R.string.msa_afternoon_desc)
            }
            in 16..18 -> {
                greet = getString(R.string.txt_evening)
                timelyDesc = getString(R.string.msa_evening_desc)
            }
            in 19..23 -> {
                greet = getString(R.string.txt_night)
                timelyDesc = getString(R.string.msa_night_desc)
            }
            else -> {
                greet = getString(R.string.txt_morning)
                timelyDesc = getString(R.string.msa_morning_desc)
            }
        }
        val name = Utils.getUser(requireContext())?.name
        binding.tvWelcome.text = Html.fromHtml(getString(R.string.home_welcome, greet, name), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.tvFoodListDesc.text = timelyDesc

        getRandomFact()
    }

    private fun getRandomFact() {
        val randomFact = resources.getStringArray(R.array.diet_tips).random()
        binding.tvRandomFact.text = randomFact
    }

    private fun setCalories(foodCalories: AddFoodHistoryViewModel.FoodCalories) {
        //set percent terpenuhi dari kalori
        val percent = (foodCalories.currentCalories / foodCalories.recommendedCalories) * 100
        binding.calPercent.text = Html.fromHtml(getString(R.string.home_percent_calories, percent), HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.caloriesProgress.progress = percent.toInt()

        //ini untuk namanya.. :))
        val color = when(percent){
            in 0f .. 25f -> R.color.danger
            in 25f .. 75f -> R.color.warning
            in 75f .. 100f -> R.color.success
            else -> R.color.overflow
        }
        binding.caloriesProgress.setIndicatorColor(ContextCompat.getColor(requireContext(), color))

        //set color for calories progress
        binding.tvCaloriesTarget.text = Html.fromHtml(getString(R.string.home_calories_target, String.format(Locale.getDefault(), "%,.0f", foodCalories.recommendedCalories)), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun setupViewModelBinding() {
        viewModel.recommendations.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    Utils.setComposableFunction(binding.cvFoodList) { SetFoodList(emptyList()) }
                    Utils.setShimmerVisibility(binding.shimmerFoodList, true)
                }
                is Result.Success -> {
                    Utils.setComposableFunction(binding.cvFoodList) { SetFoodList(it.data) }
                    Utils.setShimmerVisibility(binding.shimmerFoodList, false)
                }
                is Result.Error -> {
                    Utils.setShimmerVisibility(binding.shimmerFoodList, false)
                }
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    Utils.setComposableFunction(binding.cvCategoryList) { SetCategoryList(emptyList()) }
                    Utils.setShimmerVisibility(binding.shimmerCategory, true)
                }
                is Result.Success -> {
                    Utils.setComposableFunction(binding.cvCategoryList) { SetCategoryList(it.data) }
                    Utils.setShimmerVisibility(binding.shimmerCategory, false)
                }
                is Result.Error -> {
                    Utils.setShimmerVisibility(binding.shimmerCategory, false)
                }
            }
        }

        viewModel.foodCaloriesHistory.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    Utils.setComposableFunction(binding.cvUserFood) { SetUserFoodHistory(emptyList()) }
                    Utils.setShimmerVisibility(binding.shimmerUserEat, true)
                }
                is Result.Success -> {
                    Utils.setComposableFunction(binding.cvUserFood) { SetUserFoodHistory(it.data) }
                    Utils.setShimmerVisibility(binding.shimmerUserEat, false)

                    var totalCalories = 0f
                    it.data.forEach { fhg ->
                        totalCalories += fhg.totalCalories
                    }

                    lifecycleScope.launch {
                        Log.e("totalCalories", viewModel.getUserData().toString())
                        val foodCalories = AddFoodHistoryViewModel.FoodCalories(
                            totalCalories,
                            viewModel.getUserData()?.idealCalories ?: 0f
                            )
                        setCalories(foodCalories)
                    }
                }
                is Result.Error -> {
                    Utils.setShimmerVisibility(binding.shimmerUserEat, false)
                }
            }
        }

        viewModel.message.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Triggers when user open the app or back from other activity
        viewModel.getCaloriesHistory()
    }

    @Composable
    fun SetUserFoodHistory(foodHistories: List<FoodHistoryGroup>){

        LazyRow(
            modifier = Modifier
                .padding(PaddingValues(bottom = 4.dp)),
            horizontalArrangement = Arrangement.spacedBy(space = 16.dp)

        ) {
            items(foodHistories){ item ->
                AndroidViewBinding(
                    factory = { layoutInflater, parent, _ ->
                        ItemUserEatBinding.inflate(layoutInflater, parent, false)
                    },

                    update = {
                        this.tvCaloriesEaten.text = Html.fromHtml(getString(R.string.user_calories, item.totalCalories.toInt()), HtmlCompat.FROM_HTML_MODE_LEGACY)
//                        this.tvEatTime.text = getString(R.string.tv_eat_time, item.time.toString())
                        tvEatTime.visibility = View.GONE

                        //masih belum terlalu oke
                        val timeData = when(item.time) {
                            1 -> Pair(getString(R.string.food_time_text, getString(R.string.txt_morning)), R.drawable.ic_eat_time_morning)
                            2 -> Pair(getString(R.string.food_time_text, getString(R.string.txt_afternoon)), R.drawable.ic_eat_time_afternoon)
                            3 -> Pair(getString(R.string.food_time_text, getString(R.string.txt_evening)), R.drawable.ic_eat_time_night)
                            else -> Pair(getString(R.string.snacks), R.drawable.ic_eat_time_morning)
                        }
                        this.tvEatTitle.text = timeData.first

                        this.ivEatIcon.setImageResource(timeData.second)
                    }
                )
            }
        }
    }

    @Composable
    fun SetCategoryList(categories : List<RecipeCategory>){

        LazyRow(
            modifier = Modifier
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp)

        ) {
            items(categories){ item ->
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

    @Composable
    fun SetFoodList(foodList: List<Recipe>){
        
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
                        Glide.with(this.root)
                            .load(item.image)
                            .placeholder(R.drawable.food_placeholder)
                            .into(this.ivFoodImage)
                        this.chipFoodCategory.apply {
                            text = item.category.name
                            chipBackgroundColor = ColorStateList.valueOf(item.category.getColourArrayAsHex())
                        }

                        this.tvFoodName.text = item.name
                        this.tvFoodCal.text = item.calories.toInt().toString()

                        this.root.setOnClickListener {
                            val intent = Intent(requireContext(), FoodDetailActivity::class.java)
                            intent.putExtra(FoodDetailActivity.EXTRA_RECIPE_ID, item.id)
                            startActivity(intent)
                        }
                    }
                )
            }
        }
    }
}
