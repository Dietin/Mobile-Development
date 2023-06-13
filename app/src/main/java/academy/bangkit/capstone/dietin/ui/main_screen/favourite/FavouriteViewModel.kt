package academy.bangkit.capstone.dietin.ui.main_screen.favourite

import academy.bangkit.capstone.dietin.data.Result
import academy.bangkit.capstone.dietin.data.remote.model.ApiErrorResponse
import academy.bangkit.capstone.dietin.data.remote.model.FavouriteRecipe
import academy.bangkit.capstone.dietin.data.remote.service.ApiConfig
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

class FavouriteViewModel(private val application: Application): ViewModel() {

    private val _favouriteList = MutableLiveData<Result<List<FavouriteRecipe>>>()
    val favouriteList: LiveData<Result<List<FavouriteRecipe>>> = _favouriteList

    init {
        getFavourites()
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
}