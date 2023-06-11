package academy.bangkit.capstone.dietin.ui.main_screen.home

import academy.bangkit.capstone.dietin.MainActivity
import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.remote.model.FoodHistoryGroup
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.data.remote.model.RecipeCategory
import academy.bangkit.capstone.dietin.databinding.FragmentHomeBinding
import academy.bangkit.capstone.dietin.databinding.ItemCategoryBinding
import academy.bangkit.capstone.dietin.databinding.ItemFoodCard1Binding
import academy.bangkit.capstone.dietin.databinding.ItemUserEatBinding
import academy.bangkit.capstone.dietin.ui.food_history.AddFoodHistoryActivity
import academy.bangkit.capstone.dietin.ui.food_history.AddFoodHistoryViewModel
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import java.time.format.DateTimeFormatter
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
        setupShimmerView()
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
            Toast.makeText(requireContext(), "Search", Toast.LENGTH_SHORT).show()
        }

        setAllContent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setAllContent() = lifecycleScope.launch {

        //untuk di atas
        //dikasih greet selamat pagi atau apapun itu.. :V
        //kalau name nanti get nama User
        val greet = when(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)){
            in 0..11 -> "Selamat pagi"
            in 12..15 -> "Selamat siang"
            in 16..18 -> "Selamat sore"
            in 19..23 -> "Selamat malam"
            else -> "Halo"
        }
        val name = Utils.getUser(requireContext())?.name
        binding.tvWelcome.text = Html.fromHtml(getString(R.string.home_welcome, greet, name), HtmlCompat.FROM_HTML_MODE_LEGACY)
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
            else -> R.color.success
        }
        binding.caloriesProgress.setIndicatorColor(ContextCompat.getColor(requireContext(), color))

        //set color for calories progress
        binding.tvCaloriesTarget.text = Html.fromHtml(getString(R.string.home_calories_target, String.format(Locale.getDefault(), "%,.0f", foodCalories.recommendedCalories)), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }


    private fun setupShimmerView(){

        binding.shimmerCategory.startShimmer()
        binding.shimmerFoodList.startShimmer()
        binding.shimmerUserEat.startShimmer()

    }

    private fun setupViewModelBinding() {
        viewModel.recommendations.observe(viewLifecycleOwner) {
            binding.shimmerFoodList.stopShimmer()
            binding.shimmerFoodList.visibility = View.GONE
            Utils.setComposableFunction(binding.cvFoodList) { SetFoodList(it) }
        }

        viewModel.categories.observe(viewLifecycleOwner) {
            binding.shimmerCategory.stopShimmer()
            binding.shimmerCategory.visibility = View.GONE
            Utils.setComposableFunction(binding.cvCategoryList) { SetCategoryList(it) }
        }

        viewModel.foodCaloriesHistory.observe(viewLifecycleOwner) {
            binding.shimmerUserEat.stopShimmer()
            binding.shimmerUserEat.visibility = View.GONE
            Utils.setComposableFunction(binding.cvUserFood) { SetUserFoodHistory(it) }

            var totalCalories = 0f
            it.forEach {
                totalCalories += it.totalCalories
            }

            val foodCalories = AddFoodHistoryViewModel.FoodCalories(
                totalCalories,
                2000f // TODO: Get from userData
            )
            setCalories(foodCalories)
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
                            1 -> Pair("Makan Pagi", R.drawable.ic_eat_time_morning)
                            2 -> Pair("Makan Siang", R.drawable.ic_eat_time_afternoon)
                            3 -> Pair("Makan Malam", R.drawable.ic_eat_time_afternoon)
                            else -> Pair("Cemilan", R.drawable.ic_eat_time_morning)
                        }
                        this.tvEatTitle.text = timeData.first
//                        this.tvEatTitle.text = getString(
//                            R.string.eat_title,
//                            timeData.first
//                        )

                        // set icon, masih belum selesai untuk icon malam
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
                .padding(PaddingValues(vertical = 4.dp)),
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
                            val intent = Intent(requireContext(), AddFoodHistoryActivity::class.java)
                            intent.putExtra(AddFoodHistoryActivity.EXTRA_RECIPE, item)
                            requireContext().startActivity(intent)
                        }
                    }
                )
            }
        }
    }
}
