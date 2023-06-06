package academy.bangkit.capstone.dietin.ui.main_screen.home

import academy.bangkit.capstone.dietin.data.remote.model.ApiErrorResponse
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
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
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _recommendations = MutableLiveData<List<Recipe>>()
    val recommendations: LiveData<List<Recipe>> = _recommendations

    init {
        getAllRecommendations()
    }

    fun getAllRecommendations() = viewModelScope.launch {
        try {
            _isLoading.value = true
            val token = Utils.getToken(application)
            _recommendations.value = ApiConfig.getApiService().getRecommendations(
                token = "Bearer $token",
                page = 1,
                size = 10
            ).data!!
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
}