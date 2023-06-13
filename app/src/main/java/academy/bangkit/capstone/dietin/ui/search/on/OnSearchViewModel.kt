package academy.bangkit.capstone.dietin.ui.search.on

import academy.bangkit.capstone.dietin.R
import academy.bangkit.capstone.dietin.data.Result
import academy.bangkit.capstone.dietin.data.remote.model.ApiErrorResponse
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.data.remote.model.RecipeCategory
import academy.bangkit.capstone.dietin.data.remote.service.ApiConfig
import academy.bangkit.capstone.dietin.utils.Event
import academy.bangkit.capstone.dietin.utils.Utils
import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class OnSearchViewModel(private val application: Application): ViewModel() {

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _searchResult = MutableLiveData<Result<List<Recipe>>>()
    val searchResult: LiveData<Result<List<Recipe>>> = _searchResult

    private val _listCategories = MutableLiveData<Result<List<RecipeCategory>>>()
    val listCategories: LiveData<Result<List<RecipeCategory>>> = _listCategories

    val selectedCategory = mutableStateOf("0")

    init {
        getCategories()
    }

    private var searchQuery = ""
    fun searchGlobal(query: String = searchQuery) = viewModelScope.launch {
        try {
            _searchResult.value = Result.Loading
            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().searchGlobal(
                token = "Bearer $token",
                query = query,
                category = selectedCategory.value
            ).data
            searchQuery = query
            _searchResult.value = Result.Success(data)
        } catch (e: IOException) {
            // No Internet Connection
            _searchResult.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _searchResult.value = Result.Error(errorResponse.message)
        }
    }

    private fun getCategories() = viewModelScope.launch {
        try {
            _listCategories.value = Result.Loading
            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().getAllCategories(
                token = "Bearer $token"
            ).data.toMutableList()

            // All category:
            data.add(0, RecipeCategory(
                id = "0",
                name = application.getString(R.string.rs_all_category),
                icon = "",
                _colourArray = "0,0,0"
            ))
            _listCategories.value = Result.Success(data)
        } catch (e: IOException) {
            // No Internet Connection
            _listCategories.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _listCategories.value = Result.Error(errorResponse.message)
        }
    }
}