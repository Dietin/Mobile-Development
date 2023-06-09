package academy.bangkit.capstone.dietin.ui.main_screen.home

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.remote.model.FoodHistoryGroup
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.data.remote.model.RecipeCategory
import academy.bangkit.capstone.dietin.databinding.FragmentHomeBinding
import academy.bangkit.capstone.dietin.databinding.ItemCategoryBinding
import academy.bangkit.capstone.dietin.databinding.ItemFoodCard1Binding
import academy.bangkit.capstone.dietin.databinding.ItemUserEatBinding
import academy.bangkit.capstone.dietin.ui.food_history.AddFoodHistoryActivity
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
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import java.time.format.DateTimeFormatter

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

        return binding.root
    }

    private fun setupViewModelBinding() {
        viewModel.recommendations.observe(viewLifecycleOwner) {
            binding.cvFoodList.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    MaterialTheme {
                        SetFoodList(it)
                    }
                }
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) {
            binding.cvCategoryList.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    MaterialTheme {
                        SetCategoryList(it)
                    }
                }
            }
        }

        viewModel.foodCaloriesHistory.observe(viewLifecycleOwner) {
            binding.cvUserFood.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    MaterialTheme {
                        SetUserFoodHistory(it)
                    }
                }
            }
        }

        viewModel.message.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAllContent()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setAllContent(){

        //untuk di atas
        //dikasih greet selamat pagi atau apapun itu.. :V
        //kalau name nanti get nama User
        val greet = "Selamat Pagi"
        val name = "Deez Nuts"
        binding.tvWelcome.text = Html.fromHtml(getString(R.string.home_welcome, greet, name))

        //set percent terpenuhi dari kalori
        val percent = 80
        binding.calPercent.text = Html.fromHtml(getString(R.string.home_percent_calories, percent))
        binding.caloriesProgress.progress = percent

        //ini untuk namanya.. :))
        binding.caloriesProgress.setIndicatorColor(
            ContextCompat.getColor(requireContext(), when(percent){
                in 0..25 -> R.color.danger
                in 26..75 -> R.color.warning
                in 76..100 -> R.color.success
                else -> R.color.danger
            })
        )

        //set color for calories progress
        val caloriesNeeded = 9110
        binding.tvCaloriesTarget.text = Html.fromHtml(getString(R.string.home_calories_target, caloriesNeeded), HtmlCompat.FROM_HTML_MODE_LEGACY)


    }

    @Composable
    fun SetUserFoodHistory(foodHistories: List<FoodHistoryGroup>){

        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        LazyRow(
            modifier = Modifier
                .padding(PaddingValues(bottom = 4.dp)),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 16.dp)

        ) {
            items(foodHistories){ item ->
                AndroidViewBinding(
                    factory = { layoutInflater, parent, _ ->
                        ItemUserEatBinding.inflate(layoutInflater, parent, false)
                    },

                    update = {
                        this.tvCaloriesEaten.text = Html.fromHtml(getString(R.string.user_calories, item.totalCalories.toInt()), HtmlCompat.FROM_HTML_MODE_LEGACY)
                        this.tvEatTime.text = getString(R.string.tv_eat_time, item.time.toString())

                        //masih belum terlalu oke
                        val timeData = when(item.time) {
                            1 -> Pair("Pagi", R.drawable.ic_eat_time_morning)
                            2 -> Pair("Siang", R.drawable.ic_eat_time_afternoon)
                            3 -> Pair("Malam", R.drawable.ic_eat_time_afternoon)
                            else -> Pair("Snack", R.drawable.ic_eat_time_morning)
                        }
                        this.tvEatTitle.text = getString(
                            R.string.eat_title,
                            timeData.first
                        )

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
