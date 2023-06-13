package academy.bangkit.capstone.dietin.data.remote

import academy.bangkit.capstone.dietin.data.remote.model.FavouriteRecipe
import academy.bangkit.capstone.dietin.data.remote.service.ApiConfig
import academy.bangkit.capstone.dietin.utils.Utils
import android.app.Application
import android.util.Log

class ApplicationRepository private constructor(private val application: Application) {

    suspend fun addFavourite(recipeId: Int): Boolean {
        try {
            val token = Utils.getToken(application)
            ApiConfig.getApiService().addFavouriteRecipe(
                token = "Bearer $token",
                recipeId = recipeId
            )
            return true
        } catch (e: Exception) {
            Log.e(TAG, "addFavourite: ${e.message}")
            return false
        }
    }

    suspend fun removeFavourite(recipeId: Int): Boolean {
        try {
            val token = Utils.getToken(application)
            ApiConfig.getApiService().deleteFavouriteRecipe(
                token = "Bearer $token",
                recipeId = recipeId
            )
            return true
        } catch (e: Exception) {
            Log.e(TAG, "removeFavourite: ${e.message}")
            return false
        }
    }

    suspend fun checkIsFavourite(recipeId: Int): FavouriteRecipe? {
        try {
            val token = Utils.getToken(application)
            return ApiConfig.getApiService().checkIsFavourite(
                token = "Bearer $token",
                recipeId = recipeId
            ).data
        } catch (e: Exception) {
            Log.e(TAG, "checkIsFavourite: ${e.message}")
            return null
        }
    }

    suspend fun addSearchHistory(recipeId: Int): Boolean {
        try {
            val token = Utils.getToken(application)
            ApiConfig.getApiService().addSearchHistory(
               token = "Bearer $token",
               recipeId = recipeId
            )
            return true
        } catch (e: Exception) {
            Log.e(TAG, "addSearchHistory: ${e.message}")
            return false
        }
    }

    suspend fun removeSearchHistory(): Boolean {
        try {
            val token = Utils.getToken(application)
            ApiConfig.getApiService().deleteAllSearchHistory(
                token = "Bearer $token"
            )
            return true
        } catch (e: Exception) {
            Log.e(TAG, "removeSearchHistory: ${e.message}")
            return false
        }
    }

    companion object {
        private const val TAG = "ApplicationRepository"

        @Volatile
        private var instance: ApplicationRepository? = null

        fun getInstance(application: Application): ApplicationRepository =
            instance ?: synchronized(this) {
                instance ?: ApplicationRepository(application)
            }
    }
}