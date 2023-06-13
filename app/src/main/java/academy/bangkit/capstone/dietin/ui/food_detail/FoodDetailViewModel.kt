package academy.bangkit.capstone.dietin.ui.food_detail

import academy.bangkit.capstone.dietin.data.remote.model.ApiErrorResponse
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import academy.bangkit.capstone.dietin.data.remote.service.ApiConfig
import academy.bangkit.capstone.dietin.di.Injection
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

class FoodDetailViewModel(private val application: Application): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _recipe = MutableLiveData<Recipe>()
    val recipe: LiveData<Recipe> = _recipe

    private val _isFavourite = MutableLiveData<Boolean>()
    val isFavourite: LiveData<Boolean> = _isFavourite

    private var recipeId = 0

    suspend fun setFavourite() = Injection.provideRepository(application).addFavourite(recipeId)
    suspend fun removeFavourite() = Injection.provideRepository(application).removeFavourite(recipeId)

    private suspend fun addToSearchHistory() = Injection.provideRepository(application).addSearchHistory(recipeId)

    fun setRecipeId(recipeId: Int) {
        if (this.recipeId == 0) {
            this.recipeId = recipeId
            getRecipeDetail()
        }

        viewModelScope.launch {
            _isFavourite.value = Injection.provideRepository(application).checkIsFavourite(recipeId) != null
        }
    }

    private fun getRecipeDetail() = viewModelScope.launch {
        try {
            _isLoading.value = true
            val token = Utils.getToken(application)
            _recipe.value = ApiConfig.getApiService().getRecipeDetail(
                token = "Bearer $token",
                id = recipeId
            ).data!!
            addToSearchHistory()
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