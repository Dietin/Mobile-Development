package academy.bangkit.capstone.dietin.ui.main_screen.home

import academy.bangkit.capstone.dietin.MainActivity
import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.databinding.FragmentHomeBinding
import academy.bangkit.capstone.dietin.databinding.ItemCategoryBinding
import academy.bangkit.capstone.dietin.databinding.ItemFoodCard1Binding
import academy.bangkit.capstone.dietin.databinding.ItemUserEatBinding
import academy.bangkit.capstone.dietin.utils.Utils
import academy.bangkit.capstone.dietin.utils.ViewModelFactory
import android.content.Intent
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import java.time.LocalTime
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

        binding.btnRecordFood.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            requireActivity().startActivity(intent)
        }

        binding.cvCategoryList.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {

                    //tinggal memanggil fungsi SetFoodList()
                    //dengan parameter berupa ArrayList
                    SetCategoryList(createDummyCategoriesData())
                }
            }
        }

        binding.cvUserFood.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {

                    //tinggal memanggil fungsi SetFoodList()
                    //dengan parameter berupa ArrayList
                    SetUserFoodHistory(createDummyUserFoodHistory())
                }
            }
        }


        return binding.root
    }

    private fun setupViewModelBinding() {
        viewModel.recommendations.observe(viewLifecycleOwner) {
            binding.cvFoodList.apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    MaterialTheme {

                        //tinggal memanggil fungsi SetFoodList()
                        //dengan parameter berupa ArrayList
                        SetFoodList(it)
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
        binding.tvCaloriesTarget.text = Html.fromHtml(getString(R.string.home_calories_target, caloriesNeeded))


    }



//    surat cinta untuk jolly
//    ini untuk dummy data ya Pak... nanti di hapus aja.. :)))) wkwkwwkwkwk
    private fun createDummyCategoriesData() : ArrayList<Category> {

        val categories = ArrayList<Category>()

        for(i in 1..10){
            categories.add(
                Category(
                    image = R.drawable.ic_category_plant_based,
                    title = "Contoh Category $i"
                )
            )
        }

        return categories
    }

//    surat cinta untuk jolly
//    ini untuk dummy data User Food History
    private fun createDummyUserFoodHistory() : ArrayList<UserFood>{

        val userFoodHistories = ArrayList<UserFood>()

        for(i in 1..10){
            userFoodHistories.add(
                UserFood(
                    cal = (100+i).toDouble(),
                    time = "${i+3}:00"
                )
            )
        }

        return userFoodHistories
    }


    @Composable
    fun SetUserFoodHistory(foodHistories : ArrayList<UserFood>){

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
                        this.tvCaloriesEaten.text = Html.fromHtml(getString(R.string.user_calories, item.cal.toInt()))
                        this.tvEatTime.text = getString(R.string.tv_eat_time, item.time)

                        val time = try{
                            LocalTime.parse(item.time, timeFormatter)
                        } catch (e : Exception){
                            null
                        }

                        //masih belum terlalu oke
                        this.tvEatTitle.text = getString(
                            R.string.eat_title,
                            when(time){
                                null -> "Null"
                                in LocalTime.of(0, 0)..LocalTime.of(11, 59) -> "Pagi"
                                in LocalTime.of(12, 0)..LocalTime.of(17, 59) -> "Siang"
                                in LocalTime.of(18, 0)..LocalTime.of(23, 59) -> "Malam"
                                else -> "Pagi"
                            }
                        )

                        // set icon, masih belum selesai untuk icon malam
                        this.ivEatIcon.setImageResource(
                            when(time){
                                null -> R.drawable.ic_eat_time_morning
                                in LocalTime.of(0, 0)..LocalTime.of(11, 59) -> R.drawable.ic_eat_time_morning
                                in LocalTime.of(12, 0)..LocalTime.of(17, 59) -> R.drawable.ic_eat_time_afternoon
                                in LocalTime.of(18, 0)..LocalTime.of(23, 59) -> R.drawable.ic_eat_time_afternoon
                                else -> R.drawable.ic_eat_time_morning
                            }
                        )



                    }
                )
            }
        }
    }


    @Composable
    fun SetCategoryList(categories : ArrayList<Category>){

        LazyRow(
            modifier = Modifier
                .padding(PaddingValues(vertical = 4.dp)),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(space = 16.dp)

        ) {
            items(categories){ item ->
                AndroidViewBinding(
                    factory = { layoutInflater, parent, _ ->
                        ItemCategoryBinding.inflate(layoutInflater, parent, false)
                    },

                    update = {
                        this.ivCategoryImage.setImageResource(item.image)
                        this.tvCategoryTitle.text = item.title


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
                        this.chipFoodCategory.text = item.category.name

                        //nanti set colornya juga

                        this.tvFoodName.text = item.name
                        this.tvFoodCal.text = getString(R.string.food_cal, item.calories.toInt())
                    }
                )
            }
        }

    }

}

//surat cinta untuk jolly
//ini data sementara jol.. nanti di buatin lah.. :v
data class Category(
    val image : Int,
    val title : String
)

//surat cinta untuk jolly
//ini data sementara, intinya ada waktu dia makan dan kalorinya berapa
data class UserFood(
    val time : String,
    val cal : Double,
)
