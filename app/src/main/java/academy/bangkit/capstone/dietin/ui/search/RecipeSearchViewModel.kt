package academy.bangkit.capstone.dietin.ui.search

import academy.bangkit.capstone.dietin.data.Result
import academy.bangkit.capstone.dietin.data.remote.model.ApiErrorResponse
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.data.remote.service.ApiConfig
import academy.bangkit.capstone.dietin.utils.Event
import academy.bangkit.capstone.dietin.utils.Utils
import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RecipeSearchViewModel(private val application: Application): ViewModel() {

    private val _message = MutableLiveData<Event<String>>()
    val message = _message

    private val _searchResult = MutableLiveData<Result<List<Recipe>>>()
    val searchResult = _searchResult

    fun searchGlobal(query: String) = viewModelScope.launch {
        try {
            _searchResult.value = Result.Loading
            val token = Utils.getToken(application)
            _searchResult.value = Result.Success(
                ApiConfig.getApiService().searchGlobal(
                token = "Bearer $token",
                query = query
            ).data)
        } catch (e: IOException) {
            // No Internet Connection
            _searchResult.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _searchResult.value = Result.Error(errorResponse.message)
        }
    }
}