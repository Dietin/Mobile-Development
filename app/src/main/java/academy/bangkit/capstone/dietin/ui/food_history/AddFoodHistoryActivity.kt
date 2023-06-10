package academy.bangkit.capstone.dietin.ui.food_history

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.remote.model.FoodHistory
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.databinding.ActivityAddFoodHistoryBinding
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
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

    private var selectedDate = Calendar.getInstance()
    private var selectedWaktuMakan = 0

    private val listWaktuMakan = hashMapOf(
        1 to "Makan Pagi",
        2 to "Makan Siang",
        3 to "Makan Malam",
        4 to "Cemilan"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFoodHistoryBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[AddFoodHistoryViewModel::class.java]
        setupViewModelBinding()
        loader = Utils.generateLoader(this)
        setContentView(binding.root)

        binding.btnNavigateUp.setOnClickListener {
            onBackPressed()
        }

        binding.btnAddFood.setOnClickListener {
            if (jumlahPorsi == 0f) {
                Toast.makeText(this, "Jumlah porsi tidak boleh 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val food = FoodHistory(
                id = 0,
                userId = 0, // will be set in viewModel (seharusnya di backend)
                recipeId = recipe.id.toString(), // TODO: Update database type to Int
                calories = recipe.calories * jumlahPorsi,
                carbs = recipe.carbs * jumlahPorsi,
                proteins = recipe.proteins * jumlahPorsi,
                fats = recipe.fats * jumlahPorsi,
                date = Utils.formatDate(selectedDate.time, "yyyy-MM-dd"),
                time = selectedWaktuMakan
            )
            viewModel.addFoodHistory(food)
        }

        val recipeIntent = intent.getParcelableExtra<Recipe>(EXTRA_RECIPE)
        if (recipeIntent != null) {
            setupData(recipeIntent)
        } else {
            Toast.makeText(this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupData(recipe: Recipe) {
        this.recipe = recipe

        // Initialize UI
        Glide.with(this)
            .load(recipe.image)
            .placeholder(R.drawable.food_placeholder)
            .into(binding.ivFoodImage)
        binding.tvFoodName.text = recipe.name
        binding.tvFoodShortDesc.text = Html.fromHtml("${String.format(Locale.getDefault(), "%,.1f", recipe.calories)} kalori • ${recipe.category.name}", Html.FROM_HTML_MODE_COMPACT)

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
        binding.tvJumlahPorsi.text = Html.fromHtml("Jumlah Porsi: <b>${String.format(Locale.getDefault(), "%.2f", newJumlahPorsi)}</b>", Html.FROM_HTML_MODE_COMPACT)
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

        binding.tvJumlahKaloriTerpenuhi.text = Html.fromHtml("<b>${String.format(Locale.getDefault(), "%,.0f", currentCalories + (recipe.calories * newJumlahPorsi))}</b> dari <b>${String.format(Locale.getDefault(), "%,.0f", maxCalories)}</b> kalori terpenuhi.", Html.FROM_HTML_MODE_COMPACT)
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
                finish()
            }
        }

        viewModel.calories.observe(this) {
            currentCalories = it.currentCalories
            maxCalories = it.recommendedCalories

            setCalories(jumlahPorsi)

            val currentCaloriesPerc = currentCalories / maxCalories
            getLayoutParamsLL(binding.lpiKaloriTerpenuhi).weight = currentCaloriesPerc
        }

//        viewModel.loadingTask.observe(this) {
//            if (it <= 0) {
//                // Semua loading sudah selesai
//                loader.dismiss()
//            }
//        }
    }

    companion object {
        const val EXTRA_RECIPE = "extra_recipe"
    }
}