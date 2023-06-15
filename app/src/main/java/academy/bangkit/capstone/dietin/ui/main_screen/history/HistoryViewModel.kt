package academy.bangkit.capstone.dietin.ui.main_screen.history

import academy.bangkit.capstone.dietin.data.remote.model.ApiErrorResponse
import academy.bangkit.capstone.dietin.data.remote.model.DataUser
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

class HistoryViewModel(private val application: Application): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _currentFoodHistory = MutableLiveData<List<FoodHistoryGroupedByTime>>()
    val currentFoodHistory: LiveData<List<FoodHistoryGroupedByTime>> = _currentFoodHistory

    private val _currentCalories = MutableLiveData<Float>()
    val currentCalories: LiveData<Float> = _currentCalories

    private val _requestCount = MutableLiveData(0)

    var dataUser: DataUser? = null

    init {
        _requestCount.observeForever {
            if (it == 2) {
                _isLoading.value = false
            }
        }

        viewModelScope.launch {
            dataUser = getUserData()
        }
    }

    private suspend fun getUserData() = Utils.getUserData(application)
    suspend fun getUser() = Utils.getUser(application)

    fun getFoodHistory(date: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            _requestCount.value = 0
            getCaloriesHistory(date)

            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().getFoodHistory(
                token = "Bearer $token",
                date = date
            ).data

            val groupedFoodHistory = data.groupBy { it.time }
            val groupedFoodHistoryList = mutableListOf<FoodHistoryGroupedByTime>()
            groupedFoodHistory.forEach { (time, foodHistory) ->
                groupedFoodHistoryList.add(
                    FoodHistoryGroupedByTime(
                        time = time,
                        totalCalories = foodHistory.sumOf { it.calories.toDouble() }.toFloat(),
                        foodHistory = foodHistory
                    )
                )
            }

            _currentFoodHistory.value = groupedFoodHistoryList
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            if (e.code() == 404) {
                _currentFoodHistory.value = listOf()
            } else {
                val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
                _message.value = Event(errorResponse.message)
            }
        } finally {
            _requestCount.value = _requestCount.value?.plus(1)
        }
    }

    private fun getCaloriesHistory(date: String) = viewModelScope.launch {
        try {
            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().getWeightHistory(
                token = "Bearer $token",
                date = date
            )

            _currentCalories.value = data.data?.idealCalories ?: dataUser?.idealCalories ?: 1f
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _message.value = Event(errorResponse.message)
        } finally {
            _requestCount.value = _requestCount.value?.plus(1)
        }
    }

    inner class FoodHistoryGroupedByTime(
        val time: Int,
        val totalCalories: Float,
        val foodHistory: List<FoodHistory>
    )
}