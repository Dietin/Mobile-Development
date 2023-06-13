package academy.bangkit.capstone.dietin.ui.main_screen.history

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.remote.model.FoodHistory
import academy.bangkit.capstone.dietin.databinding.FragmentHistoryBinding
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Html
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
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.util.Calendar
import java.util.Locale


class HistoryFragment : Fragment() {

    private var _binding : FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HistoryViewModel
    private lateinit var loader: AlertDialog

    private var selectedDate = Calendar.getInstance()


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
            }
        }

        viewModel.message.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.userData.observe(viewLifecycleOwner) {
            setAllContent(it.idealCalories)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListener()
        Utils.setComposableFunction(binding.cvFoodHistory){ SetTable() }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setListener(){

        binding.btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(requireContext(), {
                    _, _year, _monthOfYear, _dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate.set(_year, _monthOfYear, _dayOfMonth)
                setTanggal(newDate)
            }, year, month, dayOfMonth)
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.datePicker.updateDate(
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        binding.btnNext.setOnClickListener {
            selectedDate.add(Calendar.DAY_OF_MONTH, 1)
            setTanggal(selectedDate)
        }

        binding.btnBack.setOnClickListener {
            selectedDate.add(Calendar.DAY_OF_MONTH, -1)
            setTanggal(selectedDate)
        }



    }

    private fun setTanggal(newDate : Calendar){
        selectedDate = newDate
        val dateYMD = Utils.formatDate(selectedDate.time, "yyyy-MM-dd")
        binding.tvHistoryDate.text = Utils.formatDate(selectedDate.time, "dd MMMM yyyy")

        viewModel.getFoodHistory(dateYMD)
    }

    //disini set semua content yang diperlukan
    private fun setAllContent(idealCalories: Float){

        //untuk penanggalan
        binding.tvHistoryDate.text = Utils.formatDate(selectedDate.time, "dd MMMM yyyy")

        //untuk target kalori
        binding.tvTargetCaloriesValue.text = Html.fromHtml(getString(R.string.food_cal, String.format(Locale.getDefault(), "%.0f", idealCalories)), Html.FROM_HTML_MODE_COMPACT)
    }



    @Composable
    fun SetTable(){
        val currentFoodHistory by viewModel.currentFoodHistory.observeAsState()
        val userData by viewModel.userData.observeAsState()

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val totalCaloriesEaten = currentFoodHistory?.sumOf { it.totalCalories.toDouble() }?.toFloat() ?: 0f

            //untuk total kalori yang sudah dikonsumsi
            binding.tvTotalCaloriesValue.text = Html.fromHtml(getString(R.string.food_cal, String.format(Locale.getDefault(), "%.0f", totalCaloriesEaten)), Html.FROM_HTML_MODE_COMPACT)

            //progress bar
            val percent = totalCaloriesEaten / (userData?.idealCalories ?: 1f) * 100
            binding.historyCaloriesProgress.progress = percent.toInt()

            val color = when(percent){
                in 0f .. 25f -> R.color.danger
                in 25f .. 75f -> R.color.warning
                in 75f .. 100f -> R.color.success
                else -> R.color.overflow
            }
            binding.historyCaloriesProgress.setIndicatorColor(ContextCompat.getColor(requireContext(), color))

            binding.tvCaloriesPercent.text = "${String.format(Locale.getDefault(), "%.0f", percent)}%"

            for (i in 1 .. 4) {
                val foodHistoryGroupedByTime = currentFoodHistory?.find { it.time == i }
                val eatTime = getString(R.string.food_time_text, when(i){
                    1 -> getString(R.string.txt_morning)
                    2 -> getString(R.string.txt_afternoon)
                    3 -> getString(R.string.txt_night)
                    4 -> getString(R.string.snacks)
                    else -> ""
                })
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
                        fontSize = 10.sp,
                        color = colorResource(id = R.color.white)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "${percentFood}%",
                        fontSize = 8.sp,
                        color = colorResource(id = R.color.white),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .align(Alignment.CenterVertically)
                    )

                    Text(
                        text = "${totalCalories} Kal",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.satin_gold),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                }


                //disini nanti pake foreach aja Pak...
                //di parameter belum ku tambahkan arraylistnya..
                //karna saya ga tau responsenya.. :V
                //JANGAN PAKE LAZY COLUMN YA PAK.. KARENA KALO PAKE LAZY COLUMN
                //NANTI TERIAK2 DIA.. :v
                /*
                    dataHistory.foreach{
                        FoodList(foodName = it.foodName, foodCalories = it.foodCalories)
                    }
                */
                dataHistory.forEach {
                    FoodList(
                        foodName = it.recipe?.name ?: it.recipeId,
                        foodCalories = it.calories.toInt()
                    )
                }

            }
        }
    }


    @Composable
    fun FoodList(
        foodName : String,
        foodCalories : Int
    ){
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ){
            Text(text = foodName)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "${foodCalories} Kalori")
        }
    }



}