package academy.bangkit.capstone.dietin.ui.main_screen.home

import academy.bangkit.capstone.dietin.data.Result
import academy.bangkit.capstone.dietin.data.remote.model.ApiErrorResponse
import academy.bangkit.capstone.dietin.data.remote.model.FoodHistoryGroup
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.data.remote.model.RecipeCategory
import academy.bangkit.capstone.dietin.data.remote.service.ApiConfig
import academy.bangkit.capstone.dietin.utils.Event
import academy.bangkit.capstone.dietin.utils.Utils
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class HomeViewModel(private val application: Application) : ViewModel() {

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _recommendations = MutableLiveData<Result<List<Recipe>>>()
    val recommendations: LiveData<Result<List<Recipe>>> = _recommendations

    private val _categories = MutableLiveData<Result<List<RecipeCategory>>>()
    val categories: LiveData<Result<List<RecipeCategory>>> = _categories

    private val _foodCaloriesHistory = MutableLiveData<Result<List<FoodHistoryGroup>>>()
    val foodCaloriesHistory: LiveData<Result<List<FoodHistoryGroup>>> = _foodCaloriesHistory

    init {
        getAllRecommendations()
        getAllCategories()
        getCaloriesHistory()
    }

    suspend fun getUserData() = Utils.getUserData(application)

    fun getAllRecommendations() = viewModelScope.launch {
        try {
            _recommendations.value = Result.Loading
            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().getRecommendations(
                token = "Bearer $token",
                page = 1,
                size = 10
            ).data
            _recommendations.value = Result.Success(data)
        } catch (e: IOException) {
            // No Internet Connection
            _recommendations.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _recommendations.value = Result.Error(errorResponse.message)
        }
    }

    fun getAllCategories() = viewModelScope.launch {
        try {
            _categories.value = Result.Loading
            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().getAllCategories(
                token = "Bearer $token"
            ).data
            _categories.value = Result.Success(data)
        } catch (e: IOException) {
            // No Internet Connection
            _categories.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _categories.value = Result.Error(errorResponse.message)
        }
    }

    fun getCaloriesHistory() = viewModelScope.launch {
        try {
            _foodCaloriesHistory.value = Result.Loading
            val token = Utils.getToken(application)
            val fch = ApiConfig.getApiService().getFoodHistoryGroupedByTime(
                token = "Bearer $token",
                date = Utils.getCurrentDate()
            ).data

            val fchFiltered = mutableListOf<FoodHistoryGroup>()
            for (i in 1 .. 4) {
                val totalCalories = fch.filter { it.time == i }.sumOf { it.totalCalories.toDouble() }.toFloat()
                fchFiltered.add(FoodHistoryGroup(
                    i,
                    totalCalories
                ))
            }

            _foodCaloriesHistory.value = Result.Success(fchFiltered)
        } catch (e: IOException) {
            // No Internet Connection
            _foodCaloriesHistory.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _foodCaloriesHistory.value = Result.Error(errorResponse.message)
        }
    }
}