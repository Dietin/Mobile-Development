package academy.bangkit.capstone.dietin.ui.main_screen.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.databinding.FragmentHistoryBinding
import academy.bangkit.capstone.dietin.utils.Utils
import android.app.DatePickerDialog
import android.text.Html
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.Calendar


class HistoryFragment : Fragment() {

    private var _binding : FragmentHistoryBinding? = null
    private val binding get() = _binding!!


    private var selectedDate = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListener()
        setAllContent()
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
        binding.tvHistoryDate.text = Utils.formatDate(selectedDate.time, "dd MMMM yyyy")
    }


    //disini set semua content yang diperlukan
    private fun setAllContent(){

        //untuk penanggalan
        binding.tvHistoryDate.text = Utils.formatDate(selectedDate.time, "dd MMMM yyyy")

        //untuk total kalori yang sudah dikonsumsi
        binding.tvTotalCaloriesValue.text = Html.fromHtml(getString(R.string.food_cal, 2000))

        //untuk target kalori
        binding.tvTargetCaloriesValue.text = Html.fromHtml(getString(R.string.food_cal, 2000))


        //progress bar
        val percent = 50
        binding.historyCaloriesProgress.progress = percent

        val color = when(percent.toFloat()){
            in 0f .. 25f -> R.color.danger
            in 25f .. 75f -> R.color.warning
            in 75f .. 100f -> R.color.success
            else -> R.color.overflow
        }
        binding.historyCaloriesProgress.setIndicatorColor(ContextCompat.getColor(requireContext(), color))


    }



    @Composable
    fun SetTable(){
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FoodTable(eatTime = "Makan Pagi", 50, 1500)
            FoodTable(eatTime = "Makan Siang", 20, 200)
            FoodTable(eatTime = "Makan Malam", 20, 300)
        }
    }



    @Composable
    fun FoodTable(
        eatTime : String,
        percentFood : Int,
        totalCalories : Int,
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
                        fontSize = resources.getDimension(R.dimen.text_size_sub4).sp,
                        color = colorResource(id = R.color.white)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "${percentFood}%",
                        fontSize = resources.getDimension(R.dimen.text_size_sub5).sp,
                        color = colorResource(id = R.color.white),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .align(Alignment.CenterVertically)
                    )

                    Text(
                        text = "${totalCalories} Kal",
                        fontSize = resources.getDimension(R.dimen.text_size_sub5).sp,
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
                FoodList(foodName = "nasi goreng", foodCalories = 200)
                FoodList(foodName = "nasi goreng", foodCalories = 200)

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