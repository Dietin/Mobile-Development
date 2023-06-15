package academy.bangkit.capstone.dietin.ui.food_history

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.remote.model.FoodHistory
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.databinding.ActivityAddFoodHistoryBinding
import academy.bangkit.capstone.dietin.ui.main_screen.MainScreenActivity
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class AddFoodHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFoodHistoryBinding
    private lateinit var viewModel: AddFoodHistoryViewModel
    private lateinit var loader: AlertDialog

    private lateinit var recipe: Recipe
    private var maxCalories = 2010.0f
    private var currentCalories = 1620.0f
    private var jumlahPorsi = 1f
    private var isFavorite = false

    private var selectedDate = Calendar.getInstance()
    private var selectedWaktuMakan = 0

    private lateinit var listWaktuMakan : HashMap<Int, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodHistoryBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[AddFoodHistoryViewModel::class.java]

        setupViewModelBinding()
        loader = Utils.generateLoader(this)
        setContentView(binding.root)

        listWaktuMakan = hashMapOf(
            1 to getString(R.string.title_breakfast),
            2 to getString(R.string.title_lunch),
            3 to getString(R.string.title_dinner),
            4 to getString(R.string.title_snacks)
        )

        val recipeIntent = intent.getParcelableExtra<Recipe>(EXTRA_RECIPE)
        if (recipeIntent != null) {
            setupData(recipeIntent)
        } else {
            finish()
        }

        binding.btnNavigateUp.setOnClickListener {
            onBackPressed()
        }

        binding.btnAddFood.setOnClickListener {
            if (jumlahPorsi == 0f) {
                Toast.makeText(this, getString(R.string.afh_error_num_portions), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val food = FoodHistory(
                id = 0,
                userId = 0, // will be set in viewModel (seharusnya di backend)
                recipeId = recipe.id.toString(),
                calories = recipe.calories * jumlahPorsi,
                carbs = recipe.carbs * jumlahPorsi,
                proteins = recipe.proteins * jumlahPorsi,
                fats = recipe.fats * jumlahPorsi,
                date = Utils.formatDate(selectedDate.time, "yyyy-MM-dd"),
                time = selectedWaktuMakan
            )
            viewModel.addFoodHistory(food)
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

    @SuppressLint("SetTextI18n")
    private fun setupData(recipe: Recipe) {
        this.recipe = recipe

        viewModel.setRecipeId(recipe.id)

        // Initialize UI
        Glide.with(this)
            .load(recipe.image)
            .placeholder(R.drawable.food_placeholder)
            .into(binding.ivFoodImage)
        binding.tvFoodName.text = recipe.name
        binding.tvFoodShortDesc.text = Html.fromHtml(getString(R.string.afh_desc, recipe.calories, recipe.category.name), Html.FROM_HTML_MODE_COMPACT)

        setTanggalMakan(Calendar.getInstance())
        setUpWaktu()

        // Set kandungan saji
        val totalKandungan = recipe.carbs + recipe.fats + recipe.proteins
        val percCarbs = recipe.carbs / totalKandungan
        val percFats = recipe.fats / totalKandungan
        val percProteins = recipe.proteins / totalKandungan

        getLayoutParamsLL(binding.llCarbs).weight = percCarbs
        getLayoutParamsLL(binding.llFats).weight = percFats
        getLayoutParamsLL(binding.llProteins).weight = percProteins

        binding.tvCarbsPerc.text = "${String.format(Locale.getDefault(), "%.0f", percCarbs * 100)}%"
        binding.tvFatsPerc.text = "${String.format(Locale.getDefault(), "%.0f", percFats * 100)}%"
        binding.tvProteinsPerc.text = "${String.format(Locale.getDefault(), "%.0f", percProteins * 100)}%"

        binding.sliderJumlahPorsi.addOnChangeListener { _, value, _ ->
            setCalories(value)
        }

        // Calendar
        binding.tilTanggal.editText?.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(this, {
                _, _year, _monthOfYear, _dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate.set(_year, _monthOfYear, _dayOfMonth)
                setTanggalMakan(newDate)
            }, year, month, dayOfMonth)
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.datePicker.updateDate(
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    private fun getLayoutParamsLL(view: View): LinearLayout.LayoutParams {
        return view.layoutParams as LinearLayout.LayoutParams
    }

    private fun setCalories(newJumlahPorsi: Float) {
        binding.tvJumlahPorsi.text = Html.fromHtml(getString(R.string.afh_num_portions, newJumlahPorsi), Html.FROM_HTML_MODE_COMPACT)
        jumlahPorsi = newJumlahPorsi

        // Set kebutuhan kalori harian
        val additionalCaloriesPerc = (recipe.calories * newJumlahPorsi) / maxCalories

        binding.lpiKaloriAkanDipenuhi.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, additionalCaloriesPerc)

        val currentCaloriesPerc = currentCalories / maxCalories
        if (currentCaloriesPerc + additionalCaloriesPerc > 1.0f) {
            binding.cvKaloriBerlebihan.visibility = View.VISIBLE
        } else {
            binding.cvKaloriBerlebihan.visibility = View.GONE
        }

        binding.tvJumlahKaloriTerpenuhi.text = Html.fromHtml(getString(R.string.afh_cal_desc, currentCalories + (recipe.calories * newJumlahPorsi), maxCalories), Html.FROM_HTML_MODE_COMPACT)
    }

    private fun setTanggalMakan(newDate: Calendar) {
        selectedDate = newDate
        binding.tilTanggal.editText?.setText(Utils.formatDate(selectedDate.time, "dd MMMM yyyy"))

        viewModel.getCalories(Utils.formatDate(selectedDate.time, "yyyy-MM-dd"))
    }

    private fun setUpWaktu() {
        val adapterWaktu = ArrayAdapter(this, android.R.layout.simple_list_item_1, listWaktuMakan.values.toTypedArray())

        // Init selected waktu makan: berdasarkan jam
        selectedWaktuMakan = when(selectedDate.get(Calendar.HOUR_OF_DAY)) {
            in 0..10 -> 1
            in 11..16 -> 2
            in 17..23 -> 3
            else -> 1
        }

        binding.actvWaktu.apply {
            setAdapter(adapterWaktu)
            setOnItemClickListener { _, _, position, _ ->
                selectedWaktuMakan = position + 1
            }
        }

        binding.actvWaktu.setText(adapterWaktu.getItem(selectedWaktuMakan - 1).toString(), false)
    }

    private fun setupViewModelBinding() {
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

        viewModel.isSuccess.observe(this) {
            if (it) {
                Toast.makeText(this, getString(R.string.afh_success_add_food), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainScreenActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        viewModel.calories.observe(this) {
            currentCalories = it.currentCalories
            maxCalories = it.recommendedCalories

            setCalories(jumlahPorsi)

            val currentCaloriesPerc = currentCalories / maxCalories
            getLayoutParamsLL(binding.lpiKaloriTerpenuhi).weight = currentCaloriesPerc
        }

        viewModel.isFavourite.observe(this) {
            setFavouriteBtnState(it)
            isFavorite = it
            binding.btnFavourite.isEnabled = true
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
        const val EXTRA_RECIPE = "extra_recipe"
    }
}