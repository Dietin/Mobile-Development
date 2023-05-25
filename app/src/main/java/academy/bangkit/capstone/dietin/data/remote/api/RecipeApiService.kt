package academy.bangkit.capstone.dietin.data.remote.api

import academy.bangkit.capstone.dietin.data.remote.model.ApiResponse
import academy.bangkit.capstone.dietin.data.remote.model.Recipe
import retrofit2.http.GET

interface RecipeApiService {
    @GET("./")
    suspend fun getRecipes(): ApiResponse<List<Recipe>>
}