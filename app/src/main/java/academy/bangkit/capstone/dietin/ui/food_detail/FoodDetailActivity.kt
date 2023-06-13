package academy.bangkit.capstone.dietin.ui.food_detail

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.data.remote.model.RecipeIngredient
import academy.bangkit.capstone.dietin.data.remote.model.RecipeSteps
import academy.bangkit.capstone.dietin.databinding.ActivityFoodDetailBinding
import academy.bangkit.capstone.dietin.databinding.ItemRecipeIngredientsBinding
import academy.bangkit.capstone.dietin.databinding.ItemRecipeStepsBinding
import academy.bangkit.capstone.dietin.ui.food_history.AddFoodHistoryActivity
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import java.util.Locale

class FoodDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodDetailBinding
    private lateinit var viewModel: FoodDetailViewModel
    private lateinit var loader: AlertDialog

    private var recipeId = 0
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeId = intent.getIntExtra(EXTRA_RECIPE_ID, 0)
        if (recipeId == 0) {
            Toast.makeText(this, "Food not found", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding = ActivityFoodDetailBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[FoodDetailViewModel::class.java]
        loader = Utils.generateLoader(this)
        setupViewModelBinding()
        setContentView(binding.root)

        binding.btnNavigateUp.setOnClickListener {
            onBackPressed()
        }

        binding.btnFavourite.setOnClickListener {
            it.isEnabled = false
            lifecycleScope.launch {
                val result = if (isFavorite) {
                    viewModel.removeFavourite()
                } else {
                    viewModel.setFavourite()
                }

                if (result) {
                    isFavorite = !isFavorite
                    setFavouriteBtnState(isFavorite)
                }
                it.isEnabled = true
            }
        }
    }

    private fun setupViewModelBinding() {
        viewModel.setRecipeId(recipeId)

        viewModel.isLoading.observe(this) {
            if (it) {
                loader.show()
            } else {
                loader.dismiss()
            }
        }

        viewModel.message.observe(this) {
            it.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.recipe.observe(this) {
            setRecipeData(it)
        }

        viewModel.isFavourite.observe(this) {
            setFavouriteBtnState(it)
            isFavorite = it
            binding.btnFavourite.isEnabled = true
        }
    }

    private fun setRecipeData(recipe: Recipe) {
        Glide.with(this)
            .load(recipe.image)
            .placeholder(R.drawable.food_placeholder)
            .into(binding.ivFoodImage)
        binding.tvFoodName.text = recipe.name
        binding.chipFoodCategory.apply {
            text = recipe.category.name
            chipBackgroundColor = ColorStateList.valueOf(recipe.category.getColourArrayAsHex())
        }

        binding.tvFoodCalories.text = String.format(Locale.getDefault(), "%.0f", recipe.calories)
        binding.tvFoodCarbs.text = "${String.format(Locale.getDefault(), "%.2f", recipe.carbs)}g"
        binding.tvFoodFats.text = "${String.format(Locale.getDefault(), "%.2f", recipe.fats)}g"
        binding.tvFoodProteins.text = "${String.format(Locale.getDefault(), "%.2f", recipe.proteins)}g"

        Utils.setComposableFunction(binding.cvIngredients) { SetListIngredients(recipe.ingredients!!) }
        Utils.setComposableFunction(binding.cvSteps) { SetListSteps(recipe.steps!!) }

        binding.btnAddToPlan.setOnClickListener {
            val intent = Intent(this, AddFoodHistoryActivity::class.java)
            intent.putExtra(AddFoodHistoryActivity.EXTRA_RECIPE, recipe)
            this.startActivity(intent)
        }
    }

    @Composable
    fun SetListIngredients(ingredients: List<RecipeIngredient>) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(ingredients) { item ->
                AndroidViewBinding(ItemRecipeIngredientsBinding::inflate) {
                    this.tvIngredientsName.text = item.ingredient.name
                }
            }
        }
    }

    @Composable
    fun SetListSteps(steps: List<RecipeSteps>) {
        Column(
            modifier = Modifier.padding(start = 8.dp, end = 16.dp)
        ) {
            steps.forEachIndexed { i, item ->
                AndroidViewBinding(ItemRecipeStepsBinding::inflate) {
                    this.tvStepsDescription.text = item.text
                    this.tvStepNumber.text = item.stepNo.toString()

                    if (i == steps.size - 1) {
                        this.dividerIngredients.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setFavouriteBtnState(state: Boolean) {
        if (state) {
            binding.btnFavourite.icon = ContextCompat.getDrawable(this, R.drawable.ic_star_filled)
        } else {
            binding.btnFavourite.icon = ContextCompat.getDrawable(this, R.drawable.ic_star_outlined)
        }
    }

    companion object {
        const val EXTRA_RECIPE_ID = "recipe_id"
    }
}