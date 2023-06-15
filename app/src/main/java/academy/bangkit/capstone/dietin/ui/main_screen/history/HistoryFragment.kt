package academy.bangkit.capstone.dietin.ui.main_screen.history

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.remote.model.FoodHistory
import academy.bangkit.capstone.dietin.databinding.FragmentHistoryBinding
import academy.bangkit.capstone.dietin.helper.ProgressBarHelper
import academy.bangkit.capstone.dietin.ui.subscription.SubscriptionActivity
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale


class HistoryFragment : Fragment() {

    private var _binding : FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HistoryViewModel
    private lateinit var loader: AlertDialog

    private lateinit var datePickerDialog: DatePickerDialog

    private var selectedDate = Calendar.getInstance()
    private var maxDate = Calendar.getInstance().timeInMillis
    private var minDate = Calendar.getInstance().timeInMillis


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[HistoryViewModel::class.java]
        setupViewModelBinding()
        loader = Utils.generateLoader(requireActivity())
        return binding.root
    }

    private fun setupViewModelBinding() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                loader.show()
            } else {
                loader.dismiss()

                // sekalian update progress bar
                val currentFoodHistory = viewModel.currentFoodHistory.value
                val currentCalories = viewModel.currentCalories.value

                val totalCaloriesEaten = currentFoodHistory?.sumOf { it.totalCalories.toDouble() }?.toFloat() ?: 0f

                val percent = totalCaloriesEaten / (currentCalories ?: 1f) * 100
                ProgressBarHelper.setProgress(binding.historyCaloriesProgress, percent)

                binding.tvCaloriesPercent.text = String.format(Locale.getDefault(), "%.0f%%", percent)
            }
        }

        viewModel.message.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.currentCalories.observe(viewLifecycleOwner) {
            setAllContent(it)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListener()
        Utils.setComposableFunction(binding.cvFoodHistory){ SetTable() }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getFoodHistory(Utils.formatDate(selectedDate.time, "yyyy-MM-dd"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setListener(){

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        datePickerDialog = DatePickerDialog(requireContext(), {
                _, _year, _monthOfYear, _dayOfMonth ->
            val newDate = Calendar.getInstance()
            newDate.set(_year, _monthOfYear, _dayOfMonth)
            setTanggal(newDate)
        }, year, month, dayOfMonth)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        // tanggal default adalah hari ini, jadi disable button next:
        binding.btnNext.apply {
            isEnabled = false
            alpha = 0.5f
        }

        binding.btnPickDate.setOnClickListener {
            datePickerDialog.datePicker.updateDate(
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        lifecycleScope.launch {
            val user = viewModel.getUser()

            val createdAt = try {
                Utils.parseDate(user?.createdAt!!, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").time
            } catch (e: Exception) {
                Calendar.getInstance().timeInMillis
            }

            datePickerDialog.datePicker.minDate = createdAt
            minDate = createdAt + (24 * 60 * 60 * 1000)
        }

        binding.btnNext.setOnClickListener {
            selectedDate.add(Calendar.DAY_OF_MONTH, 1)
            setTanggal(selectedDate)
        }

        binding.btnBack.setOnClickListener {
            selectedDate.add(Calendar.DAY_OF_MONTH, -1)
            setTanggal(selectedDate)
        }

        binding.tvTotalProteinValue.setOnClickListener {
            val subsIntent = Intent(requireContext(), SubscriptionActivity::class.java)
            startActivity(subsIntent)
        }

        binding.tvTotalFatValue.setOnClickListener {
            val subsIntent = Intent(requireContext(), SubscriptionActivity::class.java)
            startActivity(subsIntent)
        }

        binding.srlHistory.setOnRefreshListener {
            viewModel.getFoodHistory(Utils.formatDate(selectedDate.time, "yyyy-MM-dd"))
            binding.srlHistory.isRefreshing = false
        }
    }

    private fun setTanggal(newDate : Calendar){
        selectedDate = newDate
        val dateYMD = Utils.formatDate(selectedDate.time, "yyyy-MM-dd")
        binding.tvHistoryDate.text = Utils.formatDate(selectedDate.time, "dd MMMM yyyy")

        viewModel.getFoodHistory(dateYMD)

        if (newDate.timeInMillis >= maxDate){
            binding.btnNext.apply {
                isEnabled = false
                alpha = 0.5f
            }
        } else {
            binding.btnNext.apply {
                isEnabled = true
                alpha = 1f
            }
        }

        Log.e("HistoryFragment", "${newDate.timeInMillis} | $minDate | $maxDate")
        if (newDate.timeInMillis <= minDate){
            binding.btnBack.apply {
                isEnabled = false
                alpha = 0.5f
            }
        } else {
            binding.btnBack.apply {
                isEnabled = true
                alpha = 1f
            }
        }
    }

    //disini set semua content yang diperlukan
    private fun setAllContent(idealCalories: Float){

        //untuk penanggalan
        binding.tvHistoryDate.text = Utils.formatDate(selectedDate.time, "dd MMMM yyyy")

        //untuk target kalori
        binding.tvTargetCaloriesValue.text = Html.fromHtml(getString(R.string.food_cal, idealCalories), Html.FROM_HTML_MODE_COMPACT)
    }


    @SuppressLint("SetTextI18n")
    @Composable
    fun SetTable(){
        val currentFoodHistory by viewModel.currentFoodHistory.observeAsState()

        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .padding(8.dp)
        ) {
            val totalCaloriesEaten = currentFoodHistory?.sumOf { it.totalCalories.toDouble() }?.toFloat() ?: 0f

            //untuk total kalori yang sudah dikonsumsi
            binding.tvTotalCaloriesValue.text = Html.fromHtml(getString(R.string.food_cal, totalCaloriesEaten), Html.FROM_HTML_MODE_COMPACT)

            for (i in 1 .. 4) {
                val foodHistoryGroupedByTime = currentFoodHistory?.find { it.time == i }
                val eatTime = when(i){
                    1 -> getString(R.string.title_breakfast)
                    2 -> getString(R.string.title_lunch)
                    3 -> getString(R.string.title_dinner)
                    4 -> getString(R.string.title_snacks)
                    else -> ""
                }
                var percentFood = (foodHistoryGroupedByTime?.totalCalories ?: 0f) / totalCaloriesEaten
                percentFood = if (percentFood.isNaN()) 0f else percentFood

                FoodTable(
                    eatTime = eatTime,
                    percentFood = (percentFood * 100),
                    totalCalories = foodHistoryGroupedByTime?.totalCalories ?: 0f,
                    dataHistory = foodHistoryGroupedByTime?.foodHistory ?: listOf()
                )
            }
        }
    }



    @Composable
    fun FoodTable(
        eatTime : String,
        percentFood : Float,
        totalCalories : Float,
        dataHistory: List<FoodHistory>
    ){
        Card(
            elevation = 2.dp,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()

        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.gunmetal_calmer))
                        .padding(16.dp)

                ) {
                    Text(
                        text = eatTime,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.inter)),
                        color = colorResource(id = R.color.white)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "${String.format(Locale.getDefault(), "%,.1f", percentFood)}%",
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.inter)),
                        color = colorResource(id = R.color.white),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .align(Alignment.CenterVertically)
                    )

                    Text(
                        text = Html.fromHtml(getString(R.string.food_cal, totalCalories), Html.FROM_HTML_MODE_COMPACT).toString(),
                        fontSize = 12.sp,
                        fontFamily = FontFamily(Font(R.font.inter)),
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.satin_gold),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                }

                if(dataHistory.isNotEmpty()){
                    dataHistory.forEach {
                        FoodList(
                            foodName = it.recipe?.name ?: it.recipeId,
                            foodCalories = it.calories
                        )
                    }
                } else {
                    FoodList(foodName = "-", foodCalories = 0f)
                }

            }
        }
    }


    @Composable
    fun FoodList(
        foodName : String,
        foodCalories : Float
    ){
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ){
            Text(
                text = foodName,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontFamily = FontFamily(Font(R.font.inter)),
                modifier = Modifier.weight(5f)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = Html.fromHtml(getString(R.string.food_cal, foodCalories), Html.FROM_HTML_MODE_COMPACT).toString(),
                fontFamily = FontFamily(Font(R.font.inter)),
            )
        }
    }



}