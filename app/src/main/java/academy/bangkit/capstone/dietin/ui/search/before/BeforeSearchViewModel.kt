package academy.bangkit.capstone.dietin.ui.search.before

import academy.bangkit.capstone.dietin.data.Result
import academy.bangkit.capstone.dietin.data.remote.model.ApiErrorResponse
import academy.bangkit.capstone.dietin.data.remote.model.FavouriteRecipe
import academy.bangkit.capstone.dietin.data.remote.model.SearchHistory
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

class BeforeSearchViewModel(private val application: Application): ViewModel() {

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _favouriteList = MutableLiveData<Result<List<FavouriteRecipe>>>()
    val favouriteList: LiveData<Result<List<FavouriteRecipe>>> = _favouriteList

    private val _searchHistory = MutableLiveData<Result<List<SearchHistory>>>()
    val searchHistory: LiveData<Result<List<SearchHistory>>> = _searchHistory

    init {
        getFavourites()
        getSearchHistory()
    }

    fun getFavourites() = viewModelScope.launch {
        try {
            _favouriteList.value = Result.Loading
            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().getFavouriteRecipes(
                token = "Bearer $token"
            ).data
            _favouriteList.value = Result.Success(data)
        } catch (e: IOException) {
            // No Internet Connection
            _favouriteList.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _favouriteList.value = Result.Error(errorResponse.message)
        }
    }

    fun getSearchHistory() = viewModelScope.launch {
        try {
            _searchHistory.value = Result.Loading
            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().getSearchHistory(
                token = "Bearer $token"
            ).data
            _searchHistory.value = Result.Success(data)
        } catch (e: IOException) {
            // No Internet Connection
            _searchHistory.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _searchHistory.value = Result.Error(errorResponse.message)
        }
    }

    fun deleteSearchHistory() = viewModelScope.launch {
        try {
            _searchHistory.value = Result.Loading
            val token = Utils.getToken(application)
            ApiConfig.getApiService().deleteAllSearchHistory(
                token = "Bearer $token"
            )
            getSearchHistory()
        } catch (e: IOException) {
            // No Internet Connection
            _searchHistory.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _searchHistory.value = Result.Error(errorResponse.message)
        }
    }
}