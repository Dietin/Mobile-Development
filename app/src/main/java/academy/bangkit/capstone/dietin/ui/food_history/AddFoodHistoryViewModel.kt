package academy.bangkit.capstone.dietin.ui.food_history

import academy.bangkit.capstone.dietin.data.remote.model.ApiErrorResponse
import academy.bangkit.capstone.dietin.data.remote.model.FoodHistory
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

class AddFoodHistoryViewModel(private val application: Application): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _calories = MutableLiveData<FoodCalories>()
    val calories: LiveData<FoodCalories> = _calories

    // Holder: berapa banyak task lagi yang harus dikerjakan sebelum loading selesai
    private var _loadingTask = MutableLiveData<Int>(1)
    val loadingTask: LiveData<Int> = _loadingTask

    init {
        getCalories()
        
        loadingTask.observeForever {
            _isLoading.value = it > 0
        }
    }

    fun getCalories() = viewModelScope.launch {
        val recommendedCalories = 2000f
        try {
            _isLoading.value = true
            val token = Utils.getToken(application)
            val userId = Utils.getUserId(application)

            // TODO: Temporary: user id should not be stored in shared preferences
            val data = ApiConfig.getApiService().getFoodHistoryGroupedByTime(
                token = "Bearer $token",
                date = Utils.getCurrentDate(),
                userId = userId
            ).data
            var totalCalories = 0f
            data.forEach {
                totalCalories += it.totalCalories
            }

            _calories.value = FoodCalories(
                currentCalories = totalCalories,
                recommendedCalories = recommendedCalories
            )
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _message.value = Event(errorResponse.message)
        } finally {
//            _isLoading.value = false // replaced with loadingTask
            _loadingTask.value = _loadingTask.value?.minus(1)
        }
    }

    fun addFoodHistory(foodHistory: FoodHistory) = viewModelScope.launch {
        try {
            _isLoading.value = true
            val token = Utils.getToken(application)
            val userId = Utils.getUserId(application)

            // Set user id
            foodHistory.userId = userId
            ApiConfig.getApiService().addFoodHistory(
                token = "Bearer $token",
                foodHistory = foodHistory,
            )

            // Kalau sampai sini, berarti berhasil
            _isSuccess.value = true
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _message.value = Event(errorResponse.message)
        } finally {
            _isLoading.value = false
        }
    }

    inner class FoodCalories(
        val currentCalories: Float,
        val recommendedCalories: Float
    )
}