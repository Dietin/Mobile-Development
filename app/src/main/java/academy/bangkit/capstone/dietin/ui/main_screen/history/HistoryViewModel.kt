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

    private val _userData = MutableLiveData<DataUser>()
    val userData: LiveData<DataUser> = _userData

    init {
        getFoodHistory(Utils.getCurrentDate())
        getUserData()
    }

    private fun getUserData() = viewModelScope.launch {
        _userData.value = Utils.getUserData(application)
    }

    fun getFoodHistory(date: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
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
            _isLoading.value = false
        }
    }

    inner class FoodHistoryGroupedByTime(
        val time: Int,
        val totalCalories: Float,
        val foodHistory: List<FoodHistory>
    )

    fun getEmptyFHGBT(time: Int) = FoodHistoryGroupedByTime(
        time = time,
        totalCalories = 0f,
        foodHistory = listOf()
    )
}